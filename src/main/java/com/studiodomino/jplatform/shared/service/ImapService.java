package com.studiodomino.jplatform.shared.service;

import com.studiodomino.jplatform.shared.entity.Account;
import com.studiodomino.jplatform.shared.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMultipart;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImapService {

    private final AccountRepository accountRepository;

    public List<Map<String, Object>> leggiInbox(Integer idAccount, String folderName) {
        List<Map<String, Object>> risultati = new ArrayList<>();

        try {
            Account account = accountRepository.findById(idAccount)
                    .orElseThrow(() -> new RuntimeException("Account non trovato: " + idAccount));

            String host     = account.getEmailInServer();
            int    porta    = account.getEmailInServerPort() != null ? account.getEmailInServerPort() : 110;
            String user     = account.getEmailUser();
            String password = account.getEmailPassword();
            String tipo     = account.getEmailInServerType() != null ? account.getEmailInServerType().toUpperCase() : "POP3";

            Properties props = new Properties();
            Store store;

            if (tipo.contains("IMAP")) {
                if (porta == 993) {
                    props.put("mail.store.protocol", "imaps");
                    props.put("mail.imaps.host", host);
                    props.put("mail.imaps.port", porta);
                    props.put("mail.imaps.ssl.enable", "true");
                    Session session = creaSessioneSenzaSSLCheck(props);
                    store = session.getStore();
                } else {
                    props.put("mail.store.protocol", "imap");
                    props.put("mail.imap.host", host);
                    props.put("mail.imap.port", porta);
                    Session session = Session.getInstance(props);
                    store = session.getStore();
                }
            } else {
                // POP3
                if (porta == 995) {
                    props.put("mail.store.protocol", "pop3s");
                    props.put("mail.pop3s.host", host);
                    props.put("mail.pop3s.port", porta);
                    props.put("mail.pop3s.ssl.enable", "true");
                } else {
                    props.put("mail.store.protocol", "pop3");
                    props.put("mail.pop3.host", host);
                    props.put("mail.pop3.port", porta);
                }
                Session session = Session.getInstance(props);
                store = session.getStore();
            }

            store.connect(host, user, password);

            // POP3 supporta solo INBOX
            String fn = tipo.contains("IMAP") && folderName != null && !folderName.isBlank()
                    ? folderName : "INBOX";

            Folder folder = store.getFolder(fn);
            folder.open(Folder.READ_ONLY);

            int total = folder.getMessageCount();
            int start = Math.max(1, total - 49);

            Message[] messages = folder.getMessages(start, total);
            for (int i = messages.length - 1; i >= 0; i--) {
                Message msg = messages[i];
                Map<String, Object> m = new HashMap<>();
                m.put("id",       msg.getMessageNumber());
                m.put("oggetto",  msg.getSubject() != null ? msg.getSubject() : "(senza oggetto)");
                m.put("mittente", msg.getFrom() != null && msg.getFrom().length > 0 ? msg.getFrom()[0].toString() : "");
                m.put("data",     msg.getSentDate());
                m.put("letto",    msg.isSet(Flags.Flag.SEEN));
                m.put("testo",    estraiTesto(msg));
                risultati.add(m);
            }

            folder.close(false);
            store.close();

        } catch (Exception e) {
            log.error("Errore lettura account idAccount={} folder={}", idAccount, folderName, e);
            throw new RuntimeException("Errore connessione: " + e.getMessage(), e);
        }

        return risultati;
    }

    private String estraiTesto(Message msg) {
        try {
            Object content = msg.getContent();
            if (content instanceof String) return (String) content;
            if (content instanceof MimeMultipart mp) {
                for (int i = 0; i < mp.getCount(); i++) {
                    BodyPart part = mp.getBodyPart(i);
                    if (part.isMimeType("text/plain")) return (String) part.getContent();
                }
                for (int i = 0; i < mp.getCount(); i++) {
                    BodyPart part = mp.getBodyPart(i);
                    if (part.isMimeType("text/html")) return (String) part.getContent();
                }
            }
        } catch (Exception e) {
            log.warn("Errore estrazione testo email", e);
        }
        return "";
    }

    private Session creaSessioneSenzaSSLCheck(Properties props) throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
        };
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new SecureRandom());

        props.put("mail.imaps.ssl.socketFactory", sc.getSocketFactory());
        props.put("mail.imaps.ssl.socketFactory.fallback", "false");
        props.put("mail.imaps.ssl.checkserveridentity", "false");

        return Session.getInstance(props);
    }
}