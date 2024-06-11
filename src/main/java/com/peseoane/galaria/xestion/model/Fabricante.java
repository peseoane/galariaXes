package com.peseoane.galaria.xestion.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "fabricante", schema = "inventario", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_fabricante", columnNames = {"referencia"})
})
public class Fabricante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "referencia", length = 32)
    private String referencia;

    @OneToMany(mappedBy = "fabricante")
    private Set<Gpu> gpusAsociadas = new LinkedHashSet<>();

    @OneToMany(mappedBy = "fabricante")
    private Set<ModeloMonitor> modelosMonitorAsociados = new LinkedHashSet<>();

    @OneToMany(mappedBy = "fabricante")
    private Set<ModeloOrdenador> modeloPcsAsociados = new LinkedHashSet<>();

    @OneToMany(mappedBy = "fabricante")
    private Set<Procesador> procesadoresAsociados = new LinkedHashSet<>();

}