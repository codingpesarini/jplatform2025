package com.studiodomino.jplatform.shared.service;

import com.studiodomino.jplatform.shared.entity.Account;
import com.studiodomino.jplatform.shared.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.search.FlagTerm;
import jakarta.mail.search.SearchTerm;

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

    // --- METODO ORIGINALE: LEGGE TUTTA LA INBOX ---
    public List<Map<String, Object>> leggiInbox(Integer idAccount, String folderName) {
        return recuperaMessaggiDalServer(idAccount, folderName, null);
    }

    // --- NUOVO METODO: LEGGE SOLO LE EMAIL NON LETTE ---
    public List<Map<String, Object>> leggiEmailNonLette(Integer idAccount, String folderName) {
        // Filtro IMAP per messaggi dove il flag SEEN è falso
        SearchTerm unseenTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
        return recuperaMessaggiDalServer(idAccount, folderName, unseenTerm);
    }

    // --- LOGICA CORE DI RECUPERO (UNIFICATA) ---
    private List<Map<String, Object>> recuperaMessaggiDalServer(Integer idAccount, String folderName, SearchTerm filtro) {
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

            // Configurazione Connessione (Tua logica originale)
            if (tipo.contains("IMAP")) {
                if (porta == 993) {
                    props.put("mail.store.protocol", "imaps");
                    props.put("mail.imaps.host", host);
                    props.put("mail.imaps.port", porta);
                    props.put("mail.imaps.ssl.enable", "true");
                    store = creaSessioneSenzaSSLCheck(props).getStore();
                } else {
                    props.put("mail.store.protocol", "imap");
                    props.put("mail.imap.host", host);
                    props.put("mail.imap.port", porta);
                    store = Session.getInstance(props).getStore();
                }
            } else {
                // POP3 non supporta filtri di ricerca complessi (SearchTerm)
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
                store = Session.getInstance(props).getStore();
            }

            store.connect(host, user, password);

            String fn = (tipo.contains("IMAP") && folderName != null && !folderName.isBlank())
                    ? cercaCartella(store, folderName) : "INBOX";

            Folder folder = store.getFolder(fn);
            folder.open(Folder.READ_ONLY);

            Message[] messages;

            // Applichiamo il filtro se presente (solo IMAP)
            if (tipo.contains("IMAP") && filtro != null) {
                messages = folder.search(filtro);
            } else {
                int total = folder.getMessageCount();
                int start = Math.max(1, total - 49);
                messages = (total > 0) ? folder.getMessages(start, total) : new Message[0];
            }

            // Mappatura risultati in ordine decrescente (più recenti prima)
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

                // Opzionale: limitiamo a 50 anche per i filtri non letti
                if (risultati.size() >= 50) break;
            }

            folder.close(false);
            store.close();

        } catch (Exception e) {
            log.error("Errore lettura messaggi idAccount={} folder={}", idAccount, folderName, e);
            throw new RuntimeException("Errore connessione: " + e.getMessage(), e);
        }
        return risultati;
    }

    // --- METODI DI SUPPORTO ORIGINALI (Invariati) ---

    private String estraiTestoDaPart(Part part) {
        try {
            if (part.isMimeType("text/html")) return (String) part.getContent();
            if (part.isMimeType("text/plain")) return (String) part.getContent();
            if (part.isMimeType("multipart/*")) {
                MimeMultipart mp = (MimeMultipart) part.getContent();
                for (int i = 0; i < mp.getCount(); i++) {
                    if (mp.getBodyPart(i).isMimeType("text/html"))
                        return (String) mp.getBodyPart(i).getContent();
                }
                for (int i = 0; i < mp.getCount(); i++) {
                    String r = estraiTestoDaPart(mp.getBodyPart(i));
                    if (!r.isEmpty()) return r;
                }
            }
        } catch (Exception e) {
            log.warn("Errore estrazione parte", e);
        }
        return "";
    }

    private String estraiTesto(Message msg) {
        try { return estraiTestoDaPart(msg); }
        catch (Exception e) { return ""; }
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

    private String cercaCartella(Store store, String nomeRichiesto) {
        try {
            Map<String, String> mapping = new HashMap<>();
            mapping.put("SENT", "Sent");
            mapping.put("DRAFTS", "Drafts");
            mapping.put("TRASH", "Trash");
            mapping.put("SPAM", "Spam");
            String mapped = mapping.getOrDefault(nomeRichiesto.toUpperCase(), nomeRichiesto);
            Folder[] folders = store.getDefaultFolder().list("*");
            for (Folder f : folders) {
                if (f.getName().equalsIgnoreCase(nomeRichiesto) || f.getName().equalsIgnoreCase(mapped)) {
                    return f.getFullName();
                }
            }
            return mapped;
        } catch (Exception e) { return nomeRichiesto; }
    }

    public Map<String, Object> leggiMessaggio(Integer idAccount, int messageNumber, String folderName) {
        // Implementazione identica alla tua originale...
        try {
            Account account = accountRepository.findById(idAccount).orElseThrow();
            Properties props = new Properties();
            // ... (logica connessione identica) ...
            props.put("mail.store.protocol", "imaps");
            props.put("mail.imaps.host", account.getEmailInServer());
            props.put("mail.imaps.port", 993);
            props.put("mail.imaps.ssl.enable", "true");
            Store store = creaSessioneSenzaSSLCheck(props).getStore();
            store.connect(account.getEmailInServer(), account.getEmailUser(), account.getEmailPassword());

            String fn = (folderName != null && !folderName.isBlank()) ? cercaCartella(store, folderName) : "INBOX";
            Folder folder = store.getFolder(fn);
            folder.open(Folder.READ_ONLY);
            Message msg = folder.getMessage(messageNumber);
            Map<String, Object> result = new HashMap<>();
            result.put("id",       msg.getMessageNumber());
            result.put("oggetto",  msg.getSubject());
            result.put("mittente", msg.getFrom()[0].toString());
            result.put("data",     msg.getSentDate());
            result.put("testo",    estraiTesto(msg));
            folder.close(false);
            store.close();
            return result;
        } catch (Exception e) { return null; }
    }

    public int contaMessaggiNonLetti(Integer idAccount) {
        // Tua implementazione originale (molto efficiente con getUnreadMessageCount)
        try {
            Account account = accountRepository.findById(idAccount).orElseThrow();
            if (account.getEmailInServerType() != null && !account.getEmailInServerType().toUpperCase().contains("IMAP")) return 0;
            Properties props = new Properties();
            props.put("mail.store.protocol", "imaps");
            props.put("mail.imaps.host", account.getEmailInServer());
            props.put("mail.imaps.port", 993);
            props.put("mail.imaps.ssl.enable", "true");
            Store store = creaSessioneSenzaSSLCheck(props).getStore();
            store.connect(account.getEmailInServer(), account.getEmailUser(), account.getEmailPassword());
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            int nonLetti = inbox.getUnreadMessageCount();
            inbox.close(false);
            store.close();
            return nonLetti;
        } catch (Exception e) { return 0; }
    }

    public void cancellaMessaggiDalServer(Account account, List<String> ids) throws Exception {
        // Tua implementazione originale...
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", account.getEmailInServer());
        props.put("mail.imaps.port", 993);
        props.put("mail.imaps.ssl.enable", "true");
        Store store = creaSessioneSenzaSSLCheck(props).getStore();
        store.connect(account.getEmailInServer(), account.getEmailUser(), account.getEmailPassword());
        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_WRITE);
        String trashName = cercaCartella(store, "TRASH");
        Folder trashFolder = store.getFolder(trashName);
        try {
            List<Message> toMove = new ArrayList<>();
            for (String id : ids) toMove.add(folder.getMessage(Integer.parseInt(id)));
            if (!toMove.isEmpty()) {
                Message[] arr = toMove.toArray(new Message[0]);
                if (trashFolder.exists() || trashFolder.create(Folder.HOLDS_MESSAGES)) folder.copyMessages(arr, trashFolder);
                folder.setFlags(arr, new Flags(Flags.Flag.DELETED), true);
            }
        } finally {
            folder.close(true);
            store.close();
        }
    }
}