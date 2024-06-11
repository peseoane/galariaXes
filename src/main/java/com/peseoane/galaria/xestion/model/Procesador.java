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
@Table(name = "procesador", schema = "inventario", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_procesador", columnNames = {"referencia"})
})
public class Procesador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "referencia", length = 128)
    private String referencia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fabricante", nullable = false)
    private Fabricante fabricante;

    @Column(name = "velocidad")
    private Integer velocidad;

    @Column(name = "nucleos")
    private Integer nucleos;

    @Column(name = "hilos")
    private Integer hilos;

    @OneToMany(mappedBy = "procesador")
    private Set<OrdenadorProcesador> ordenadoresAsociados = new LinkedHashSet<>();

}