package com.peseoane.galaria.xestion.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "nivel", schema = "inventario", uniqueConstraints = {
        @UniqueConstraint(name = "nivel_unique", columnNames = {"nivel"})
})
public class Nivel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nivel", length = 32)
    private String nivel;


}