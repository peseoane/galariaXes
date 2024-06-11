package com.peseoane.galaria.xestion.service;

import com.peseoane.galaria.xestion.model.Contrato;
import com.peseoane.galaria.xestion.model.Monitor;
import com.peseoane.galaria.xestion.model.Ordenador;
import com.peseoane.galaria.xestion.repository.ContratoRepository;
import com.peseoane.galaria.xestion.repository.MonitorRepository;
import com.peseoane.galaria.xestion.repository.OrdenadorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContratoService {

    private final ContratoRepository contratoRepository;
    private final OrdenadorRepository ordenadorRepository;
    private final MonitorRepository monitorRepository;

    public ContratoService(ContratoRepository contratoRepository,
                           OrdenadorRepository ordenadorRepository,
                           MonitorRepository monitorRepository) {
        this.contratoRepository = contratoRepository;
        this.ordenadorRepository = ordenadorRepository;
        this.monitorRepository = monitorRepository;
    }

    @Transactional
    public void eliminarContrato(Integer id) {
        contratoRepository.deleteById(id);
    }

    public boolean existeContrato(String referencia) {
        return contratoRepository.existsByReferencia(referencia);
    }

    public boolean contieneContrato(String busqueda) {
        return contratoRepository.existsByReferenciaContaining(busqueda);
    }

    public List<Contrato> buscarContratos(String busqueda) {
        return contratoRepository.findByReferenciaContaining(busqueda);
    }

    public boolean guardarContrato(Contrato contrato) {
        if (contratoRepository.existsByReferencia(contrato.getReferencia())) {
            return false;
        }
        contratoRepository.save(contrato);
        contratoRepository.flush();
        return true;
    }

    public Contrato obtenerContratoPorId(Integer id) {
        return contratoRepository.findById(id).orElse(null);
    }

    public List<Contrato> obtenerTodos() {
        return contratoRepository.findAll();
    }

    @Transactional
    public void actualizarContrato(Contrato contrato) {
        Contrato contratoViejo = contratoRepository.findById(contrato.getId()).orElse(null);
        contratoViejo.setReferencia(contrato.getReferencia());
        contratoViejo.setNotas(contrato.getNotas());
        contratoViejo.setFechaCompra(contrato.getFechaCompra());
        contratoViejo.setPeriodoGarantia(contrato.getPeriodoGarantia());
        contratoRepository.save(contratoViejo);
        contratoRepository.flush();
    }

    public void forzarEliminarContrato(Integer id) {
        Contrato contrato = contratoRepository.findById(id).orElse(null);

        for (Ordenador ordenador : ordenadorRepository.findByContrato(contrato)) {
            ordenador.setContrato(null);
            ordenadorRepository.save(ordenador);
        }

        for (Monitor monitor : monitorRepository.findByContrato(contrato)) {
            monitor.setContrato(null);
            monitorRepository.save(monitor);
        }

        contratoRepository.deleteById(id);
    }
}