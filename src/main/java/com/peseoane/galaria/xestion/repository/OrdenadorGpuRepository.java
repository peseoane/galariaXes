package com.peseoane.galaria.xestion.repository;

import com.peseoane.galaria.xestion.model.Gpu;
import com.peseoane.galaria.xestion.model.Ordenador;
import com.peseoane.galaria.xestion.model.OrdenadorGpu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenadorGpuRepository extends JpaRepository<OrdenadorGpu, Integer> {
    boolean existsByOrdenadorAndGpuAndFechaBajaNull(Ordenador ordenador, Gpu gpu);

    OrdenadorGpu findByOrdenadorAndGpuAndFechaBajaNull(Ordenador ordenador, Gpu gpu);
}