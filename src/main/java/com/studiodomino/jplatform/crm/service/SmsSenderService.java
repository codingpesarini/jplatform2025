package com.studiodomino.jplatform.crm.service;

import com.studiodomino.jplatform.shared.config.Configurazione;
import com.studiodomino.jplatform.shared.entity.Site;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
@Slf4j
public class SmsSenderService {

    private static final String DEFAULT_SMS_URL =
            "https://gateway.skebby.it/api/send/smseasy/advanced/http.php";

    public void inviaSms(String telefono, String testo, String tipoMittente, Configurazione config) {
        if (config == null || config.getSito() == null) {
            throw new RuntimeException("Configurazione sito assente");
        }

        Site sito = config.getSito();

        String smsUrl = nvl(sito.getSmsUrl()).trim();
        String username = nvl(sito.getSmsLogin()).trim();
        String password = nvl(sito.getSmsPassword()).trim();
        String sender = nvl(sito.getSmsSender()).trim();

        if (smsUrl.isBlank()) {
            smsUrl = DEFAULT_SMS_URL;
        }

        String numeroNormalizzato = normalizzaNumero(telefono);
        if (numeroNormalizzato == null) {
            throw new RuntimeException("Numero destinatario non valido");
        }

        String testoPulito = nvl(testo).trim();
        if (testoPulito.isBlank()) {
            throw new RuntimeException("Testo SMS assente");
        }

        if (username.isBlank()) {
            throw new RuntimeException("smsLogin assente in configurazione");
        }

        if (password.isBlank()) {
            throw new RuntimeException("smsPassword assente in configurazione");
        }

        String smsType = mappaTipo(tipoMittente);

        try {
            HttpClient httpClient = buildHttpClient(sito);

            String body = buildFormBody(
                    smsType,
                    username,
                    password,
                    numeroNormalizzato,
                    testoPulito,
                    sender
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(smsUrl))
                    .timeout(Duration.ofSeconds(15))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();

            log.info("Invio SMS URI={}", smsUrl);
            log.info("Invio SMS a={} tipo={} sender={}", numeroNormalizzato, smsType, sender);

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = nvl(response.body());

            log.info("Gateway SMS status={} body={}", response.statusCode(), responseBody);

            if (response.statusCode() == 504) {
                throw new RuntimeException("Gateway SMS non ha risposto in tempo (HTTP 504)");
            }

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "Invio SMS fallito. HTTP " + response.statusCode() + " - " + responseBody
                );
            }

            if (responseBody.contains("status=success") || responseBody.contains("OK")) {
                log.info("SMS inviato correttamente a={}", numeroNormalizzato);
                return;
            }

            throw new RuntimeException("Invio SMS non confermato dal gateway: " + responseBody);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Invio SMS interrotto: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("Errore tecnico invio SMS: " + e.getMessage(), e);
        }
    }

    private HttpClient buildHttpClient(Site sito) {
        HttpClient.Builder builder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10));

        if ("1".equals(nvl(sito.getProxy()))) {
            String proxyHost = nvl(sito.getProxyip()).trim();
            String proxyPort = nvl(sito.getProxyport()).trim();
            String proxyUser = nvl(sito.getProxyuser()).trim();
            String proxyPassword = nvl(sito.getProxypassword()).trim();

            if (!proxyHost.isBlank() && !proxyPort.isBlank()) {
                builder.proxy(ProxySelector.of(
                        new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort))
                ));

                if (!proxyUser.isBlank()) {
                    builder.authenticator(new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(
                                    proxyUser,
                                    proxyPassword.toCharArray()
                            );
                        }
                    });
                }
            }
        }

        return builder.build();
    }

    private String buildFormBody(
            String smsType,
            String username,
            String password,
            String recipient,
            String text,
            String sender
    ) {
        StringBuilder sb = new StringBuilder();

        addParam(sb, "method", smsType);
        addParam(sb, "username", username);
        addParam(sb, "password", password);

        if (!sender.isBlank()) {
            addParam(sb, "sender_string", sender);
        }

        addParam(sb, "recipients[]", recipient);
        addParam(sb, "text", text);

        return sb.toString();
    }

    private void addParam(StringBuilder sb, String key, String value) {
        if (sb.length() > 0) {
            sb.append("&");
        }

        sb.append(URLEncoder.encode(key, StandardCharsets.UTF_8));
        sb.append("=");
        sb.append(URLEncoder.encode(nvl(value), StandardCharsets.UTF_8));
    }

    private String normalizzaNumero(String numero) {
        if (numero == null || numero.isBlank()) {
            return null;
        }

        String pulito = numero.replaceAll("[^\\d]", "");

        if (pulito.length() < 8) {
            return null;
        }

        if (pulito.length() < 11) {
            return "39" + pulito;
        }

        return pulito;
    }

    private String mappaTipo(String tipoMittente) {
        String valore = nvl(tipoMittente).trim().toUpperCase();

        return switch (valore) {
            case "GP" -> "send_sms_classic";
            case "TI" -> "send_sms_classic_report";
            case "SI" -> "send_sms_basic";
            default -> "send_sms_basic";
        };
    }

    private String nvl(String s) {
        return s == null ? "" : s;
    }
}