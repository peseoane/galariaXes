package com.peseoane.galaria.xestion.repository;

import com.peseoane.galaria.xestion.model.Fabricante;
import com.peseoane.galaria.xestion.model.Gpu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GpuRepository extends JpaRepository<Gpu, Integer> {
    boolean existsByReferenciaAndFabricante(String referencia, Fabricante fabricante);

    Gpu findByReferenciaAndFabricante(String referencia, Fabricante fabricante);
}