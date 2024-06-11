package com.peseoane.galaria.xestion.repository;

import com.peseoane.galaria.xestion.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicioRepository extends JpaRepository<Servicio, Integer> {
    Servicio findByServicio(String s);

    boolean existsByServicio(String s);
}