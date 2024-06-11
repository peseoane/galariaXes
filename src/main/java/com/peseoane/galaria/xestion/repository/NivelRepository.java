package com.peseoane.galaria.xestion.repository;

import com.peseoane.galaria.xestion.model.Nivel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NivelRepository extends JpaRepository<Nivel, Integer> {

    boolean existsByNivel(String nivelSolicitado);

    Nivel findByNivel(String nivelSolicitado);
}