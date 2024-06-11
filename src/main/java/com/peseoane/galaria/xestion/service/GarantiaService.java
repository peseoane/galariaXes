package com.peseoane.galaria.xestion.service;


import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class GarantiaService {

    public String calcularTiempoTranscurrido(LocalDateTime fechaGarantia) {
        LocalDateTime fechaActual = LocalDateTime.now();
        long años = ChronoUnit.YEARS.between(fechaGarantia, fechaActual);
        fechaGarantia = fechaGarantia.plusYears(años);
        long meses = ChronoUnit.MONTHS.between(fechaGarantia, fechaActual);
        fechaGarantia = fechaGarantia.plusMonths(meses);
        long días = ChronoUnit.DAYS.between(fechaGarantia, fechaActual);
        return String.format("%d años %d meses %d días", Math.abs(años), Math.abs(meses), Math.abs(días));
    }
}