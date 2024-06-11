package com.peseoane.galaria.xestion.service;

import com.peseoane.galaria.xestion.repository.VistaIncidenciaRepository;
import com.peseoane.galaria.xestion.repository.VistaMonitoreRepository;
import com.peseoane.galaria.xestion.repository.VistaOrdenadoreRepository;
import com.peseoane.galaria.xestion.views.VistaIncidencias;
import com.peseoane.galaria.xestion.views.VistaMonitores;
import com.peseoane.galaria.xestion.views.VistaOrdenadores;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VistaService {

    private final VistaIncidenciaRepository vistaIncidenciaRepository;
    private final VistaMonitoreRepository vistaMonitoreRepository;
    private final VistaOrdenadoreRepository vistaOrdenadoreRepository;

    public VistaService(VistaIncidenciaRepository vistaIncidenciaRepository, VistaMonitoreRepository vistaMonitoreRepository, VistaOrdenadoreRepository vistaOrdenadoreRepository) {
        this.vistaIncidenciaRepository = vistaIncidenciaRepository;
        this.vistaMonitoreRepository = vistaMonitoreRepository;
        this.vistaOrdenadoreRepository = vistaOrdenadoreRepository;
    }

    public List<VistaOrdenadores> getOrdenadores() {
        return vistaOrdenadoreRepository.findAll();
    }

    public List<VistaMonitores> getMonitores() {
        return vistaMonitoreRepository.findAll();
    }

    public List<VistaIncidencias> getIncidencias() {
        return vistaIncidenciaRepository.findAll();
    }

}