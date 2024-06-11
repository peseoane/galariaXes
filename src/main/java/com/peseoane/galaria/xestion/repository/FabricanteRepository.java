package com.peseoane.galaria.xestion.repository;

import com.peseoane.galaria.xestion.model.Fabricante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FabricanteRepository extends JpaRepository<Fabricante, Integer> {
    boolean existsByReferencia(String xmlFabricante);

    Fabricante findByReferencia(String xmlFabricante);
}