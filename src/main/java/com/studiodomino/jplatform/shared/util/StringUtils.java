package com.studiodomino.jplatform.shared.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    /**
     * Verifica se una stringa è vuota o null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Verifica se una stringa NON è vuota
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * Ritorna stringa o default se null/vuota
     */
    public static String defaultIfEmpty(String str, String defaultValue) {
        return isEmpty(str) ? defaultValue : str;
    }

    /**
     * Tronca stringa a lunghezza massima
     */
    public static String truncate(String str, int maxLength) {
        if (str == null) {
            return "";
        }
        return str.length() > maxLength ? str.substring(0, maxLength) + "..." : str;
    }

    /**
     * Converte array separato da ; in array Java
     * Equivalente del tuo stringToArray in Struts
     */
    public static String[] stringToArray(String str, String separator) {
        if (isEmpty(str)) {
            return new String[0];
        }

        List<String> result = new ArrayList<>();
        String[] tokens = str.split(separator);

        for (String token : tokens) {
            if (!token.isEmpty()) {
                // Rimuovi parentesi come nel tuo codice Struts
                String cleaned = token.replaceAll("[()]", "");
                result.add(cleaned);
            }
        }

        return result.toArray(new String[0]);
    }

    /**
     * Converte array Java in stringa separata da ;
     * Equivalente del tuo arrayToString in Struts
     */
    public static String arrayToString(String[] array, String separator) {
        if (array == null || array.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (String item : array) {
            if (item != null && !item.isEmpty()) {
                sb.append("(").append(item).append(")").append(separator);
            }
        }

        return sb.toString();
    }

    /**
     * Capitalizza prima lettera
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    /**
     * Rimuove HTML tags
     */
    public static String stripHtml(String html) {
        if (html == null) {
            return "";
        }
        return html.replaceAll("<[^>]*>", "");
    }

    /**
     * Genera slug da stringa (per URL)
     */
    public static String toSlug(String str) {
        if (isEmpty(str)) {
            return "";
        }
        return str.toLowerCase()
                .replaceAll("[àáâãäå]", "a")
                .replaceAll("[èéêë]", "e")
                .replaceAll("[ìíîï]", "i")
                .replaceAll("[òóôõö]", "o")
                .replaceAll("[ùúûü]", "u")
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }
}