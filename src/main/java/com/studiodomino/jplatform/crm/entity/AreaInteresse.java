package com.studiodomino.jplatform.crm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity JPA per la tabella 'areeintessecrm' (o il nome reale sul DB).
 * Conversione da AreaInteresse.java (Struts ActionForm).
 */
@Entity
@Table(name = "areeinteresse")
@Getter
@Setter
@NoArgsConstructor
public class AreaInteresse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "descrizione", length = 255)
    private String descrizione;

    // Costruttore rapido se servisse per test
    public AreaInteresse(Integer id, String descrizione) {
        this.id = id;
        this.descrizione = descrizione;
    }
}
