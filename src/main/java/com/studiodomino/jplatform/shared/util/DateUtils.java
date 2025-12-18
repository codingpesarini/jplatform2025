package com.studiodomino.jplatform.shared.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility per gestione date in formato String MySQL
 */
public class DateUtils {

    // Formato MySQL datetime: "yyyy-MM-dd HH:mm:ss"
    private static final DateTimeFormatter MYSQL_DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Formato data italiana: "dd/MM/yyyy HH:mm"
    private static final DateTimeFormatter ITALIAN_DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Formato data italiana solo data: "dd/MM/yyyy"
    private static final DateTimeFormatter ITALIAN_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Converte LocalDateTime in String formato MySQL
     */
    public static String toMySQLDateTimeString(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(MYSQL_DATETIME_FORMATTER);
    }

    /**
     * Converte String MySQL in LocalDateTime
     */
    public static LocalDateTime fromMySQLDateTimeString(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeString, MYSQL_DATETIME_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Ottiene data/ora corrente in formato MySQL String
     */
    public static String nowAsMySQLString() {
        return toMySQLDateTimeString(LocalDateTime.now());
    }

    /**
     * Formatta data String MySQL in formato italiano
     * Es: "2025-11-26 15:30:45" → "26/11/2025 15:30"
     */
    public static String formatToItalian(String mysqlDateTime) {
        LocalDateTime dateTime = fromMySQLDateTimeString(mysqlDateTime);
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(ITALIAN_DATETIME_FORMATTER);
    }

    /**
     * Formatta data String MySQL in formato italiano solo data
     * Es: "2025-11-26 15:30:45" → "26/11/2025"
     */
    public static String formatToItalianDateOnly(String mysqlDateTime) {
        LocalDateTime dateTime = fromMySQLDateTimeString(mysqlDateTime);
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(ITALIAN_DATE_FORMATTER);
    }

    /**
     * Verifica se una stringa data è valida
     */
    public static boolean isValidMySQLDateTime(String dateTimeString) {
        return fromMySQLDateTimeString(dateTimeString) != null;
    }
}