package com.studiodomino.jplatform.shared.util;

import com.studiodomino.jplatform.shared.config.AppConfiguration;
import com.studiodomino.jplatform.shared.entity.Site;

/**
 * Utility per gestire view multi-sito
 */
public class ViewUtils {

    /**
     * Risolve il template in base al Site attivo
     *
     * Esempi:
     * - resolveTemplate(config, "front/home") → "site01/front/home"
     * - resolveTemplate(config, "front/home") → "site02/front/home"
     */
    public static String resolveTemplate(AppConfiguration config, String templatePath) {
        if (config == null || config.getSito() == null) {
            return "error/500";
        }

        Site site = config.getSito();
        Integer siteId = site.getId();

        String siteFolder = getSiteFolder(siteId);

        return siteFolder + "/" + templatePath;
    }

    /**
     * Risolve template per admin/manager (non dipende da site)
     *
     * Esempio:
     * - resolveManagerTemplate("front/dashboard") → "manager/front/dashboard"
     */
    public static String resolveManagerTemplate(String templatePath) {
        return "manager/" + templatePath;
    }

    /**
     * Risolve il path CSS/JS/immagini in base al Site attivo
     *
     * Esempio:
     * - resolveAsset(config, "css/style.css") → "/site01/assets/css/style.css"
     */
    public static String resolveAsset(AppConfiguration config, String assetPath) {
        if (config == null || config.getSito() == null) {
            return "/common/" + assetPath;
        }

        Site site = config.getSito();
        Integer siteId = site.getId();
        String assetFolder = getAssetFolder(siteId);

        return "/" + assetFolder + "/" + assetPath;
    }

    /**
     * Risolve asset per manager/admin
     */
    public static String resolveManagerAsset(String assetPath) {
        return "/manager/assets/" + assetPath;
    }

    /**
     * Mappa Site.id → cartella template
     */
    private static String getSiteFolder(Integer siteId) {
        if (siteId == null) {
            return "site01";
        }

        return switch (siteId) {
            case 1 -> "site01";
            case 2 -> "site02";
            case 3 -> "site03";
            default -> "site01";
        };
    }

    /**
     * Mappa Site.id → cartella assets
     */
    private static String getAssetFolder(Integer siteId) {
        if (siteId == null) {
            return "site01/assets";  // ← CORRETTO: slash non punto
        }

        return switch (siteId) {
            case 1 -> "site01/assets";   // ← CORRETTO
            case 2 -> "site02/assets";   // ← CORRETTO
            case 3 -> "site03/assets";   // ← CORRETTO
            default -> "site01/assets";
        };
    }

    /**
     * Ottieni base URL per assets (per usare in template)
     */
    public static String getAssetBaseUrl(AppConfiguration config) {
        if (config == null || config.getSito() == null) {
            return "/common";
        }

        return "/" + getAssetFolder(config.getSito().getId());
    }
}