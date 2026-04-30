package com.eranitor.tcc.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table (name = "agenda")
@Getter
@Setter
public class Agenda {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "idagenda")
    private Integer id;

    @ManyToOne
    @JoinColumn (name = "usuario_idusuario")
    private Usuario usuario;

    @Column (name = "data_2")
    private LocalDate data_2;

    @Column (name = "titulo")
    private String titulo;

    @Column (name = "tipo")
    private String tipo;

}
