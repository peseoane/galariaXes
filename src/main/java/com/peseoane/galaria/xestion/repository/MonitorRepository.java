package com.peseoane.galaria.xestion.repository;

import com.peseoane.galaria.xestion.model.Contrato;
import com.peseoane.galaria.xestion.model.Etiqueta;
import com.peseoane.galaria.xestion.model.Monitor;
import com.peseoane.galaria.xestion.model.Ordenador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface MonitorRepository extends JpaRepository<Monitor, Integer> {
    boolean existsByEtiqueta(Etiqueta etiqueta);

    Monitor findByEtiqueta(Etiqueta etiqueta);

    boolean existsByNumeroSerie(String numeroSerie);

    Monitor findByNumeroSerie(String numeroSerie);

    List<Monitor> findAllByOrdenador(Ordenador ordenador);

    @Query("""
            SELECT DISTINCT m.id FROM Monitor m
            LEFT JOIN m.etiqueta e 
            LEFT JOIN m.modeloMonitor mm 
            LEFT JOIN m.ordenador o 
            LEFT JOIN m.contrato con
            WHERE m.numeroSerie LIKE CONCAT('%', :busqueda, '%') 
            OR e.referencia LIKE CONCAT('%', :busqueda, '%') 
            OR mm.referencia LIKE CONCAT('%', :busqueda, '%') 
            OR o.numeroSerie LIKE CONCAT('%', :busqueda, '%') 
            OR o.etiqueta.referencia LIKE CONCAT('%', :busqueda, '%')
            OR o.notas LIKE CONCAT('%', :busqueda, '%')
            OR con.referencia LIKE CONCAT('%', :busqueda, '%')
            """)
    List<Integer> getIdByQueryInMonitores(String busqueda);

    @Query("""
            SELECT DISTINCT m.id FROM Monitor m
            """)
    List<Integer> getIdByAllMonitores();

    @Query("SELECT m FROM Monitor m JOIN m.etiqueta e ORDER BY e.referencia DESC")
    List<Monitor> findAllByEtiquetaOrderDesc();

    List<Monitor> findByContrato(Contrato contratoActualizado);


    List<Monitor> findByGarantiaIsNotNullAndGarantiaGreaterThan(LocalDateTime now);

    List<Monitor> findByGarantiaIsNotNullAndGarantiaLessThanEqual(LocalDateTime now);
}