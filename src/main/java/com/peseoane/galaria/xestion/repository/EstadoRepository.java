package com.peseoane.galaria.xestion.repository;

import com.peseoane.galaria.xestion.model.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Integer> {


}