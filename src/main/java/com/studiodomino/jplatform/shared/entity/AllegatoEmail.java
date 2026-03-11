package com.studiodomino.jplatform.shared.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllegatoEmail implements Serializable {

    private Integer id;
    private Integer iddocallegati;
    private String nome;
    private String type;
    private String idorigine;
}