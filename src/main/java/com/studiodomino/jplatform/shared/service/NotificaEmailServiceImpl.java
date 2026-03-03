package com.studiodomino.jplatform.shared.service;

import com.studiodomino.jplatform.shared.entity.Site;
import com.studiodomino.jplatform.shared.repository.NotificaEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificaEmailServiceImpl implements NotificaEmailService {

    @Override
    public void notificaModificaContenuto(Site site, String titolo, String url, String quando) {
        // Nel tuo progetto non c'è un sistema email configurato.
        // Qui lasciamo un log (o puoi lasciare vuoto).
        if (site != null && "1".equals(site.getLibero8())) {
            log.info("NOTIFICA (noop) modifica contenuto: quando={}, titolo={}, url={}", quando, titolo, url);
        }
    }
}