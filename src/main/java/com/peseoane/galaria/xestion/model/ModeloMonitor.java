package com.peseoane.galaria.xestion.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "modelo_monitor", schema = "inventario", indexes = {
        @Index(name = "idx_modelo_monitor_fabricante", columnList = "fabricante"),
        @Index(name = "idx_modelo_monitor", columnList = "referencia")
})
public class ModeloMonitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "referencia", length = 64)
    private String referencia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fabricante", nullable = false)
    private Fabricante fabricante;

    @OneToMany(mappedBy = "modeloMonitor")
    private Set<Monitor> monitoresAsociados = new LinkedHashSet<>();

}