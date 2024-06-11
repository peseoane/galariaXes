package com.peseoane.galaria.xestion.service;

import com.peseoane.galaria.xestion.model.Etiqueta;
import com.peseoane.galaria.xestion.model.TipoElemento;
import com.peseoane.galaria.xestion.repository.TipoElementoRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoElementoService {

    private final TipoElementoRepository tipoElementoRepository;
    @Getter
    private final TipoElemento ordenador;
    @Getter
    private final TipoElemento servidor;
    @Getter
    private final TipoElemento maquinaVirtual;

    public TipoElementoService(TipoElementoRepository tipoElementoRepository) {
        this.tipoElementoRepository = tipoElementoRepository;
        this.ordenador = tipoElementoRepository.findByTipo("ordenador");
        this.servidor = tipoElementoRepository.findByTipo("servidor");
        this.maquinaVirtual = tipoElementoRepository.findByTipo("maquina_virtual");
    }


    public TipoElemento obtenerPorId(Integer id) {
        return tipoElementoRepository.findById(id).orElse(null);
    }

    public List<TipoElemento> obtenerTodos() {
        return tipoElementoRepository.findAll();
    }

    public TipoElemento porEtiqueta(Etiqueta etiquetaPc) {
        if (etiquetaPc == null || etiquetaPc.getReferencia().isBlank()) {
            throw new IllegalArgumentException("La etiqueta no puede estar vacía");
        }

        String referencia = etiquetaPc.getReferencia().trim();
        String tipo;

        if (referencia.contains("P")) {
            tipo = "ordenador";
        } else if (referencia.contains("S")) {
            tipo = "servidor";
        } else if (referencia.contains("I")) {
            tipo = "impresora";
        } else {
            tipo = "ordenador";
        }

        return tipoElementoRepository.findByTipo(tipo);
    }

    public TipoElemento obtenerOrdenador() {
        return tipoElementoRepository.findByTipo("ordenador");
    }

    public TipoElemento obtenerServidor() {
        return tipoElementoRepository.findByTipo("servidor");
    }

    public TipoElemento obtenerMaquinaVirtual() {
        return tipoElementoRepository.findByTipo("maquina_virtual");
    }


    public TipoElemento obtenerImpresora() {
        return tipoElementoRepository.findByTipo("impresora");
    }

    public TipoElemento obtenerPorTipo(String tipo) {
        return tipoElementoRepository.findByTipo(tipo);
    }

}