package com.studiodomino.jplatform.cms.front.service;

import com.studiodomino.jplatform.shared.config.Configurazione;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * Service per gestione cookie di navigazione
 * Traccia profilo navigazione utente (ultimi 12 contenuti visitati)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CookieNavigationService {

    private static final String COOKIE_PROFILE_NAME = "jplatform_nav_profile";
    private static final int MAX_PROFILE_ITEMS = 12;
    private static final int COOKIE_MAX_AGE = 60 * 60 * 24 * 30; // 30 giorni

    /**
     * Aggiorna cookie profilo navigazione
     */
    public void updateNavigationCookies(
            HttpServletRequest request,
            HttpServletResponse response,
            Configurazione configCore) {

        try {
            // Leggi cookie esistente (decodificato)
            String currentProfile = getCookieValue(request, COOKIE_PROFILE_NAME);

            // Imposta in config
            configCore.setProfileIDCookieValue(currentProfile);
            configCore.setProfileIDCookieName(COOKIE_PROFILE_NAME);

        } catch (Exception e) {
            log.error("Errore aggiornamento cookie navigazione", e);
        }
    }

    /**
     * Aggiorna profilo navigazione con nuovo contenuto visitato
     */
    public void updateNavigationProfile(
            HttpServletRequest request,
            HttpServletResponse response,
            String pid,
            Configurazione configCore) {

        try {
            // Leggi profilo attuale (decodificato)
            String currentProfile = getCookieValue(request, COOKIE_PROFILE_NAME);

            // Costruisci nuovo profilo
            List<String> ids = new ArrayList<>();

            // Aggiungi nuovo ID all'inizio
            ids.add(pid);

            // Aggiungi ID esistenti (escludi duplicati)
            if (currentProfile != null && !currentProfile.isEmpty()) {
                String[] existingIds = currentProfile.split(",");
                for (String id : existingIds) {
                    if (!id.equals(pid) && ids.size() < MAX_PROFILE_ITEMS) {
                        ids.add(id);
                    }
                }
            }

            // Crea stringa profilo (formato logico, con virgole)
            String newProfile = String.join(",", ids);

            // Salva cookie (encoded, senza virgole)
            setCookie(response, COOKIE_PROFILE_NAME, newProfile, COOKIE_MAX_AGE);

            // Aggiorna config con valore logico (non encoded)
            configCore.setProfileIDCookieValue(newProfile);

            log.debug("Profilo navigazione aggiornato: {}", newProfile);

        } catch (Exception e) {
            log.error("Errore aggiornamento profilo navigazione", e);
        }
    }

    /**
     * Carica utente esterno da cookie
     * TODO: Implementare quando avremo UtenteEsterno service
     */
    public void loadExternalUser(
            HttpServletRequest request,
            HttpServletResponse response,
            Configurazione configCore) {

        try {
            // Nota: per ora lascio invariato: se un giorno il cookie user_id contenesse caratteri speciali,
            // puoi applicare lo stesso encoding/decoding anche lì.
            String userIdCookie = getCookieValue(request, "jplatform_user_id");

            if (userIdCookie != null && !userIdCookie.isEmpty()) {
                configCore.setUserIDCookieValue(userIdCookie);
                configCore.setUserIDCookieName("jplatform_user_id");
            }

        } catch (Exception e) {
            log.error("Errore caricamento utente da cookie", e);
        }
    }

    // ========================================
    // UTILITY METHODS
    // ========================================

    private String encodeCookieValue(String raw) {
        if (raw == null) return null;
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    private String decodeCookieValue(String encoded) {
        if (encoded == null || encoded.isEmpty()) return encoded;
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(encoded);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            // Se troviamo un cookie "vecchio" non encodato (es. già presente nel browser),
            // lo trattiamo come plain per retrocompatibilità.
            return encoded;
        }
    }

    /**
     * Leggi valore cookie (ritorna SEMPRE valore decodificato)
     */
    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            return Arrays.stream(cookies)
                    .filter(c -> cookieName.equals(c.getName()))
                    .map(Cookie::getValue)
                    .map(this::decodeCookieValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    /**
     * Imposta cookie (scrive SEMPRE valore encodato)
     */
    private void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        String safeValue = encodeCookieValue(value);
        Cookie cookie = new Cookie(name, safeValue);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    /**
     * Elimina cookie
     */
    private void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}