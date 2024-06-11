package com.peseoane.galaria.xestion.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "incidencia", schema = "inventario", indexes = {
        @Index(name = "idx_incidencia_estado", columnList = "estado"),
        @Index(name = "idx_incidencia_nivel", columnList = "nivel"),
        @Index(name = "idx_incidencia_etiqueta", columnList = "etiqueta")
})
public class Incidencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "etiqueta", nullable = false)
    private Etiqueta etiqueta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estado", nullable = false)
    private Estado estado;

    @Column(name = "descripcion", length = 8192, columnDefinition = "nvarchar(8192)")
    private String descripcion;

    @ColumnDefault("getdate()")
    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fechaAlta;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "fecha_baja")
    private LocalDateTime fechaBaja;

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