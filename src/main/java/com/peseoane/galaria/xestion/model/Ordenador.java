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
@Table(name = "ordenador", schema = "inventario", indexes = {
        @Index(name = "idx_numero_serie", columnList = "numero_serie"),
        @Index(name = "idx_ordenador_tipo_elemento", columnList = "tipo_elemento"),
        @Index(name = "idx_ordenador_sistema_operativo", columnList = "sistema_operativo"),
        @Index(name = "idx_ordenador_modelo_ordenador", columnList = "modelo_ordenador"),
        @Index(name = "idx_ordenador_etiqueta", columnList = "etiqueta"),
        @Index(name = "idx_ordenador_numero_serie", columnList = "numero_serie"),
        @Index(name = "UQ_ordenador_etiqueta", columnList = "etiqueta", unique = true)
})
public class Ordenador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_elemento", nullable = false)
    private TipoElemento tipoElemento;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "etiqueta", nullable = false)
    private Etiqueta etiqueta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato")
    private Contrato contrato;

    @Column(name = "numero_serie", nullable = false, length = 256)
    private String numeroSerie;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "modelo_ordenador", nullable = false)
    private ModeloOrdenador modeloOrdenador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sistema_operativo")
    private SistemaOperativo sistemaOperativo;

    @Column(name = "ram")
    private Long ram;

    @Column(name = "garantia")
    private LocalDateTime garantia;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "propuesto_retirada")
    private Boolean propuestoRetirada;

    @Column(name = "notas", length = 1024, columnDefinition = "nvarchar(1024)")
    private String notas;

    @ColumnDefault("getdate()")
    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fechaAlta;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "fecha_baja")
    private LocalDateTime fechaBaja;

    @OneToMany(mappedBy = "ordenador")
    private Set<Monitor> monitoresAsociados = new LinkedHashSet<>();

    @OneToMany(mappedBy = "ordenador", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrdenadorDicom> configuracionesDIcom = new LinkedHashSet<>();

    @OneToMany(mappedBy = "ordenador", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrdenadorGpu> gpusAsociadas = new LinkedHashSet<>();

    @OneToMany(mappedBy = "ordenador", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrdenadorNetwork> interfacesRedAsociadas = new LinkedHashSet<>();

    @OneToMany(mappedBy = "ordenador", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrdenadorProcesador> procesadoresAsociados = new LinkedHashSet<>();

    @PrePersist
    public void prePersist() {
        this.fechaAlta = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        this.propuestoRetirada = false;
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

}