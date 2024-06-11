package com.peseoane.galaria.xestion.service;

import com.peseoane.galaria.xestion.model.Estado;
import com.peseoane.galaria.xestion.repository.EstadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoService {

    private final EstadoRepository estadoRepository;

    public EstadoService(EstadoRepository estadoRepository) {
        this.estadoRepository = estadoRepository;
    }

    public List<Estado> obtenerTodosLosEstados() {
        return estadoRepository.findAll();
    }

}