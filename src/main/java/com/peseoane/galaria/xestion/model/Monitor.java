package com.peseoane.galaria.xestion.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "monitor", schema = "inventario",
       indexes = {
           @Index(name = "idx_monitor_modelo_monitor", columnList = "modelo_monitor"),
           @Index(name = "idx_monitor_etiqueta", columnList = "etiqueta"),
           @Index(name = "idx_monitor_numero_serie", columnList = "numero_serie")
       }
)
public class Monitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "etiqueta")
    private Etiqueta etiqueta;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ordenador")
    private Ordenador ordenador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato")
    private Contrato contrato;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "modelo_monitor", nullable = false)
    private ModeloMonitor modeloMonitor;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "propuesto_retirada")
    private Boolean propuestoRetirada;

    @Column(name = "numero_serie", nullable = false, length = 256)
    private String numeroSerie;

    @Column(name = "garantia")
    private LocalDateTime garantia;

    @ColumnDefault("getdate()")
    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fechaAlta = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "fecha_baja")
    private LocalDateTime fechaBaja;

    @PrePersist
    public void prePersist() {
        this.fechaAlta = LocalDateTime.now();
        this.propuestoRetirada = false;
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

}