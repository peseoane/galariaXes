package com.peseoane.galaria.xestion.service;

import com.peseoane.galaria.xestion.model.Etiqueta;
import com.peseoane.galaria.xestion.model.Log;
import com.peseoane.galaria.xestion.repository.LogRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static jakarta.transaction.Transactional.TxType.REQUIRES_NEW;

@Service
public class LogService {

    private final LogRepository logRepository;
    private final NivelService nivelService;

    public LogService(LogRepository logRepository, NivelService nivelService, EtiquetaService etiquetaService) {
        this.logRepository = logRepository;
        this.nivelService = nivelService;
    }

    @Transactional(REQUIRES_NEW)
    public Log autoLog(String nivel, Etiqueta etiqueta, String descripcion) {
        Log log = new Log();
        log.setEtiqueta(etiqueta);
        log.setNivel(nivelService.procesarNivel(nivel));
        log.setDescripcion(descripcion);
        logRepository.save(log);
        return log;
    }


    @Transactional(REQUIRES_NEW)
    public Log autoLog(String nivel, String descripcion) {
        Log log = new Log();
        log.setNivel(nivelService.procesarNivel(nivel));
        log.setDescripcion(descripcion);
        logRepository.save(log);
        return log;
    }

    @Transactional(REQUIRES_NEW)
    public List<Log> getLogsBetween(LocalDateTime inicio, LocalDateTime fin) {
        return logRepository.findByFechaBetweenOrderByFechaDesc(inicio, fin);
    }

    public List<Log> obtenerUltimoTrimestreRegistros() {
        LocalDateTime inicio = LocalDateTime.now().minusMonths(3);
        LocalDateTime fin = LocalDateTime.now();
        return getLogsBetween(inicio, fin);
    }

    public List<Log> obtenerUltimoMes(){
        LocalDateTime inicio = LocalDateTime.now().minusMonths(1);
        LocalDateTime fin = LocalDateTime.now();
        return getLogsBetween(inicio, fin);
    }

    public List<Log> obtenerUltimosLogs(){
        return logRepository.findTop1024ByOrderByFechaDesc();
    }

    public List<Log> obtenerLogsByQuery(String busqueda) {
        return logRepository.buscarEnTodosCampos(busqueda);
    }



}