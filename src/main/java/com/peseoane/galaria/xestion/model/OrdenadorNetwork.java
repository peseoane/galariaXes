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
@Table(name = "ordenador_network", schema = "inventario", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_ordenador_network", columnNames = {"ordenador", "ipv4", "mac"})
})
public class OrdenadorNetwork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ordenador", nullable = false)
    private Ordenador ordenador;

    @Column(name = "ipv4",length = 15)
    private String ipv4;

    @Column(name = "mac", length = 17, columnDefinition = "nchar(12)")
    private String mac;

    @Column(name = "boca_red", length = 64)
    private String bocaRed;

    @Column(name = "notas", length = 1024)
    private String notas;

    @ColumnDefault("getdate()")
    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fechaAlta;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "fecha_baja")
    private LocalDateTime fechaBaja;

}