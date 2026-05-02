package com.eranitor.tcc.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table (name = "tarefa")
@Entity
@Getter
@Setter
public class Tarefa {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "idtarefa")
    private Integer idTarefa;

    @ManyToOne
    @JoinColumn (name = "materia_idmateria")
    private Materia materia;

    @ManyToOne
    @JoinColumn (name = "usuario_idusuario")
    private Usuario usuario;

    @Column (name = "titulo")
    private String titulo;

    @Column (name = "descricao")
    private String descricao;

    @Column (name = "datavencimento")
    private LocalDate dataVencimento;

    @Column (name = "cor")
    private String cor;

    @Column (name = "concluida")
    private Boolean concluida;

    @Column (name = "criadoem")
    private LocalDate criadoEm;
}
