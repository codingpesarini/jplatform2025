package com.studiodomino.jplatform.cms.admin.service;

import com.studiodomino.jplatform.shared.entity.Account;
import com.studiodomino.jplatform.shared.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderService {

    private final AccountRepository accountRepository;

    public void inviaEmail(String to, String cc, String oggetto, String testo) {
        // Usa il primo account EMAIL disponibile
        Account account = accountRepository.findFirstByTipoAccountOrderByIdAsc("EMAIL");
        if (account == null) {
            log.error("Nessun account EMAIL configurato nel DB");
            throw new RuntimeException("Nessun account EMAIL configurato");
        }
        inviaEmailConAccount(account, to, cc, oggetto, testo);
    }

    public void inviaEmailConAccount(Account account, String to, String cc, String oggetto, String testo) {
        try {
            JavaMailSenderImpl mailSender = creaMailSender(account);

            var message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(account.getEmailAccount());
            helper.setTo(to);
            if (cc != null && !cc.isBlank()) {
                helper.setCc(cc);
            }
            helper.setSubject(oggetto);
            helper.setText(testo, true); // true = HTML

            mailSender.send(message);
            log.info("Email inviata a: {} con account: {}", to, account.getEmailAccount());

        } catch (Exception e) {
            log.error("Errore invio email a {}: {}", to, e.getMessage());
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