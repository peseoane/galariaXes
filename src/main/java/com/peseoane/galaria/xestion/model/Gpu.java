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
@Table(name = "gpu", schema = "inventario", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_gpu", columnNames = {"fabricante, referencia"})
})
public class Gpu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fabricante")
    private Fabricante fabricante;

    @Column(name = "referencia", length = 128)
    private String referencia;

    @OneToMany(mappedBy = "gpu")
    private Set<OrdenadorGpu> ordenadoresPorGpu = new LinkedHashSet<>();

}