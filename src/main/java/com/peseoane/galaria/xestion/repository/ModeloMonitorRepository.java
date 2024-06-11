package com.peseoane.galaria.xestion.repository;

import com.peseoane.galaria.xestion.model.Fabricante;
import com.peseoane.galaria.xestion.model.ModeloMonitor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModeloMonitorRepository extends JpaRepository<ModeloMonitor, Integer> {
    boolean existsByReferenciaAndFabricante(String referencia, Fabricante fabricante);

    ModeloMonitor findByReferenciaAndFabricante(String referencia, Fabricante fabricante);
}