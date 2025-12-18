package com.studiodomino.jplatform.shared.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * PasswordService - Gestione crittografia password
 * Compatibile con CryptBean legacy (MD5 + Base64)
 */
@Component
@Slf4j
public class PasswordService {

    /**
     * Cripta password con MD5 + Base64 (legacy compatibility)
     * Equivalente a CryptBean.cryptString()
     */
    public String cryptPassword(String password) {
        if (password == null || password.isEmpty()) {
            return "";
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            log.error("Errore crittografia password", e);
            return "";
        }
    }

    /**
     * Verifica password contro hash memorizzato
     */
    public boolean verificaPassword(String passwordInChiaro, String passwordCriptata) {
        if (passwordInChiaro == null || passwordCriptata == null) {
            return false;
        }

        // Prova versione normale
        String crypted = cryptPassword(passwordInChiaro);
        if (crypted.equals(passwordCriptata)) {
            return true;
        }

        // Prova versione uppercase (legacy compatibility)
        String cryptedUpper = cryptPassword(passwordInChiaro.toUpperCase());
        return cryptedUpper.equals(passwordCriptata);
    }

    /**
     * Genera password casuale
     */
    public String generaPasswordCasuale(int lunghezza) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < lunghezza; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }

        return password.toString();
    }

    /**
     * Valida robustezza password
     */
    public boolean isPasswordRobusta(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasMaiuscola = password.matches(".*[A-Z].*");
        boolean hasMinuscola = password.matches(".*[a-z].*");
        boolean hasNumero = password.matches(".*\\d.*");

        return hasMaiuscola && hasMinuscola && hasNumero;
    }
}