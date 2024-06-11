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
@Table(name = "tipo_elemento", schema = "inventario", indexes = {
        @Index(name = "tipo_unique", columnList = "tipo", unique = true)
})
public class TipoElemento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "tipo", length = 64)
    private String tipo;

    @OneToMany(mappedBy = "tipoElemento")
    private Set<Ordenador> ordenadoresFiltradosTipo = new LinkedHashSet<>();

}