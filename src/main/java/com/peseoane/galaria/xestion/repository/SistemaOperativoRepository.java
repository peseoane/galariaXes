package com.peseoane.galaria.xestion.repository;

import com.peseoane.galaria.xestion.model.SistemaOperativo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SistemaOperativoRepository extends JpaRepository<SistemaOperativo, Integer> {
    boolean existsByNombreAndCompilacionAndVersion(String nombre, String compilacion, String version);

    SistemaOperativo findByNombreAndCompilacionAndVersion(String nombre, String compilacion, String version);
}