package com.peseoane.galaria.xestion.service;

import com.peseoane.galaria.xestion.model.*;
import com.peseoane.galaria.xestion.repository.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
/**
 * Esta clase se encarga de gestionar toda la lógica relacionada con los
 * ordenadores y sus componentes.
 */ public class OrdenadorService {

    private static final Logger log = LoggerFactory.getLogger(OrdenadorService.class);
    private final SistemaOperativoService sistemaOperativoService;
    private final TipoElementoService tipoElementoService;
    private final NetworkService networkService;
    private final ModeloOrdenadorRepository modeloOrdenadorRepository;
    private final OrdenadorRepository ordenadorRepository;
    private final LogService logService;
    private final EtiquetaService etiquetaService;
    private final MonitorService monitorService;
    private final MonitorRepository monitorRepository;
    private final OrdenadorDicomRepository ordenadorDicomRepository;
    private final OrdenadorNetworkRepository ordenadorNetworkRepository;

    public OrdenadorService(SistemaOperativoService sistemaOperativoService, TipoElementoService tipoElementoService, NetworkService networkService, ModeloOrdenadorRepository modeloOrdenadorRepository, OrdenadorRepository ordenadorRepository, LogService logService, EtiquetaService etiquetaService, MonitorService monitorService, MonitorRepository monitorRepository, OrdenadorDicomRepository ordenadorDicomRepository, OrdenadorNetworkRepository ordenadorNetworkRepository) {
        this.sistemaOperativoService = sistemaOperativoService;
        this.tipoElementoService = tipoElementoService;
        this.networkService = networkService;
        this.modeloOrdenadorRepository = modeloOrdenadorRepository;
        this.ordenadorRepository = ordenadorRepository;
        this.logService = logService;
        this.etiquetaService = etiquetaService;
        this.monitorService = monitorService;
        this.monitorRepository = monitorRepository;
        this.ordenadorDicomRepository = ordenadorDicomRepository;
        this.ordenadorNetworkRepository = ordenadorNetworkRepository;
    }


    @Transactional
    public ModeloOrdenador procesarModeloOrdenador(String referencia, Fabricante fabricante) {
        if (modeloOrdenadorRepository.existsByReferenciaAndFabricante(referencia, fabricante)) {
            return modeloOrdenadorRepository.findByReferenciaAndFabricante(referencia, fabricante);
        }
        ModeloOrdenador modeloOrdenador = new ModeloOrdenador();
        modeloOrdenador.setReferencia(referencia);
        modeloOrdenador.setFabricante(fabricante);
        modeloOrdenadorRepository.save(modeloOrdenador);
        return modeloOrdenador;
    }


    @Transactional
    public Ordenador procesarOrdenador(TipoElemento tipoElemento, Etiqueta etiqueta, String numeroSerie, ModeloOrdenador modeloOrdenador, SistemaOperativo sistemaOperativo, Long ram, Boolean activo) {
        LocalDateTime fechaTransaccion = LocalDateTime.now();
        Ordenador ordenador;

        if (ordenadorRepository.existsByEtiqueta(etiqueta)) {
            ordenador = ordenadorRepository.findByEtiqueta(etiqueta);
            isSistemaOperativoActualizado(sistemaOperativo, ordenador);
            isNumeroSerieModificado(numeroSerie, ordenador);
            isMismoModeloPc(modeloOrdenador, ordenador);
            isOrdenadorActivo(activo, ordenador);
            isRamDiferente(ram, ordenador);
        } else if (ordenadorRepository.existsByNumeroSerie(numeroSerie)) {
            ordenador = ordenadorRepository.findByNumeroSerie(numeroSerie);
            isSistemaOperativoActualizado(sistemaOperativo, ordenador);
            isEtiquetaModificada(etiqueta, ordenador);
            isNumeroSerieModificado(numeroSerie, ordenador);
            isMismoModeloPc(modeloOrdenador, ordenador);
            isOrdenadorActivo(activo, ordenador);
            isRamDiferente(ram, ordenador);
        } else {
            logService.autoLog("INFO", etiqueta, "Nuevo ordenador registrado con número de serie [" + numeroSerie + "] y etiqueta " + etiqueta.getReferencia());
            ordenador = new Ordenador();
            ordenador.setTipoElemento(tipoElemento);
            ordenador.setFechaAlta(fechaTransaccion);
            ordenador.setEtiqueta(etiqueta);
            ordenador.setNumeroSerie(numeroSerie);
            ordenador.setModeloOrdenador(modeloOrdenador);
        }


        ordenador.setSistemaOperativo(sistemaOperativo);
        ordenador.setRam(ram);
        ordenador.setActivo(activo);
        ordenador.setFechaActualizacion(fechaTransaccion);
        ordenadorRepository.save(ordenador);
        return ordenador;
    }

    private boolean isRamDiferente(Long ram, Ordenador ordenador) {
        if (!ordenador.getRam().equals(ram)) {
            ordenador.setRam(ram);
            double ramInGb = ram / 1073741824.0;
            double oldRamInGb = ordenador.getRam() / 1073741824.0;
            String logMessage = "La RAM ha cambiado de " + oldRamInGb + " GB a " + ramInGb + " GB";
            logService.autoLog("INFO", ordenador.getEtiqueta(), logMessage);
            return true;
        }
        return false;
    }

    private boolean isOrdenadorActivo(Boolean activo, Ordenador ordenador) {
        if (ordenador.getActivo() != activo) {
            ordenador.setActivo(activo);
            logService.autoLog("INFO", ordenador.getEtiqueta(), "El ordenador ha sido " + (activo ? "activado" : "desactivado"));
            return true;
        }
        return false;
    }

    private boolean isMismoModeloPc(ModeloOrdenador modeloOrdenador, Ordenador ordenador) {
        if (!ordenador.getModeloOrdenador().equals(modeloOrdenador)) {
            ordenador.setModeloOrdenador(modeloOrdenador);
            String logMessage = "El PC ha cambiado de modelo [" + ordenador.getModeloOrdenador().getReferencia() + "] a [" + modeloOrdenador.getReferencia() + "]";
            logService.autoLog("INFO", ordenador.getEtiqueta(), logMessage);
            return true;
        }
        return false;
    }

    private boolean isNumeroSerieModificado(String numeroSerie, Ordenador ordenador) {
        if (!numeroSerie.equals(ordenador.getNumeroSerie())) {
            ordenador.setNumeroSerie(numeroSerie);
            String logMessage = "El ordenador ha cambiado su número de serie de " + ordenador.getNumeroSerie() + " a " + numeroSerie;
            logService.autoLog("WARNING", ordenador.getEtiqueta(), logMessage);
            return true;
        }
        return false;
    }

    private boolean isEtiquetaModificada(Etiqueta etiqueta, Ordenador ordenador) {
        if (ordenador.getEtiqueta() != etiqueta) {
            ordenador.setEtiqueta(etiqueta);
            String logMessage = "El ordenador ha cambiado la etiqueta de [" + ordenador.getEtiqueta().getReferencia() + "] a [" + etiqueta.getReferencia() + "]";
            logService.autoLog("WARNING", ordenador.getEtiqueta(), logMessage);
            return true;
        }
        return false;
    }

    private boolean isSistemaOperativoActualizado(SistemaOperativo sistemaOperativo, Ordenador ordenador) {
        if (ordenador.getSistemaOperativo() != null && ordenador.getSistemaOperativo() != sistemaOperativo) {
            String logMessage = "Sistema Operativo actualizado de " + ordenador.getSistemaOperativo() + " a " + sistemaOperativo;
            logService.autoLog("INFO", ordenador.getEtiqueta(), logMessage);
            return true;
        }
        return false;
    }

    public List<Ordenador> obtenerOrdenadores() {
        return ordenadorRepository.findAll();
    }

    public List<Ordenador> buscarEnOrdenadores(String busqueda) {

        if (busqueda == null || busqueda.isEmpty()) {
            return obtenerOrdenadores();
        }
        List<Ordenador> resultado = ordenadorRepository.buscarEnTodosLosCampos(busqueda);
        return resultado;
    }

    public List<Integer> getIdByQueryInOrdenadores(String busqueda) {
        return ordenadorRepository.getIdByQueryInOrdenadores(busqueda);
    }

    public List<Integer> getIdByAllrdenadores() {
        return ordenadorRepository.findAllByIdAsInt();
    }

    public Ordenador obtenerOrdenadorPorId(Integer id) {
        return ordenadorRepository.findById(id).orElse(null);
    }

    public ModeloOrdenador obtenerModeloPcPorId(Integer id) {
        return modeloOrdenadorRepository.findById(id).orElse(null);
    }

    public List<ModeloOrdenador> obtenerTodosModelosOrdenador() {
        return modeloOrdenadorRepository.findAll();
    }

    @Transactional
    public void guardarOrdenador(Ordenador ordenadorForm) {
        ordenadorRepository.save(ordenadorForm);
        ordenadorRepository.flush();
    }

    public List<Ordenador> obtenerOrdenadoresByOrderDesc() {
        return ordenadorRepository.findAllByEtiquetaOrderDesc();
    }

    @Transactional
    public boolean eliminarOrdenador(Ordenador ordenador) {
        try {
            for (Monitor monitor : ordenador.getMonitoresAsociados()) {
                monitor.setOrdenador(null);
                monitorRepository.save(monitor);
            }

            for (OrdenadorDicom ordenadorDicom : ordenador.getConfiguracionesDIcom()) {
                ordenadorDicom.setOrdenador(null);
                ordenadorDicomRepository.save(ordenadorDicom);
            }

            for (OrdenadorNetwork ordenadorNetwork : ordenador.getInterfacesRedAsociadas()) {
                ordenadorNetwork.setOrdenador(null);
                ordenadorNetworkRepository.save(ordenadorNetwork);
            }

            monitorService.guardarMonitores(ordenador.getMonitoresAsociados());
            ordenadorRepository.delete(ordenador);
            return true;
        } catch (Exception e) {
            log.error("Error al eliminar el ordenador", e);
            logService.autoLog("ERROR", ordenador.getEtiqueta(), "Error al eliminar el ordenador");
            return false;
        }
    }


    public List<Ordenador> obtenerOrdenadoresByContrato(Contrato contratoActualizado) {
        return ordenadorRepository.findByContrato(contratoActualizado);
    }

    public List<Ordenador> obtenerOrdenadoresConGarantia() {
        return ordenadorRepository.findByGarantiaIsNotNullAndGarantiaGreaterThan(LocalDateTime.now());
    }


    public List<Ordenador> obtenerOrdenadoresSinGarantia() {
        return ordenadorRepository.findByGarantiaIsNotNullAndGarantiaLessThanEqual(LocalDateTime.now());
    }
}