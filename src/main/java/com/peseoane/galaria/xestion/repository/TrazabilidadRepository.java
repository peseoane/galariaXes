package com.peseoane.galaria.xestion.repository;

import com.peseoane.galaria.xestion.model.Etiqueta;
import com.peseoane.galaria.xestion.model.Trazabilidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrazabilidadRepository extends JpaRepository<Trazabilidad, Integer> {
    boolean existsByEtiquetaAndFechaBajaNull(Etiqueta etiqueta);

    Trazabilidad findByEtiquetaAndFechaBajaNull(Etiqueta etiqueta);
}