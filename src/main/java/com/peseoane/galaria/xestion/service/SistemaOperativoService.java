package com.peseoane.galaria.xestion.service;

import com.peseoane.galaria.xestion.model.SistemaOperativo;
import com.peseoane.galaria.xestion.repository.SistemaOperativoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service

public class SistemaOperativoService {

    private final SistemaOperativoRepository sistemaOperativoRepository;
    private final LogService logService;

    public SistemaOperativoService(SistemaOperativoRepository sistemaOperativoRepository, LogService logService) {
        this.sistemaOperativoRepository = sistemaOperativoRepository;
        this.logService = logService;
    }

    @Transactional
    public SistemaOperativo procesarSistemaOperativo(String nombre,
                                                     String compilacion,
                                                     String version,
                                                     String codename) {
        LocalDateTime fechaTransaccion = LocalDateTime.now();
        SistemaOperativo sistemaOperativo;
        if (sistemaOperativoRepository.existsByNombreAndCompilacionAndVersion(nombre, compilacion, version)) {
            return sistemaOperativoRepository.findByNombreAndCompilacionAndVersion(nombre, compilacion, version);
        }
        sistemaOperativo = new SistemaOperativo();
        sistemaOperativo.setFechaAlta(fechaTransaccion);
        logService.autoLog("INFO",
                           "Nuevo SO registrado: " + nombre + " " + compilacion + " " + version + " " + codename);
        sistemaOperativo.setNombre(nombre);
        sistemaOperativo.setCompilacion(compilacion);
        sistemaOperativo.setVersion(version);
        sistemaOperativo.setCodename(codename);
        sistemaOperativo.setFechaActualizacion(fechaTransaccion);
        sistemaOperativoRepository.save(sistemaOperativo);
        return sistemaOperativo;
    }

    public SistemaOperativo obtenerPorId(Integer id) {
        return sistemaOperativoRepository.findById(id).orElse(null);
    }

    public List<SistemaOperativo> obtenerTodos() {
        return sistemaOperativoRepository.findAll();
    }
}