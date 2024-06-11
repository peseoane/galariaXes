package com.peseoane.galaria.xestion.repository;

import com.peseoane.galaria.xestion.model.Contrato;
import com.peseoane.galaria.xestion.model.Etiqueta;
import com.peseoane.galaria.xestion.model.Ordenador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrdenadorRepository extends JpaRepository<Ordenador, Integer> {
    boolean existsByNumeroSerie(String numeroSerie);

    Ordenador findByNumeroSerie(String numeroSerie);

    Ordenador findByEtiqueta(Etiqueta etiqueta);

    @Query(value = """
            SELECT DISTINCT o FROM Ordenador o
            LEFT JOIN o.etiqueta e 
            LEFT JOIN o.modeloOrdenador m 
            LEFT JOIN o.sistemaOperativo s 
            LEFT JOIN o.tipoElemento t 
            LEFT JOIN o.monitoresAsociados mon 
            LEFT JOIN mon.etiqueta et
            LEFT JOIN mon.modeloMonitor mm 
            LEFT JOIN o.interfacesRedAsociadas r
            LEFT JOIN o.gpusAsociadas g 
            LEFT JOIN o.contrato con
            WHERE o.numeroSerie LIKE CONCAT('%', :busqueda, '%') 
            OR e.referencia LIKE CONCAT('%', :busqueda, '%') 
            OR m.referencia LIKE CONCAT('%', :busqueda, '%') 
            OR s.nombre LIKE CONCAT('%', :busqueda, '%') 
            OR s.version LIKE CONCAT('%', :busqueda, '%') 
            OR s.codename LIKE CONCAT('%', :busqueda, '%') 
            OR s.compilacion LIKE CONCAT('%', :busqueda, '%') 
            OR t.tipo LIKE CONCAT('%', :busqueda, '%') 
            OR o.notas LIKE CONCAT('%', :busqueda, '%')
            OR mon.numeroSerie LIKE CONCAT('%', :busqueda, '%') 
            OR et.referencia LIKE CONCAT('%', :busqueda, '%')
            OR mm.referencia LIKE CONCAT('%', :busqueda, '%') 
            OR r.ipv4 LIKE CONCAT('%', :busqueda, '%') 
            OR r.mac LIKE CONCAT('%', :busqueda, '%') 
            OR r.bocaRed LIKE CONCAT('%', :busqueda, '%') 
            OR r.notas LIKE CONCAT('%', :busqueda, '%') 
            OR con.referencia LIKE CONCAT('%', :busqueda, '%')
            """)
    List<Ordenador> buscarEnTodosLosCampos(@Param("busqueda") String busqueda);


    @Query("""
            SELECT DISTINCT o.id FROM Ordenador o
            LEFT JOIN o.etiqueta e 
            LEFT JOIN o.modeloOrdenador m 
            LEFT JOIN o.sistemaOperativo s 
            LEFT JOIN o.tipoElemento t 
            LEFT JOIN o.monitoresAsociados mon 
            LEFT JOIN mon.etiqueta etMon
            LEFT JOIN mon.modeloMonitor mm 
            LEFT JOIN o.interfacesRedAsociadas r
            LEFT JOIN o.gpusAsociadas g 
            LEFT JOIN o.etiqueta.trazasAsociadas ta
            LEFT JOIN ta.servicio ser
            LEFT JOIN ta.localizacion loc
                        LEFT JOIN o.contrato con

            WHERE o.numeroSerie LIKE CONCAT('%', :busqueda, '%') 
            OR e.referencia LIKE CONCAT('%', :busqueda, '%') 
            OR m.referencia LIKE CONCAT('%', :busqueda, '%') 
            OR s.nombre LIKE CONCAT('%', :busqueda, '%') 
            OR s.version LIKE CONCAT('%', :busqueda, '%') 
            OR s.compilacion LIKE CONCAT('%', :busqueda, '%') 
            OR s.codename LIKE CONCAT('%', :busqueda, '%') 
            OR t.tipo LIKE CONCAT('%', :busqueda, '%') 
            OR o.notas LIKE CONCAT('%', :busqueda, '%')
            OR mon.numeroSerie LIKE CONCAT('%', :busqueda, '%') 
            OR etMon.referencia LIKE CONCAT('%', :busqueda, '%')
            OR mm.referencia LIKE CONCAT('%', :busqueda, '%') 
            OR r.ipv4 LIKE CONCAT('%', :busqueda, '%') 
            OR r.mac LIKE CONCAT('%', :busqueda, '%') 
            OR r.bocaRed LIKE CONCAT('%', :busqueda, '%') 
            OR r.notas LIKE CONCAT('%', :busqueda, '%') 
            OR ser.servicio LIKE CONCAT('%', :busqueda, '%')
            OR loc.localizacion LIKE CONCAT('%', :busqueda, '%')
            OR ta.notas LIKE CONCAT('%', :busqueda, '%')
            OR con.referencia LIKE CONCAT('%', :busqueda, '%')
            """)
    List<Integer> getIdByQueryInOrdenadores(String busqueda);


    @Query("SELECT o.id FROM Ordenador o")
    List<Integer> findAllByIdAsInt();

    @Query("SELECT o FROM Ordenador o JOIN o.etiqueta e ORDER BY e.referencia DESC")
    List<Ordenador> findAllByEtiquetaOrderDesc();


    List<Ordenador> findByContratoId(Integer id);

    List<Ordenador> findByContrato(Contrato contratoActualizado);

    boolean existsByEtiqueta(Etiqueta etiqueta);

    List<Ordenador> findByGarantiaIsNotNullAndGarantiaGreaterThan(LocalDateTime now);

    List<Ordenador> findByGarantiaIsNotNullAndGarantiaLessThanEqual(LocalDateTime now);
}