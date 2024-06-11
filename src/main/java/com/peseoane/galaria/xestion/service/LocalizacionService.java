package com.peseoane.galaria.xestion.service;


import com.peseoane.galaria.xestion.etc.InformacionOrdenador;
import com.peseoane.galaria.xestion.model.*;
import com.peseoane.galaria.xestion.repository.LocalizacionRepository;
import com.peseoane.galaria.xestion.repository.ServicioRepository;
import com.peseoane.galaria.xestion.repository.TrazabilidadRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LocalizacionService {

    private final LocalizacionRepository localizacionRepository;
    private final ServicioRepository servicioRepository;
    private final TrazabilidadRepository trazabilidadRepository;

    @Autowired
    private ActiveDirectoryLdapQuery activeDirectoryLdapQuery;
    @Autowired
    private LogService logService;

    public LocalizacionService(LocalizacionRepository localizacionRepository,
                               ServicioRepository servicioRepository,
                               TrazabilidadRepository trazabilidadRepository) {
        this.localizacionRepository = localizacionRepository;
        this.servicioRepository = servicioRepository;
        this.trazabilidadRepository = trazabilidadRepository;
    }

 @Transactional
public Trazabilidad actualizarPropietarioAd(Ordenador ordenador) {

    LocalDateTime fechaTransaccion = LocalDateTime.now();
    InformacionOrdenador informacionOrdenador = activeDirectoryLdapQuery.getOrdenadorByCn(ordenador.getEtiqueta().getReferencia());
    if (informacionOrdenador != null) {
        Localizacion localizacion;
        if (localizacionRepository.existsByLocalizacion(informacionOrdenador.lugar())) {
            localizacion = localizacionRepository.findByLocalizacion(informacionOrdenador.lugar());
        } else {
            localizacion = new Localizacion();
            localizacion.setLocalizacion(informacionOrdenador.lugar());
            localizacionRepository.save(localizacion);
        }

        Servicio servicio;
        if (servicioRepository.existsByServicio(informacionOrdenador.ou().get(1))) {
            servicio = servicioRepository.findByServicio(informacionOrdenador.ou().get(1));
        } else {
            servicio = new Servicio();
            servicio.setServicio(informacionOrdenador.ou().get(1));
            servicioRepository.save(servicio);
        }

        Trazabilidad trazabilidad;
        if (trazabilidadRepository.existsByEtiquetaAndFechaBajaNull(ordenador.getEtiqueta())) {
            trazabilidad = trazabilidadRepository.findByEtiquetaAndFechaBajaNull(ordenador.getEtiqueta());
            // Si el servicio es diferente, dar de baja la traza antigua y crear una nueva
            if (!trazabilidad.getServicio().equals(servicio)) {
                trazabilidad.setFechaBaja(fechaTransaccion);
                trazabilidadRepository.save(trazabilidad);
                trazabilidad = new Trazabilidad();
                trazabilidad.setEtiqueta(ordenador.getEtiqueta());
                trazabilidad.setFechaAlta(fechaTransaccion);
            }
        } else {
            trazabilidad = new Trazabilidad();
            trazabilidad.setEtiqueta(ordenador.getEtiqueta());
            trazabilidad.setFechaAlta(fechaTransaccion);
        }
        trazabilidad.setNotas(informacionOrdenador.ou().toString());
        trazabilidad.setLocalizacion(localizacion);
        trazabilidad.setServicio(servicio);
        trazabilidad.setFechaActualizacion(fechaTransaccion);
        trazabilidadRepository.save(trazabilidad);
        trazabilidadRepository.flush();
        return trazabilidad;
    }
    return null;
}

    @Transactional
    public Localizacion procesarLocalizacionEtiqueta(Etiqueta etiquetaOrdenador, String servicioOrdenador,
                                                     String localizacionOrdenador, String notas) {
        Localizacion localizacionEntity;
        if (localizacionRepository.existsByLocalizacion(localizacionOrdenador)) {
            localizacionEntity = localizacionRepository.findByLocalizacion(localizacionOrdenador);
        } else {
            localizacionEntity = new Localizacion();
            localizacionEntity.setLocalizacion(localizacionOrdenador);
            localizacionRepository.save(localizacionEntity);
        }
        Servicio servicio;
        if (servicioRepository.existsByServicio(servicioOrdenador)) {
            servicio = servicioRepository.findByServicio(servicioOrdenador);
        } else {
            servicio = new Servicio();
            servicio.setServicio(servicioOrdenador);
            servicioRepository.save(servicio);
        }
        Trazabilidad trazabilidad;
        if (trazabilidadRepository.existsByEtiquetaAndFechaBajaNull(etiquetaOrdenador)) {
            trazabilidad = trazabilidadRepository.findByEtiquetaAndFechaBajaNull(etiquetaOrdenador);
        } else {
            trazabilidad = new Trazabilidad();
            trazabilidad.setFechaAlta(LocalDateTime.now());
        }
        trazabilidad.setEtiqueta(etiquetaOrdenador);
        trazabilidad.setLocalizacion(localizacionEntity);
        trazabilidad.setServicio(servicio);
        trazabilidad.setNotas(notas);
        trazabilidadRepository.save(trazabilidad);
        trazabilidadRepository.flush();
        return localizacionEntity;
    }

    @Transactional
    public Localizacion procesarLocalizacionMonitor(Monitor monitor, Localizacion localizacionPc, Servicio servicioPc){

        Trazabilidad trazabilidad;
        if (trazabilidadRepository.existsByEtiquetaAndFechaBajaNull(monitor.getEtiqueta())) {
            trazabilidad = trazabilidadRepository.findByEtiquetaAndFechaBajaNull(monitor.getEtiqueta());
        } else {
            trazabilidad = new Trazabilidad();
            trazabilidad.setFechaAlta(LocalDateTime.now());
        }
        trazabilidad.setEtiqueta(monitor.getEtiqueta());
        trazabilidad.setLocalizacion(localizacionPc);
        trazabilidad.setServicio(servicioPc);
        trazabilidadRepository.save(trazabilidad);
        trazabilidadRepository.flush();
        return localizacionPc;

    }



    public List<Trazabilidad> obtenerTrazabilidades() {
        return trazabilidadRepository.findAll();
    }

    public List<Servicio> obtenerServicios() {
        return servicioRepository.findAll();
    }

    public Localizacion getLastTrazabilidad(Ordenador ordenador) {
        Localizacion localizacion = trazabilidadRepository.findByEtiquetaAndFechaBajaNull(ordenador.getEtiqueta())
                                                           .getLocalizacion();
        return localizacion;
    }

    public Servicio getLastServicio(Ordenador ordenador) {
        Servicio servicio = trazabilidadRepository.findByEtiquetaAndFechaBajaNull(ordenador.getEtiqueta())
                                                   .getServicio();
        return servicio;
    }
}