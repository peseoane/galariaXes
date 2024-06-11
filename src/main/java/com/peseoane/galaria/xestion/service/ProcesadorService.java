package com.peseoane.galaria.xestion.service;

import com.peseoane.galaria.xestion.model.Fabricante;
import com.peseoane.galaria.xestion.model.Procesador;
import com.peseoane.galaria.xestion.repository.ProcesadorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ProcesadorService {

    private final ProcesadorRepository procesadorRepository;

    public ProcesadorService(ProcesadorRepository procesadorRepository) {
        this.procesadorRepository = procesadorRepository;
    }


    @Transactional
    public Procesador procesarProcesador(String referencia, Fabricante fabricante, int velocidad, int nucleos, int hilos) {
        if (procesadorRepository.existsByReferenciaAndFabricante(referencia, fabricante)) {
            return procesadorRepository.findByReferenciaAndFabricante(referencia, fabricante);
        }
        Procesador entidadProcesador = new Procesador();
        entidadProcesador.setReferencia(referencia);
        entidadProcesador.setFabricante(fabricante);
        entidadProcesador.setVelocidad(velocidad);
        entidadProcesador.setNucleos(nucleos);
        entidadProcesador.setHilos(hilos);
        procesadorRepository.save(entidadProcesador);
        return entidadProcesador;
    }

}