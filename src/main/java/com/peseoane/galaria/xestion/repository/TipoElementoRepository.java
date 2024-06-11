package com.peseoane.galaria.xestion.repository;

import com.peseoane.galaria.xestion.model.TipoElemento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoElementoRepository extends JpaRepository<TipoElemento, Integer> {
    TipoElemento findByTipo(String ordenador);
}