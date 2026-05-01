package com.eranitor.tcc.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table (name = "materia")
@Entity
@Getter
@Setter
public class Materia {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "idmateria")
    private Integer idMateria;

    @ManyToOne
    @JoinColumn (name = "usuario_idusuario", nullable = false)
    private Usuario usuario;

    @Column (name = "nome")
    private String nome;

    @Column (name = "ativa")
    private Boolean ativa;
}
