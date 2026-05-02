package com.eranitor.tcc.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table (name = "tempoestudo")
@Entity
@Getter
@Setter
public class TempoEstudo {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "idtempoestudo")
    private Integer idTempoEstudo;

    @ManyToOne
    @JoinColumn (name = "materia_idmateria")
    private Materia materia;

    @ManyToOne
    @JoinColumn (name = "usuario_idusuario")
    private Usuario usuario;

    @Column (name = "mes")
    private Integer mes;

    @Column (name = "ano")
    private Integer ano;

    @Column (name = "minutosatividade")
    private Integer minutosAtividade;

    @Column (name = "minutosvideo")
    private Integer mintutosVideo;

    @Column (name = "minutosapostila")
    private Integer minutosApostila;
}
