package com.studiodomino.jplatform.shared.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

@Entity
@Table(name = "account")
@Data
public class Account implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String descrizione;

    @Column(name = "tipoaccount")
    private String tipoAccount;

    @Column(name = "lasciacopiaserver")
    private String lasciaCopiaServer;

    // ── EMAIL ──
    @Column(name = "emailaccount")
    private String emailAccount;

    @Column(name = "emailintestazione")
    private String emailIntestazione;

    @Column(name = "emailuser")
    private String emailUser;

    @Column(name = "emailpassword")
    private String emailPassword;

    @Column(name = "emailinserver")
    private String emailInServer;

    @Column(name = "emailinserverport")
    private Integer emailInServerPort;

    @Column(name = "emailoutserver")
    private String emailOutServer;

    @Column(name = "emailoutserverport")
    private Integer emailOutServerPort;

    @Column(name = "emailinservertype")
    private String emailInServerType;

    @Column(name = "emailoutserverauth")
    private String emailOutServerAuth;

    // ── PEC ──
    @Column(name = "pecaccount")
    private String pecAccount;

    @Column(name = "pecintestazione")
    private String pecIntestazione;

    @Column(name = "pecuser")
    private String pecUser;

    @Column(name = "pecpassword")
    private String pecPassword;

    @Column(name = "pecinserver")
    private String pecInServer;

    @Column(name = "pecinserverport")
    private Integer pecInServerPort;

    @Column(name = "pecoutserver")
    private String pecOutServer;

    @Column(name = "pecoutserverport")
    private Integer pecOutServerPort;

    @Column(name = "pecinservertype")
    private String pecInServerType;

    @Column(name = "pecoutserverauth")
    private String pecOutServerAuth;
}