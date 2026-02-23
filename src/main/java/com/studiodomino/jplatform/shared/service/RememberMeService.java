package com.studiodomino.jplatform.shared.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class RememberMeService {

    private static final String COOKIE_NAME = "jplatform_remember";
    private static final int COOKIE_DAYS = 30;
    private static final int COOKIE_MAX_AGE = COOKIE_DAYS * 24 * 60 * 60;

    // token -> username (in-memory, si azzera al restart del server)
    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();

    /**
     * Crea il cookie remember me e salva il token
     */
    public void createRememberMeCookie(String username, HttpServletResponse response) {
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, username);

        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setPath("/");
        cookie.setHttpOnly(true);  // non accessibile da JS
        response.addCookie(cookie);

        log.info("Remember me cookie creato per: {}", username);
    }

    /**
     * Risolve lo username dal cookie, null se non valido
     */
    public String resolveUsername(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                String token = cookie.getValue();
                String username = tokenStore.get(token);
                if (username != null) {
                    log.info("Remember me valido per: {}", username);
                }
                return username;
            }
        }
        return null;
    }

    /**
     * Cancella il cookie e il token salvato
     */
    public void deleteRememberMeCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return;

        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                // rimuovi token dallo store
                tokenStore.remove(cookie.getValue());

                // cancella il cookie impostando maxAge=0
                Cookie deleteCookie = new Cookie(COOKIE_NAME, "");
                deleteCookie.setMaxAge(0);
                deleteCookie.setPath("/");
                deleteCookie.setHttpOnly(true);
                response.addCookie(deleteCookie);

                log.info("Remember me cookie cancellato");
                break;
            }
        }
    }
}