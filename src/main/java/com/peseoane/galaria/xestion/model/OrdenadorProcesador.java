package com.peseoane.galaria.xestion.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "ordenador_procesador", schema = "inventario", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_ordenador_procesador", columnNames = {"ordenador", "procesador"})
})
public class OrdenadorProcesador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ordenador", nullable = false)
    private Ordenador ordenador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "procesador", nullable = false)
    private Procesador procesador;

    @ColumnDefault("getdate()")
    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fechaAlta;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "fecha_baja")
    private LocalDateTime fechaBaja;

}