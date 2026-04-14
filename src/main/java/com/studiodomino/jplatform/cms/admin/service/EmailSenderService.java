package com.studiodomino.jplatform.cms.admin.service;

import com.studiodomino.jplatform.shared.entity.Account;
import com.studiodomino.jplatform.shared.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderService {

    private final AccountRepository accountRepository;

    public void inviaEmail(String to, String cc, String oggetto, String testo,
                           List<MultipartFile> allegati) {
        Account account = accountRepository.findFirstByTipoAccountOrderByIdAsc("EMAIL");
        if (account == null) throw new RuntimeException("Nessun account EMAIL configurato");
        inviaEmailConAccount(account, to, cc, oggetto, testo, allegati);
    }

    // Mantieni il vecchio per compatibilità con chi lo chiama senza allegati
    public void inviaEmail(String to, String cc, String oggetto, String testo) {
        inviaEmail(to, cc, oggetto, testo, null);
    }

    public void inviaEmailConAccount(Account account, String to, String cc,
                                     String oggetto, String testo,
                                     List<MultipartFile> allegati) {
        try {
            JavaMailSenderImpl mailSender = creaMailSender(account);
            var message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(account.getEmailAccount());
            helper.setTo(to);
            if (cc != null && !cc.isBlank()) helper.setCc(cc);
            helper.setSubject(oggetto);
            helper.setText(testo, true);

            if (allegati != null) {
                for (MultipartFile allegato : allegati) {
                    if (allegato != null && !allegato.isEmpty()) {
                        helper.addAttachment(
                                Objects.requireNonNull(allegato.getOriginalFilename()),
                                new ByteArrayResource(allegato.getBytes())
                        );
                    }
                }
            }

            mailSender.send(message);
            log.info("Email inviata a: {} allegati: {}", to,
                    allegati != null ? allegati.stream().filter(a -> !a.isEmpty()).count() : 0);

        } catch (Exception e) {
            log.error("Errore invio email a {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Errore invio email: " + e.getMessage(), e);
        }
    }

    private JavaMailSenderImpl creaMailSender(Account account) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(account.getEmailOutServer());
        mailSender.setPort(account.getEmailOutServerPort() != null ? account.getEmailOutServerPort() : 587);
        mailSender.setUsername(account.getEmailUser());
        mailSender.setPassword(account.getEmailPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "TRUE".equalsIgnoreCase(account.getEmailOutServerAuth()) ? "true" : "false");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", account.getEmailOutServer());
        props.put("mail.debug", "false");

        return mailSender;
    }
}