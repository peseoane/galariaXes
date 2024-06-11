package com.peseoane.galaria.xestion.repository;

import com.peseoane.galaria.xestion.model.Etiqueta;
import com.peseoane.galaria.xestion.model.TipoElemento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EtiquetaRepository extends JpaRepository<Etiqueta, Integer> {
    boolean existsByReferencia(String xmlEtiqueta);

    Etiqueta findByReferencia(String xmlEtiqueta);

    boolean existsByReferenciaContaining(String busqueda);

    List<Etiqueta> findByReferenciaContaining(String busqueda);

    @Query("SELECT e FROM Etiqueta e JOIN Ordenador o ON e.id = o.etiqueta.id WHERE o.tipoElemento IN :targetType")
    List<Etiqueta> findByTipoElementoIn(List<TipoElemento> targetType);

    @Query(value = """
            SELECT DISTINCT e.id, e.referencia FROM inventario.etiqueta e
            LEFT JOIN inventario.ordenador o ON e.id = o.etiqueta
            LEFT JOIN inventario.monitor m ON e.id = m.etiqueta
            WHERE m.etiqueta IS NOT NULL
            """, nativeQuery = true)
    List<Etiqueta> findAll();


}