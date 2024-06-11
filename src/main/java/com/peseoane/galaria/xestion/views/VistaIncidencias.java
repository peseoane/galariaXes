package com.peseoane.galaria.xestion.views;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Nationalized;

/**
 * Mapping for DB view
 */
@Getter
@Setter
@Entity
@Immutable
@Table(name = "vista_incidencias", schema = "inventario")
public class VistaIncidencias {
    @Id
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "Etiqueta", nullable = false, length = 256)
    private String etiqueta;

    @Nationalized
    @Column(name = "Estado", length = 32)
    private String estado;

    @Nationalized
    @Lob
    @Column(name = "\"Descripción\"")
    private String descripción;

    @Nationalized
    @Column(name = "Servicio", length = 128)
    private String servicio;

    @Nationalized
    @Column(name = "\"Localización\"", length = 128)
    private String localización;

    @Nationalized
    @Column(name = "\"Fecha de Alta\"", length = 4000)
    private String fechaDeAlta;

    @Column(name = "\"Fecha de Actualización\"", length = 30)
    private String fechaDeActualizaciN;

    @Column(name = "\"Fecha de Baja\"", length = 30)
    private String fechaDeBaja;

}