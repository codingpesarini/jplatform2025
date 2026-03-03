package com.studiodomino.jplatform.crm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "numeratori")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Numeratore implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "l1", length = 255)
    private String l1;

    @Column(name = "anno", length = 10)
    private String anno;

    @Column(name = "numero")
    private Integer numero;

    @Column(name = "l2", length = 10)
    @Builder.Default
    private String l2 = "";

    @Column(name = "l3", length = 10)
    @Builder.Default
    private String l3 = "";

    @Column(name = "l4", length = 10)
    @Builder.Default
    private String l4 = "";

    @Column(name = "l5", length = 10)
    @Builder.Default
    private String l5 = "";

    @Column(name = "l6", length = 10)
    @Builder.Default
    private String l6 = "";

    @Column(name = "l7", length = 10)
    @Builder.Default
    private String l7 = "";

    @Column(name = "l8", length = 10)
    @Builder.Default
    private String l8 = "";

    @Column(name = "l9", length = 10)
    @Builder.Default
    private String l9 = "";

    @Column(name = "l10", length = 10)
    @Builder.Default
    private String l10 = "";
}