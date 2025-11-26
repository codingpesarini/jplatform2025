package com.studiodomino.jplatform.shared.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter ITALIAN_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter ITALIAN_DATETIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Ottieni data/ora corrente in formato SQL
     */
    public static String now() {
        return LocalDateTime.now().format(DATETIME_FORMAT);
    }

    /**
     * Ottieni data corrente in formato SQL
     */
    public static String today() {
        return LocalDate.now().format(DATE_FORMAT);
    }

    /**
     * Formatta data in formato italiano
     */
    public static String toItalianDate(String sqlDate) {
        if (sqlDate == null || sqlDate.isEmpty()) {
            return "";
        }
        try {
            LocalDate date = LocalDate.parse(sqlDate, DATE_FORMAT);
            return date.format(ITALIAN_DATE_FORMAT);
        } catch (Exception e) {
            return sqlDate;
        }
    }

    /**
     * Formatta data/ora in formato italiano
     */
    public static String toItalianDateTime(String sqlDateTime) {
        if (sqlDateTime == null || sqlDateTime.isEmpty()) {
            return "";
        }
        try {
            LocalDateTime dateTime = LocalDateTime.parse(sqlDateTime, DATETIME_FORMAT);
            return dateTime.format(ITALIAN_DATETIME_FORMAT);
        } catch (Exception e) {
            return sqlDateTime;
        }
    }

    /**
     * Converte data italiana in formato SQL
     */
    public static String fromItalianDate(String italianDate) {
        if (italianDate == null || italianDate.isEmpty()) {
            return "";
        }
        try {
            LocalDate date = LocalDate.parse(italianDate, ITALIAN_DATE_FORMAT);
            return date.format(DATE_FORMAT);
        } catch (Exception e) {
            return italianDate;
        }
    }
}