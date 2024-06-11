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
@Table(name = "modelo_ordenador", schema = "inventario", indexes = {
        @Index(name = "idx_modelo_pc_fabricante", columnList = "fabricante")
})
public class ModeloOrdenador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fabricante", nullable = false)
    private Fabricante fabricante;

    @Column(name = "referencia", length = 64)
    private String referencia;

    @ColumnDefault("getdate()")
    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fechaAlta = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "fecha_baja")
    private LocalDateTime fechaBaja;

    @OneToMany(mappedBy = "modeloOrdenador")
    private Set<Ordenador> ordenadoresAsociados = new LinkedHashSet<>();

}