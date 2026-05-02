package com.eranitor.tcc.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table (name = "desempenho")
@Entity
@Getter
@Setter
public class Desempenho {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "iddesempenho")
    private Long idDesempenho;

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

    @Column (name = "horasconcluidas")
    private Integer horasConcluidas;

    @Column (name = "horasmeta")
    private Integer horasMeta;
}
