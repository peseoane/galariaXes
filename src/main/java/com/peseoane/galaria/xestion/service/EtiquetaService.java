package com.peseoane.galaria.xestion.service;

import com.peseoane.galaria.xestion.model.Etiqueta;
import com.peseoane.galaria.xestion.model.TipoElemento;
import com.peseoane.galaria.xestion.repository.EtiquetaRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class EtiquetaService {

    private final EtiquetaRepository etiquetaRepository;
    private final TipoElementoService tipoElementoService;

    public EtiquetaService(EtiquetaRepository etiquetaRepository, TipoElementoService tipoElementoService) {
        this.etiquetaRepository = etiquetaRepository;
        this.tipoElementoService = tipoElementoService;
    }


    @Transactional
    public Etiqueta procesarEtiqueta(String xmlEtiqueta) {
        Etiqueta etiqueta;
        if (etiquetaRepository.existsByReferencia(xmlEtiqueta)) {
            return etiquetaRepository.findByReferencia(xmlEtiqueta);
        }
        etiqueta = new Etiqueta();
        etiqueta.setReferencia(xmlEtiqueta);
        etiquetaRepository.save(etiqueta);
        etiquetaRepository.flush();
        return etiqueta;
    }

    public boolean existsEtiqueta(String busqueda) {
        return etiquetaRepository.existsByReferencia(busqueda);
    }

    public boolean contains(String busqueda) {
        return etiquetaRepository.existsByReferenciaContaining(busqueda);
    }


    public List<Etiqueta> buscarEtiquetas(String busqueda) {
        return etiquetaRepository.findByReferenciaContaining(busqueda);
    }

    public Etiqueta obtenerPorId(Integer id) {
        return etiquetaRepository.findById(id).orElse(null);
    }

    public List<Etiqueta> obtenerEtiquetas() {
        return etiquetaRepository.findAll();
    }

    public List<Etiqueta> obtenerEtiquetasOrdenadoresServidores() {
        TipoElemento ordenador = tipoElementoService.getOrdenador();
        TipoElemento servidor = tipoElementoService.getServidor();

        return etiquetaRepository.findByTipoElementoIn(List.of(ordenador, servidor));
    }

    @Transactional
    public boolean eliminarEtiquetaById(Integer id) throws SQLException, DataIntegrityViolationException {

        Etiqueta etiqueta = obtenerPorId(id);

        if (etiqueta == null) {
            return false;
        }

        if (etiqueta.getOrdenadorAsociado() != null || etiqueta.getMonitorAsociado() != null) {
            throw new SQLException("No se puede eliminar la etiqueta porque está asociada a un elemento");
        }

        etiquetaRepository.deleteById(id);
        etiquetaRepository.flush();
        return true;

    }

    public Etiqueta obtenerEtiquetaPorId(Integer id) {
        return etiquetaRepository.findById(id).orElse(null);
    }
}