package com.peseoane.galaria.xestion.repository;

import com.peseoane.galaria.xestion.model.Ordenador;
import com.peseoane.galaria.xestion.model.OrdenadorNetwork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdenadorNetworkRepository extends JpaRepository<OrdenadorNetwork, Integer> {

    List<OrdenadorNetwork> findIpv4ByOrdenadorId(int id);

    boolean existsByOrdenadorAndIpv4AndMacAndFechaBajaNull(Ordenador ordenador, String ipv4, String mac);

    OrdenadorNetwork findByOrdenadorAndIpv4AndMacAndFechaBajaNull(Ordenador ordenador, String ipv4, String mac);

}