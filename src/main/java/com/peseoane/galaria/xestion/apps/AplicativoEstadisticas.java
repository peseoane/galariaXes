package com.peseoane.galaria.xestion.apps;

import com.peseoane.galaria.xestion.etc.InformacionOrdenador;
import com.peseoane.galaria.xestion.model.Fabricante;
import com.peseoane.galaria.xestion.model.SistemaOperativo;
import com.peseoane.galaria.xestion.repository.FabricanteRepository;
import com.peseoane.galaria.xestion.repository.IncidenciaRepository;
import com.peseoane.galaria.xestion.repository.MonitorRepository;
import com.peseoane.galaria.xestion.repository.OrdenadorRepository;
import com.peseoane.galaria.xestion.service.ActiveDirectoryLdapQuery;
import com.peseoane.galaria.xestion.service.FabricanteService;
import com.peseoane.galaria.xestion.service.SistemaOperativoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/panel/aplicativo/estadisticas")
public class AplicativoEstadisticas {

    private static final Logger log = LoggerFactory.getLogger(AplicativoEstadisticas.class);
    private final OrdenadorRepository ordenadorRepository;
    private final MonitorRepository monitorRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final FabricanteRepository fabricanteRepository;
    private final ActiveDirectoryLdapQuery activeDirectoryLdapQuery;
    private final SistemaOperativoService sistemaOperativoService;
    private final FabricanteService fabricanteService;

    public AplicativoEstadisticas(OrdenadorRepository ordenadorRepository,
                                  MonitorRepository monitorRepository,
                                  IncidenciaRepository incidenciaRepository,
                                  FabricanteRepository fabricanteRepository,
                                  ActiveDirectoryLdapQuery activeDirectoryLdapQuery, SistemaOperativoService sistemaOperativoService, FabricanteService fabricanteService) {
        this.ordenadorRepository = ordenadorRepository;
        this.monitorRepository = monitorRepository;
        this.incidenciaRepository = incidenciaRepository;
        this.fabricanteRepository = fabricanteRepository;
        this.activeDirectoryLdapQuery = activeDirectoryLdapQuery;
        this.sistemaOperativoService = sistemaOperativoService;
        this.fabricanteService = fabricanteService;
    }


    @GetMapping({"", "/"})
    public String estadisticas(Model model) {
        model.addAttribute("numeroOrdenadores", ordenadorRepository.count());
        model.addAttribute("numeroMonitores", monitorRepository.count());
        model.addAttribute("numeroIncidencias", incidenciaRepository.count());
        model.addAttribute("numeroFabricantes", fabricanteRepository.count());
        model.addAttribute("listaFabricantes", fabricanteRepository.findAll());

        List<SistemaOperativo> versionesOs = sistemaOperativoService.obtenerTodos();
        List<Fabricante> fabricantes = fabricanteService.obtenerTodos();

        model.addAttribute("versionesOs",versionesOs);
        model.addAttribute("fabricantes",fabricantes);

        try {
            List<InformacionOrdenador> computerNames = activeDirectoryLdapQuery.getOrdenadores();
            model.addAttribute("ordenadores",computerNames);
            return "/panel/estadisticas/estadisticasOnline";
        } catch (Exception e) {
            log.error("Error al cargar las estadísticas: {}", e.getMessage());
            return "/panel/estadisticas/estadisticasOffline";
        }
    }
}