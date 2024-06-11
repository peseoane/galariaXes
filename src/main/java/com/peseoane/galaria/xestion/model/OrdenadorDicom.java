package com.peseoane.galaria.xestion.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "ordenador_dicom", schema = "inventario", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_dicom_configuracion", columnNames = {"ae_title_query", "ae_title_retrieve", "server_url", "server_port"})
})
public class OrdenadorDicom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ordenador", nullable = false)
    private Ordenador ordenador;

    @Column(name = "ae_title_query")
    private Integer aeTitleQuery;

    @Column(name = "ae_title_retrieve")
    private Integer aeTitleRetrieve;

    @Column(name = "server_url", length = 256)
    private String serverUrl;

    @Column(name = "server_port")
    private Integer serverPort;

    @Column(name = "notas", length = 1024, columnDefinition = "nvarchar(1024)")
    private String notas;

    @ColumnDefault("getdate()")
    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fechaAlta;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "fecha_baja")
    private LocalDateTime fechaBaja;

}