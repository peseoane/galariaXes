package com.peseoane.galaria.xestion.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "etiqueta", schema = "inventario", indexes = {
        @Index(name = "UQ_pegatina_identificador", columnList = "identificador", unique = true)
})
public class Etiqueta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "referencia", nullable = false, length = 256)
    private String referencia;

    @OneToMany(mappedBy = "etiqueta", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Incidencia> incidenciasAsociadas = new LinkedHashSet<>();

    @OneToMany(mappedBy = "etiqueta", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Log> logsAsociados = new LinkedHashSet<>();

    @OneToOne(mappedBy = "etiqueta", cascade = CascadeType.ALL, orphanRemoval = true)
    private Monitor monitorAsociado;

    @OneToOne(mappedBy = "etiqueta", cascade = CascadeType.ALL, orphanRemoval = true)
    private Ordenador ordenadorAsociado;

    @OneToMany(mappedBy = "etiqueta", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Trazabilidad> trazasAsociadas = new LinkedHashSet<>();

}