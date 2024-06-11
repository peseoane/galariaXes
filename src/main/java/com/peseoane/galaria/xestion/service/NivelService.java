package com.peseoane.galaria.xestion.service;

import com.peseoane.galaria.xestion.model.Nivel;
import com.peseoane.galaria.xestion.repository.NivelRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NivelService {

    private final NivelRepository nivelRepository;

    public NivelService(NivelRepository nivelRepository) {
        this.nivelRepository = nivelRepository;
    }

    @Transactional
    public Nivel procesarNivel(String nivelSolicitado) {
        if (nivelRepository.existsByNivel(nivelSolicitado)) {
            return nivelRepository.findByNivel(nivelSolicitado);
        }
        Nivel nivel = new Nivel();
        nivel.setNivel(nivelSolicitado);
        nivelRepository.save(nivel);
        return nivel;

    }

    public List<Nivel> obtenerTodosLosNiveles() {
        return nivelRepository.findAll();
    }
}