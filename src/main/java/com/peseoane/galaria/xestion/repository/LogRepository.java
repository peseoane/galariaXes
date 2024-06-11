package com.peseoane.galaria.xestion.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.peseoane.galaria.xestion.model.Log;

public interface LogRepository extends JpaRepository<Log, Integer> {
    List<Log> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    List<Log> findTop1024ByOrderByFechaDesc();

    @Query("""
    SELECT l FROM Log l 
    JOIN l.etiqueta e 
    JOIN l.nivel n 
    WHERE l.descripcion LIKE %:busqueda% 
    OR e.referencia LIKE %:busqueda% 
    OR n.nivel LIKE %:busqueda%
    ORDER BY l.fecha Desc
    """
    )
    List<Log> buscarEnTodosCampos(@Param("busqueda") String busqueda);

List<Log> findByFechaBetweenOrderByFechaDesc(LocalDateTime inicio, LocalDateTime fin);}