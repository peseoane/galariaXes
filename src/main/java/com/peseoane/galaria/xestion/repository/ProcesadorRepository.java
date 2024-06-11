package com.peseoane.galaria.xestion.repository;

import com.peseoane.galaria.xestion.model.Fabricante;
import com.peseoane.galaria.xestion.model.Procesador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcesadorRepository extends JpaRepository<Procesador, Integer> {
    boolean existsByReferenciaAndFabricante(String referencia, Fabricante fabricante);

    Procesador findByReferenciaAndFabricante(String referencia, Fabricante fabricante);
}