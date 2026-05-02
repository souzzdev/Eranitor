package com.eranitor.tcc.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table (name = "topico")
@Entity
@Getter
@Setter
public class Topico {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "idtopico")
    private Integer idTopico;

    @ManyToOne
    @JoinColumn (name = "materia_idmateria")
    private Materia materia;

    @Column (name = "nome")
    private String nome;

    @Column (name = "concluido")
    private Boolean concluido;

}
