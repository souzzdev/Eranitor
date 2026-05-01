package com.eranitor.tcc.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table (name = "usuario")
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "idusuario")
    private Integer idUsuario;

    @Column (name = "nome")
    private String nome;

    @Column (name = "login")
    private String login;

    @Column (name = "senhahash")
    private String senhaHash;

    @Column (name = "serie")
    private String serie;

    @Column (name = "instituicao")
    private String instituicao;

    @Column (name = "criadoem")
    private LocalDateTime criadoEm;

    @OneToMany (mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Desempenho> desempenhos;

    @OneToMany (mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Materia> materias;

    @OneToMany (mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Agenda> agenda;
}
