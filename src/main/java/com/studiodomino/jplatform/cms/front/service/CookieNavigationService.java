package com.studiodomino.jplatform.cms.front.service;

import com.studiodomino.jplatform.cms.entity.UtenteEsterno;
import com.studiodomino.jplatform.cms.front.dao.DAOUtenteEsterno;
import com.studiodomino.jplatform.cms.front.dao.DAOPubblico;
import com.studiodomino.jplatform.shared.config.ConfigurazioneCore;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.StringTokenizer;

@Service
@RequiredArgsConstructor
@Slf4j
public class CookieNavigationService {

    private final DAOUtenteEsterno daoUtenteEsterno;
    private final DAOPubblico daoPubblico;

    /**
     * Aggiorna cookies navigazione utente
     * Equivalente a AggiornaCookiesNavigazioneUtenteEsterno()
     */
    public void updateNavigationCookies(
            HttpServletRequest request,
            HttpServletResponse response,
            ConfigurazioneCore configCore) {

        try {
            String siteCheck = configCore.getSito().getCheck();
            String cookieName = "platformlang" + siteCheck;
            String userProfileIDName = "platform_userProfileID" + siteCheck;
            String platforProfileIDName = "platform_ProfileID" + siteCheck + "_";

            Cookie[] cookies = request.getCookies();
            Cookie langCookie = null;
            Cookie userIDCookie = null;
            Cookie profileCookie = null;

            // Cerca cookies esistenti
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(cookieName)) {
                        langCookie = cookie;
                        configCore.setLangPlatCookieName(cookie.getName());
                        configCore.setLangPlatCookieValue(cookie.getValue());
                    } else if (cookie.getName().equals(userProfileIDName)) {
                        userIDCookie = cookie;
                        configCore.setUserIDCookieName(cookie.getName());
                        configCore.setUserIDCookieValue(cookie.getValue());
                    }
                }
            }

            // Imposta default se non trovati
            if (userIDCookie == null) {
                configCore.setUserIDCookieName(userProfileIDName + "0");
                configCore.setUserIDCookieValue("0");
            }

            // Costruisci nome cookie profilo
            if (userIDCookie != null) {
                platforProfileIDName += userIDCookie.getValue();
            } else {
                platforProfileIDName += "0";
            }

            // Cerca cookie profilo
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(platforProfileIDName)) {
                        profileCookie = cookie;
                        configCore.setProfileIDCookieName(cookie.getName());
                        configCore.setProfileIDCookieValue(cookie.getValue());
                        break;
                    }
                }
            }

            // Default profilo
            if (profileCookie == null) {
                configCore.setProfileIDCookieName(platforProfileIDName);
                configCore.setProfileIDCookieValue("");
            }

        } catch (Exception e) {
            log.error("Errore in updateNavigationCookies", e);
        }
    }

    /**
     * Carica utente esterno da cookie
     * Equivalente a CaricaUtenteEsterno()
     */
    public void loadExternalUser(
            HttpServletRequest request,
            HttpServletResponse response,
            ConfigurazioneCore configCore) {

        try {
            String userID = configCore.getUserIDCookieValue();

            if (userID != null && !"0".equals(userID)) {
                // Carica anagrafica utente
                UtenteEsterno utente = daoUtenteEsterno.getAnagraficabyId(userID);
                utente.setFilePath(configCore.getImagesRepositoryWeb());
                configCore.setUtente(utente);

                // Carica sezioni private per questo utente
                String selettoreGruppo = utente.GruppiSqlCond();
                List<Section> sezPrivate = daoPubblico.getStrutturaMenuPrivate(
                        "1", "-1", "0", "1", "1",
                        selettoreGruppo, "position", true
                );
                utente.setSezioniFrontPrivate(sezPrivate);

                // Aggiorna cookie profilo navigazione
                Cookie cookie = new Cookie(
                        configCore.getProfileIDCookieName(),
                        utente.getNavigationProfile()
                );
                cookie.setMaxAge(360 * 24 * 60 * 60); // 1 anno
                cookie.setPath("/");
                response.addCookie(cookie);

            } else {
                configCore.setUtente(new UtenteEsterno());
            }

        } catch (Exception e) {
            log.error("Errore in loadExternalUser", e);
            configCore.setUtente(new UtenteEsterno());
        }
    }

    /**
     * Aggiorna profilo navigazione (ultimi 12 contenuti visitati)
     * Equivalente a profilaNavigazione()
     */
    public void updateNavigationProfile(
            HttpServletRequest request,
            HttpServletResponse response,
            String idOggetto,
            ConfigurazioneCore configCore) {

        try {
            String currentProfile = configCore.getProfileIDCookieValue();
            if (currentProfile == null) return;

            StringBuilder newProfile = new StringBuilder();
            int maxElements = 11;
            int count = 0;

            // Parse profilo esistente
            StringTokenizer st = new StringTokenizer(currentProfile, ",");

            // Skip primo se già 12 elementi
            if (st.countTokens() == 12) {
                st.nextToken();
            }

            // Ricostruisci profilo senza duplicati
            while (st.hasMoreTokens() && count < maxElements) {
                String token = st.nextToken();
                if (!token.equals(idOggetto)) {
                    if (newProfile.length() > 0) {
                        newProfile.append(",");
                    }
                    newProfile.append(token);
                    count++;
                }
            }

            // Nuovo profilo: ID corrente in testa
            String finalProfile = idOggetto;
            if (newProfile.length() > 0) {
                finalProfile += "," + newProfile;
            }

            // Salva cookie
            Cookie cookie = new Cookie(
                    configCore.getProfileIDCookieName(),
                    finalProfile
            );
            cookie.setMaxAge(360 * 24 * 60 * 60);
            cookie.setPath("/");
            response.addCookie(cookie);

            configCore.setProfileIDCookieValue(finalProfile);

            // Aggiorna DB utente se loggato
            UtenteEsterno utente = configCore.getUtente();
            if (utente != null && !"-1".equals(utente.getId())
                    && !"0".equals(configCore.getUserIDCookieValue())) {
                utente.setNavigationProfile(finalProfile);
                daoUtenteEsterno.updateNavigationProfile(utente);
            }

        } catch (Exception e) {
            log.error("Errore in updateNavigationProfile", e);
        }
    }
}