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
@Table(name = "servicio", schema = "inventario", uniqueConstraints = {
        @UniqueConstraint(name = "servicio_unique", columnNames = {"servicio"})
})
public class Servicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "servicio", length = 128)
    private String servicio;

    @OneToMany(mappedBy = "servicio")
    private Set<Trazabilidad> trazasAsociadas = new LinkedHashSet<>();

}