package com.studiodomino.jplatform.cms.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSender mailSender;

    public void inviaEmail(String to, String cc, String oggetto, String testo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("serviziweb@studiodomino.com");
        message.setTo(to);

        if (cc != null && !cc.isBlank()) {
            message.setCc(cc);
        }

        message.setSubject(oggetto);
        message.setText(testo);

        mailSender.send(message);
    }
}