package com.peseoane.galaria.xestion.repository;

import com.peseoane.galaria.xestion.model.Fabricante;
import com.peseoane.galaria.xestion.model.ModeloOrdenador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModeloOrdenadorRepository extends JpaRepository<ModeloOrdenador, Integer> {

    boolean existsByReferenciaAndFabricante(String referencia, Fabricante fabricante);

    ModeloOrdenador findByReferenciaAndFabricante(String referencia, Fabricante fabricante);

    boolean existsByReferenciaContaining(String busqueda);

    List<ModeloOrdenador> findByReferenciaContaining(String busqueda);
}