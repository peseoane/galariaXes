package com.peseoane.galaria.xestion.repository;

import com.peseoane.galaria.xestion.model.Localizacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalizacionRepository extends JpaRepository<Localizacion, Integer> {

    boolean existsByLocalizacion(String lugar);

    Localizacion findByLocalizacion(String lugar);
}