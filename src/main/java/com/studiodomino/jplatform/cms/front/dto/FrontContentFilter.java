package com.studiodomino.jplatform.cms.front.dto;
import com.studiodomino.jplatform.shared.entity.UtenteEsterno;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FrontContentFilter {
    private String anno;
    private String mese;
    private String stato;
    private String privato;
    private String my;
    private String archivio;
    private String ordinamento;
    private String sqlContenuto;
    private UtenteEsterno utente;
}