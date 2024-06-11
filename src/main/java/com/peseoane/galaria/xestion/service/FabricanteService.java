package com.peseoane.galaria.xestion.service;

import com.peseoane.galaria.xestion.model.Fabricante;
import com.peseoane.galaria.xestion.repository.FabricanteRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FabricanteService {

    private static final Logger log = LoggerFactory.getLogger(FabricanteService.class);
    private final FabricanteRepository fabricanteRepository;

    public FabricanteService(FabricanteRepository fabricanteRepository) {
        this.fabricanteRepository = fabricanteRepository;
    }

    @Transactional
    public Fabricante procesarFabricante(String xmlFabricante) {
        try {
            Fabricante entidadFabricante;
            if (fabricanteRepository.existsByReferencia(xmlFabricante)) {
                return fabricanteRepository.findByReferencia(xmlFabricante);
            }
            entidadFabricante = new Fabricante();
            entidadFabricante.setReferencia(xmlFabricante);
            fabricanteRepository.save(entidadFabricante);
            fabricanteRepository.flush();
            return entidadFabricante;
        } catch (Exception e) {
            log.error("ERROR FABRICANTE " + xmlFabricante + " ---- " + e.getMessage());
            return null;
        }
    }

    @Transactional
    public boolean procesarFabricante(Fabricante fabricante) {
        try {
            Fabricante entidadFabricante;
            if (fabricanteRepository.existsByReferencia(fabricante.getReferencia())) {
                return false;
            }
            entidadFabricante = new Fabricante();
            entidadFabricante.setReferencia(fabricante.getReferencia());
            fabricanteRepository.save(entidadFabricante);
            fabricanteRepository.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Fabricante> obtenerTodos() {
        return fabricanteRepository.findAll();
    }
}