package com.peseoane.galaria.xestion.views;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "vista_monitores", schema = "inventario")
public class VistaMonitores {
    @Id
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "Etiqueta", nullable = false, length = 256)
    private String etiqueta;

    @Nationalized
    @Column(name = "\"Número de Serie\"", nullable = false, length = 256)
    private String nMeroDeSerie;

    @Nationalized
    @Column(name = "Modelo", length = 64)
    private String modelo;

    @Nationalized
    @Column(name = "Servicio", length = 128)
    private String servicio;

    @Nationalized
    @Column(name = "\"Localización\"", length = 128)
    private String localización;

    @Nationalized
    @Column(name = "Contrato", length = 256)
    private String contrato;

    @Nationalized
    @Column(name = "\"Garantía\"", length = 4000)
    private String garantía;

    @Column(name = "Activo")
    private Boolean activo;

    @Column(name = "\"Propuesto Retirada\"")
    private Boolean propuestoRetirada;

    @Column(name = "\"Fecha de Alta\"", length = 30)
    private String fechaDeAlta;

    @Column(name = "\"Fecha de Actualización\"", length = 30)
    private String fechaDeActualizaciN;

    @Column(name = "\"Fecha de Baja\"", length = 30)
    private String fechaDeBaja;

}