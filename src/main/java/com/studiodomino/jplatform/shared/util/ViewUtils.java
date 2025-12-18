package com.studiodomino.jplatform.shared.util;

import com.studiodomino.jplatform.shared.config.ConfigurazioneCore;
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
    public static String resolveTemplate(ConfigurazioneCore config, String templatePath) {
        if (config == null || config.getSito() == null) {
            return "error/500";
        }

        Site site = config.getSito();
        Integer siteId = site.getId();

        String siteFolder = config.getSito().getPath2();

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
    public static String resolveAsset(ConfigurazioneCore config, String assetPath) {
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
    public static String getAssetBaseUrl(ConfigurazioneCore config) {
        if (config == null || config.getSito() == null) {
            return "/common";
        }

        return "/" + getAssetFolder(config.getSito().getId());
    }

    /**
     * Risolve template per SITI PUBBLICI (accesso = 2)
     * Usa il campo path2
     *
     * @param site Site entity
     * @param viewName Nome vista (es. "front/home")
     * @return Path completo template (es. "site01/front/home")
     */
    public static String resolvePublicTemplate(Site site, String viewName) {
        if (site == null) {
            return "error/404";
        }

        String templateFolder = site.getPublicTemplateFolder();
        return templateFolder + "/" + viewName;
    }

    /**
     * Risolve template per APPLICAZIONI PROTETTE (accesso = 1)
     * Usa sempre "manager" come folder
     *
     * @param viewName Nome vista
     * @return Path completo template (es. "manager/front/dashboard")
     */
    public static String resolveProtectedTemplate(String viewName) {
        return "manager/" + viewName;
    }

    /**
     * Risolve path assets per siti pubblici
     */
    public static String resolvePublicAsset(Site site, String assetPath) {
        String folder = site != null ? site.getPublicTemplateFolder() : "site01";
        return "/" + folder + "/assets/" + assetPath;
    }

    /**
     * Risolve path assets per applicazioni protette
     */
    public static String resolveProtectedAsset(String assetPath) {
        return "/manager/assets/" + assetPath;
    }
}