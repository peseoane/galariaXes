package com.peseoane.galaria.xestion.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "sistema_operativo", schema = "inventario", indexes = {
        @Index(name = "sistema_operativo_unique", columnList = "nombre, compilacion, version", unique = true),
        @Index(name="idx_sistema_operativo_nombre_compilacion_version", columnList = "nombre, compilacion, version"),
        @Index(name = "idx_sistema_operativo_codename", columnList = "codename"),
})
public class SistemaOperativo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nombre", length = 64)
    private String nombre;

    @Column(name = "compilacion", length = 64)
    private String compilacion;

    @Column(name = "version", length = 64)
    private String version;

    @Column(name = "codename", length = 64)
    private String codename;

    @ColumnDefault("getdate()")
    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fechaAlta;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "fecha_baja")
    private LocalDateTime fechaBaja;

    @OneToMany(mappedBy = "sistemaOperativo")
    private Set<Ordenador> ordenadoresSistemaOperativoInstalado = new LinkedHashSet<>();

}