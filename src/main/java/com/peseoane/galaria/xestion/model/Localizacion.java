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
@Table(name = "localizacion", schema = "inventario", uniqueConstraints = {
        @UniqueConstraint(name = "localizacion_unique", columnNames = {"localizacion"})
})
public class Localizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "localizacion", length = 128)
    private String localizacion;

    @OneToMany(mappedBy = "localizacion")
    private Set<Trazabilidad> trazasPorLocalizacion = new LinkedHashSet<>();

}