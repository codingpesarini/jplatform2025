package com.studiodomino.jplatform.shared.config;

import lombok.Data;
import java.io.Serializable;

@Data
public class Ricerca implements Serializable {
    private String query;
    private String tipo;
    private String categoria;
    private String dataInizio;
    private String dataFine;
    private Integer risultatiTotali;
}