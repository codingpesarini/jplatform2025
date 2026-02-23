package com.studiodomino.jplatform.shared.enums;

import lombok.Getter;

/**
 * Enum per mappare il campo utente.l2 agli endpoint applicativi
 */
@Getter
public enum ModuloApplicativo {

    INDEFINITO("", "indefinito", "Non assegnato"),
    CMS("0", "admin", "Portale WEB"),
    PROTOCOLLO("1", "protocollo", "Protocollo e gestione documentale"),
    PERSONALE("2", "personale", "Gestione personale"),
    CRM("3", "crm", "Customer Relationship Management");

    private final String codice;      // Valore nel DB (l2)
    private final String endpoint;    // URL endpoint
    private final String descrizione; // Descrizione leggibile

    ModuloApplicativo(String codice, String endpoint, String descrizione) {
        this.codice = codice;
        this.endpoint = endpoint;
        this.descrizione = descrizione;
    }

    /**
     * Trova ModuloApplicativo da codice l2
     */
    public static ModuloApplicativo fromCodice(String codice) {
        if (codice == null || codice.isEmpty()) {
            return INDEFINITO;
        }

        for (ModuloApplicativo modulo : values()) {
            if (modulo.codice.equals(codice)) {
                return modulo;
            }
        }

        return INDEFINITO;
    }

    /**
     * Ottiene endpoint da codice l2
     */
    public static String getEndpoint(String l2) {
        return fromCodice(l2).getEndpoint();
    }

    /**
     * Ottiene descrizione da codice l2
     */
    public static String getDescrizione(String l2) {
        return fromCodice(l2).getDescrizione();
    }
}