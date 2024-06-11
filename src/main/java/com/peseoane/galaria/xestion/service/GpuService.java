package com.peseoane.galaria.xestion.service;

import com.peseoane.galaria.xestion.model.Fabricante;
import com.peseoane.galaria.xestion.model.Gpu;
import com.peseoane.galaria.xestion.model.Ordenador;
import com.peseoane.galaria.xestion.model.OrdenadorGpu;
import com.peseoane.galaria.xestion.repository.GpuRepository;
import com.peseoane.galaria.xestion.repository.OrdenadorGpuRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GpuService {

    private final GpuRepository gpuRepository;
    private final OrdenadorGpuRepository ordenadorGpuRepository;

    public GpuService(GpuRepository gpuRepository, OrdenadorGpuRepository ordenadorGpuRepository) {
        this.gpuRepository = gpuRepository;
        this.ordenadorGpuRepository = ordenadorGpuRepository;
    }

    @Transactional
    public Gpu procesarGpu(String referencia, Fabricante fabricante) {
        if (gpuRepository.existsByReferenciaAndFabricante(referencia, fabricante )) {
            return gpuRepository.findByReferenciaAndFabricante(referencia, fabricante);
        }
        Gpu entidadGpu = new Gpu();
        entidadGpu.setReferencia(referencia);
        entidadGpu.setFabricante(fabricante);
        gpuRepository.save(entidadGpu);
        return entidadGpu;
    }

    @Transactional
    public Gpu asociarGpu(Ordenador ordenador, Gpu gpu) {
        LocalDateTime fechaTransaccion = LocalDateTime.now();
        OrdenadorGpu ordenadorGpu;
        if (ordenadorGpuRepository.existsByOrdenadorAndGpuAndFechaBajaNull(ordenador, gpu)) {
            ordenadorGpu = ordenadorGpuRepository.findByOrdenadorAndGpuAndFechaBajaNull(ordenador, gpu);
        } else {
            ordenadorGpu = new OrdenadorGpu();
            ordenadorGpu.setOrdenador(ordenador);
            ordenadorGpu.setFechaAlta(fechaTransaccion);
            ordenadorGpu.setGpu(gpu);
        }
        ordenadorGpu.setFechaActualizacion(fechaTransaccion);
        ordenadorGpuRepository.save(ordenadorGpu);
        return gpu;
    }

}