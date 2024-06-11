package com.peseoane.galaria.xestion.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "contrato", schema = "inventario", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_contrato", columnNames = {"referencia"})
})
public class Contrato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "referencia", length = 256)
    private String referencia;

    @Column(name = "fecha_compra")
    private LocalDateTime fechaCompra;

    @Column(name = "notas", length = 1024, columnDefinition = "nvarchar(1024)")
    private String notas;

    @ColumnDefault("getdate()")
    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fechaAlta;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @ColumnDefault("3")
    @Column(name = "periodo_garantia", nullable = false)
    private Integer periodoGarantia;

    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Ordenador> ordenadores = new LinkedHashSet<>();

    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Monitor> monitores = new LinkedHashSet<>();

    @PrePersist
    public void prePersist() {
        this.fechaAlta = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

}