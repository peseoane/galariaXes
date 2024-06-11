package com.peseoane.galaria.xestion.service;

import com.peseoane.galaria.xestion.model.Ordenador;
import com.peseoane.galaria.xestion.model.OrdenadorNetwork;
import com.peseoane.galaria.xestion.repository.OrdenadorNetworkRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class NetworkService {

    private final OrdenadorNetworkRepository ordenadorNetworkRepository;
    public NetworkService(OrdenadorNetworkRepository ordenadorNetworkRepository) {
        this.ordenadorNetworkRepository = ordenadorNetworkRepository;
    }


    @Transactional
    public OrdenadorNetwork procesarNetwork(Ordenador ordenador, String ipv4, String mac, String boca_red, String notas) {
        LocalDateTime fechaTransaccion = LocalDateTime.now();
        String queryIpv4 = ipv4;
        String queryMac = mac;
        OrdenadorNetwork ordenadorNetwork;
        if (ordenadorNetworkRepository.existsByOrdenadorAndIpv4AndMacAndFechaBajaNull(ordenador, queryIpv4, queryMac)) {
            ordenadorNetwork = ordenadorNetworkRepository.findByOrdenadorAndIpv4AndMacAndFechaBajaNull(ordenador,
                                                                                                       queryIpv4,
                                                                                                       queryMac);
        } else {
            ordenadorNetwork = new OrdenadorNetwork();
            ordenadorNetwork.setFechaAlta(fechaTransaccion);
        }
        ordenadorNetwork.setOrdenador(ordenador);
        ordenadorNetwork.setIpv4(ipv4);
        ordenadorNetwork.setMac(mac);
        ordenadorNetwork.setBocaRed(boca_red);
        ordenadorNetwork.setNotas(notas);
        ordenadorNetwork.setFechaActualizacion(fechaTransaccion);
        ordenadorNetworkRepository.save(ordenadorNetwork);
        ordenadorNetworkRepository.flush();
        return ordenadorNetwork;
    }

    public OrdenadorNetwork obtenerIpsPorOrdenadorId(int ordenadorId) {
        OrdenadorNetwork ipsByOrdenador = ordenadorNetworkRepository.findById(ordenadorId).get();
        return ipsByOrdenador;
    }
}