package com.studiodomino.jplatform.shared.repository;

import com.studiodomino.jplatform.shared.entity.Site;

public interface NotificaEmailService {
    void notificaModificaContenuto(Site site, String titolo, String url, String quando);
}