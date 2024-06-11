package com.peseoane.galaria.xestion.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "logs", schema = "inventario", indexes = {@Index(name = "idx_logs_etiqueta", columnList = "etiqueta")})
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etiqueta")
    private Etiqueta etiqueta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nivel", nullable = false)
    private Nivel nivel;

    @ColumnDefault("getdate()")
    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "descripcion", length = 1024)
    private String descripcion;

    @PrePersist
    public void prePersist() {
        this.fecha = LocalDateTime.now();
    }

}