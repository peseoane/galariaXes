package com.peseoane.galaria.xestion.service;

import com.peseoane.galaria.xestion.model.*;
import com.peseoane.galaria.xestion.repository.ModeloMonitorRepository;
import com.peseoane.galaria.xestion.repository.MonitorRepository;
import com.peseoane.galaria.xestion.repository.OrdenadorRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class MonitorService {

    private static final Logger log = LoggerFactory.getLogger(MonitorService.class);
    private final ModeloMonitorRepository modeloMonitorRepository;
    private final MonitorRepository monitorRepository;
    private final LogService logService;
    private final EtiquetaService etiquetaService;
    private final OrdenadorRepository ordenadorRepository;

    public MonitorService(ModeloMonitorRepository modeloMonitorRepository,
                          MonitorRepository monitorRepository,
                          LogService logService,
                          EtiquetaService etiquetaService, OrdenadorRepository ordenadorRepository) {
        this.modeloMonitorRepository = modeloMonitorRepository;
        this.monitorRepository = monitorRepository;
        this.logService = logService;
        this.etiquetaService = etiquetaService;
        this.ordenadorRepository = ordenadorRepository;
    }


    @Transactional
    public ModeloMonitor procesarModeloMonitor(String referencia, Fabricante fabricante) {
        ModeloMonitor modeloMonitor;
        if (modeloMonitorRepository.existsByReferenciaAndFabricante(referencia, fabricante)) {
            modeloMonitor = modeloMonitorRepository.findByReferenciaAndFabricante(referencia, fabricante);
        } else {
            modeloMonitor = new ModeloMonitor();
            modeloMonitor.setReferencia(referencia);
            modeloMonitor.setFabricante(fabricante);
            logService.autoLog("INFO", "Modelo de monitor creado Referencia: " + referencia);
        }
        modeloMonitorRepository.save(modeloMonitor);
        return modeloMonitor;
    }

    @Transactional
    public Monitor procesarMonitor(Ordenador ordenadorExt, ModeloMonitor modeloMonitor, String numeroSerie) {
        LocalDateTime fechaTransaccion = LocalDateTime.now();
        // Esto es porque al pasar Ordenador entre dos contextos el recolector de basura lo elimina
        // Así que la recargo!
        Ordenador ordenador = ordenadorRepository.findByEtiqueta(ordenadorExt.getEtiqueta());
        Monitor monitor;
        if (monitorRepository.existsByNumeroSerie(numeroSerie)) {
            monitor = monitorRepository.findByNumeroSerie(numeroSerie);
            if (monitor.getFechaBaja() != null) {
                monitor.setFechaBaja(null);
            }
            comprobarPropierarioAntiguo(ordenador, monitor);
        } else {
            monitor = new Monitor();
            monitor.setModeloMonitor(modeloMonitor);
            monitor.setNumeroSerie(numeroSerie);
            monitor.setFechaAlta(fechaTransaccion);
        }
        monitor.setOrdenador(ordenador);
        monitor.setFechaActualizacion(fechaTransaccion);
        monitorRepository.save(monitor);
        return monitor;
    }

    @Transactional
    public Monitor agregarInformacionMonitorDeGreko(Etiqueta etiqueta,
                                                    String numeroSerie,
                                                    ModeloMonitor modeloMonitor) {
        if (monitorRepository.existsByNumeroSerie(numeroSerie)) {
            Monitor monitor = monitorRepository.findByNumeroSerie(numeroSerie);
            monitor.setEtiqueta(etiqueta);
            monitor.setModeloMonitor(modeloMonitor);
            monitorRepository.save(monitor);
            return monitor;
        } else {
            Monitor monitor = new Monitor();
            monitor.setActivo(true);
            monitor.setEtiqueta(etiqueta);
            monitor.setNumeroSerie(numeroSerie);
            monitor.setModeloMonitor(modeloMonitor);
            monitor.setFechaAlta(LocalDateTime.now());
            monitorRepository.save(monitor);
            return monitor;
        }
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    protected void comprobarPropierarioAntiguo(Ordenador ordenador, Monitor monitor) {
        if (monitor.getOrdenador() != null && !monitor.getOrdenador()
                .getEtiqueta()
                .getReferencia()
                .equals(ordenador.getEtiqueta().getReferencia())) {
            logService.autoLog("INFO",
                               "Cambio de monitor de: " + monitor.getOrdenador()
                                       .getEtiqueta()
                                       .getReferencia() + " a: " + ordenador.getEtiqueta().getReferencia());
        }
    }

    public List<Monitor> getMonitoresByOrdenador(Ordenador ordenador) {
        return monitorRepository.findAllByOrdenador(ordenador);
    }

    @Transactional
    public void buscarMonitoresHuerfanos(List<Monitor> monitoresViejos, List<Monitor> monitoresNuevos) {
        for (Monitor monitor : monitoresViejos) {
            if (!monitoresNuevos.contains(monitor)) {
                monitor.setOrdenador(null);
                monitor.setFechaBaja(LocalDateTime.now());
                monitorRepository.save(monitor);
                monitorRepository.flush();
                logService.autoLog("INFO", "Monitor huerfano " + monitor.getNumeroSerie() + " sin ordenador");
            }
        }
    }


    public List<Monitor> obtenerMonitores() {
        return monitorRepository.findAll();
    }

    public List<Monitor> buscarEnMonitores(String busqueda) {
        return monitorRepository.findAll();
    }


    public List<Integer> getIdByQueryInMonitores(String busqueda) {
        return monitorRepository.getIdByQueryInMonitores(busqueda);
    }

    public List<Integer> getIdByAllMonitores() {
        return monitorRepository.getIdByAllMonitores();
    }

    public Monitor obtenerMonitorPorId(Integer id) {
        return monitorRepository.findById(id).orElse(null);
    }

    public List<ModeloMonitor> obtenerTodosModelosMonitor() {
        return modeloMonitorRepository.findAll();
    }

    public ModeloMonitor obtenerModeloMonitorPorId(Integer id) {
        return modeloMonitorRepository.findById(id).orElse(null);
    }

    @Transactional
    public void guardarMonitor(Monitor monitor) {
        monitorRepository.save(monitor);
        monitorRepository.flush();
    }


    public void guardarModeloMonitor(ModeloMonitor modeloMonitorForm) {
        modeloMonitorRepository.save(modeloMonitorForm);
    }

    public boolean isEtiquetaYaAsignada(String etiqueta) {
        return monitorRepository.existsByEtiqueta(etiquetaService.procesarEtiqueta(etiqueta));
    }

    public Monitor getMonitorAsociadoEtiqueta(String etiqueta) {
        return monitorRepository.findByEtiqueta(etiquetaService.procesarEtiqueta(etiqueta));
    }

    public List<Monitor> obtenerMonitoresByOrderDesc() {
        return monitorRepository.findAllByEtiquetaOrderDesc();
    }

    public void guardarMonitores(Set<Monitor> monitoresAsociados) {
        monitorRepository.saveAll(monitoresAsociados);
        monitorRepository.flush();
    }

    public List<Monitor> obtenerMonitoresByContrato(Contrato contratoActualizado) {
        return monitorRepository.findByContrato(contratoActualizado);
    }

    public List<Monitor> obtenerMonitoresConGarantia() {
        return monitorRepository.findByGarantiaIsNotNullAndGarantiaGreaterThan(LocalDateTime.now());
    }

    public List<Monitor> obtenerMonitoresSinGarantia() {
        return monitorRepository.findByGarantiaIsNotNullAndGarantiaLessThanEqual(LocalDateTime.now());
    }
}