package com.peseoane.galaria.xestion.repository;

import com.peseoane.galaria.xestion.model.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContratoRepository extends JpaRepository<Contrato, Integer> {
    boolean existsByReferencia(String referencia);

    boolean existsByReferenciaContaining(String busqueda);

    List<Contrato> findByReferenciaContaining(String busqueda);
}