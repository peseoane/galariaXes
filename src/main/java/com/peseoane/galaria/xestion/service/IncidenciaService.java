package com.peseoane.galaria.xestion.service;

import com.peseoane.galaria.xestion.model.Etiqueta;
import com.peseoane.galaria.xestion.model.Incidencia;
import com.peseoane.galaria.xestion.repository.IncidenciaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class IncidenciaService {
    private final IncidenciaRepository incidenciaRepository;

    public IncidenciaService(IncidenciaRepository incidenciaRepository) {
        this.incidenciaRepository = incidenciaRepository;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void guardarIncidencia(Incidencia incidencia) {
        incidenciaRepository.save(incidencia);
        incidenciaRepository.flush();
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void eliminarIncidencia(Integer id) {
        incidenciaRepository.deleteById(id);
        incidenciaRepository.flush();
    }

    public Etiqueta obtenerEtiquetaPorIncidenciaId(Integer id) {
        return incidenciaRepository.findById(id).get().getEtiqueta();
    }

    public Incidencia obtenerIncidenciaPorId(Integer id) {
        return incidenciaRepository.findById(id).get();
    }
}