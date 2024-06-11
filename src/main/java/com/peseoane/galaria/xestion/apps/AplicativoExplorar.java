package com.peseoane.galaria.xestion.apps;

import com.peseoane.galaria.xestion.component.Ping;
import com.peseoane.galaria.xestion.component.Puertos;
import com.peseoane.galaria.xestion.model.*;
import com.peseoane.galaria.xestion.service.*;
import com.peseoane.galaria.xestion.views.VistaIncidencias;
import com.peseoane.galaria.xestion.views.VistaMonitores;
import com.peseoane.galaria.xestion.views.VistaOrdenadores;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/panel/aplicativo/explorar")
public class AplicativoExplorar {

    private static final Logger log = LoggerFactory.getLogger(AplicativoExplorar.class);
    private final OrdenadorService ordenadorService;
    private final MonitorService monitorService;
    private final TipoElementoService tipoElementoService;
    private final SistemaOperativoService sistemaOperativoService;
    private final EtiquetaService etiquetaService;
    private final LocalizacionService localizacionService;
    private final NetworkService networkService;
    private final LogService logService;
    private final FabricanteService fabricanteService;
    private final ContratoService contratoService;
    private final NivelService nivelService;
    private final EstadoService estadoService;
    private final IncidenciaService incidenciaService;
    private final GarantiaService garantiaService;
    private final VistaService vistaService;

    public AplicativoExplorar(OrdenadorService ordenadorService, MonitorService monitorService, TipoElementoService tipoElementoService, SistemaOperativoService sistemaOperativoService, EtiquetaService etiquetaService, LocalizacionService localizacionService, NetworkService networkService, LogService logService, FabricanteService fabricanteService, ContratoService contratoService, NivelService nivelService, EstadoService estadoService, IncidenciaService incidenciaService, GarantiaService garantiaService, VistaService vistaService) {
        this.ordenadorService = ordenadorService;
        this.monitorService = monitorService;
        this.tipoElementoService = tipoElementoService;
        this.sistemaOperativoService = sistemaOperativoService;
        this.etiquetaService = etiquetaService;
        this.localizacionService = localizacionService;
        this.networkService = networkService;
        this.logService = logService;
        this.fabricanteService = fabricanteService;
        this.contratoService = contratoService;
        this.nivelService = nivelService;
        this.estadoService = estadoService;
        this.incidenciaService = incidenciaService;
        this.garantiaService = garantiaService;
        this.vistaService = vistaService;
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        List<Etiqueta> etiquetas = etiquetaService.obtenerEtiquetas();
        List<TipoElemento> tiposElemento = tipoElementoService.obtenerTodos();
        List<ModeloOrdenador> modelosOrdenador = ordenadorService.obtenerTodosModelosOrdenador();
        List<ModeloMonitor> modelosMonitor = monitorService.obtenerTodosModelosMonitor();
        List<SistemaOperativo> sistemasOperativos = sistemaOperativoService.obtenerTodos();
        List<Ordenador> ordenadores = ordenadorService.obtenerOrdenadoresByOrderDesc();
        List<Monitor> monitores = monitorService.obtenerMonitoresByOrderDesc();
        model.addAttribute("ordenadores", ordenadores);
        model.addAttribute("monitores", monitores);
        model.addAttribute("etiquetas", etiquetas);
        model.addAttribute("tiposElemento", tiposElemento);
        model.addAttribute("modelosOrdenador", modelosOrdenador);
        model.addAttribute("sistemasOperativos", sistemasOperativos);
        model.addAttribute("modelosMonitor", modelosMonitor);
    }


    /**
     * Muestra la página de exploración de la aplicación. El resto de pestañas son cargadas con parciales con AJAX / HTMX
     * desde este mismo controlador.
     *
     * @param model
     * @return
     */
    @GetMapping({"", "/"})
    public String index(Model model) {
        return "/panel/explorar/indexExplorar";
    }

    /**
     * Muestra la página de exploración de ordenadores (la pestaña al pulsar en bootstrap carga este elemento).
     * @return
     */
    @GetMapping("/ordenadores")
    public String obtenerOrdenadores() {
        return "/panel/explorar/ordenadores/tabOrdenadores";
    }

    /**
     * Para mostrar los PCs, el elemento de búsqueda, la barra, lo que hace es llamar a este método onload, onkeyup, etc.
     * Por tanto, al cargar, la primera query es en blanco y nos da un listado global, le manda los IDs, no el objeto!
     * @param busqueda
     * @param model
     * @return
     */
    @GetMapping("/queryOrdenadores")
    public String buscarEnOrdenadores(@RequestParam(value = "busqueda", required = false) String busqueda, Model model) {
        long inicioQuery = System.nanoTime();
        List<Integer> ordenadores;
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            ordenadores = ordenadorService.getIdByQueryInOrdenadores(busqueda);
        } else {
            ordenadores = ordenadorService.getIdByAllrdenadores();
        }
        long finQuery = System.nanoTime();
        long millis = (finQuery - inicioQuery) / 1_000_000;
        model.addAttribute("tiempoQuery", millis);
        model.addAttribute("ordenadores", ordenadores);
        log.debug("Tiempo query" + millis);
        return "/panel/explorar/ordenadores/tablaOrdenadores";
    }

    /**
     * Muestra la entidad de un ordenador por su ID.
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/ordenadorById")
    public String buscarOrdenadorPorId(@RequestParam(value = "id", required = true) Integer id, Model model) {
        Ordenador ordenador = ordenadorService.obtenerOrdenadorPorId(id);
        model.addAttribute("ordenador", ordenador);
        return "/panel/explorar/ordenadores/entidadOrdenador";
    }

    /**
     * Muestra el formulario de edición de un ordenador por su ID.
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/editarOrdenadorById")
    public String editarOrdenadorPorId(@RequestParam(value = "id", required = true) String id, Model model) {
        Ordenador ordenador = ordenadorService.obtenerOrdenadorPorId(Integer.parseInt(id));
        model.addAttribute("ordenador", ordenador);
        return "/panel/explorar/ordenadores/editarOrdenador";
    }

    /**
     * Procesa la edición de un ordenador por su ID.
     * @param ordenadorForm
     * @param model
     * @return
     */
    @Transactional
    @PostMapping("/editarOrdenadorById")
    public String editarOrdenadorPorId(@ModelAttribute Ordenador ordenadorForm, Model model) {

        TipoElemento tipoElemento = tipoElementoService.obtenerPorId(ordenadorForm.getTipoElemento().getId());
        Etiqueta etiqueta = etiquetaService.procesarEtiqueta(ordenadorForm.getEtiqueta().getReferencia());
        ModeloOrdenador modeloOrdenador = ordenadorService.obtenerModeloPcPorId(ordenadorForm.getModeloOrdenador().getId());
        SistemaOperativo sistemaOperativo = sistemaOperativoService.obtenerPorId(ordenadorForm.getSistemaOperativo().getId());

        Ordenador ordenador = ordenadorService.obtenerOrdenadorPorId(ordenadorForm.getId());
        ordenador.setTipoElemento(tipoElemento);
        ordenador.setEtiqueta(etiqueta);
        ordenador.setModeloOrdenador(modeloOrdenador);
        ordenador.setSistemaOperativo(sistemaOperativo);
        ordenador.setGarantia(ordenadorForm.getGarantia());
        ordenador.setActivo(ordenadorForm.getActivo());
        ordenador.setPropuestoRetirada(ordenadorForm.getPropuestoRetirada());
        ordenadorService.guardarOrdenador(ordenador);

        return "redirect:/panel/aplicativo/explorar/ordenadorById?id=" + ordenador.getId();
    }


    /** Recibe una IP y hace un ping simple.
     * @param ip IP a la que hacer ping. (IPv4)
     * @param model
     * @return
     */
    @Async
    @PostMapping("/pingOrdenadorPorId")
    public CompletableFuture<String> pingOrdenadorPorTid(@RequestParam(value = "id", required = true) int id, Model model) {
        return CompletableFuture.supplyAsync(() -> {
            OrdenadorNetwork iface = networkService.obtenerIpsPorOrdenadorId(id);
            model.addAttribute("id", id);
            boolean ping = Ping.pingIpv4(iface.getIpv4());
            if (ping) {
                return "panel/explorar/ordenadores/pingExito";
            }
            return "panel/explorar/ordenadores/pingFallo";
        });
    }

    /**
     * SOLO LINUX / DOCKER. Recibe una IP y escanea los puertos de la misma mediante nmap.
     * @param ip IP a la que escanear los puertos. (IPv4)
     * @param model
     * @return
     */
    @Async
    @PostMapping("/escanearPuertosPorId")
    public CompletableFuture<String> escanearPuertosPorId(@RequestParam(value = "id", required = true) int id, Model model) {
        return CompletableFuture.supplyAsync(() -> {
            OrdenadorNetwork iface = networkService.obtenerIpsPorOrdenadorId(id);
            List<List<String>> resultadoEscaner = null;
            try {
                resultadoEscaner = Puertos.escanearPuertos(iface.getIpv4());
            } catch (Exception e) {
                model.addAttribute("error", "Nmap no instalado.");
                return "panel/explorar/ordenadores/nmap";
            }

            model.addAttribute("resultadoEscaner", resultadoEscaner);
            return "panel/explorar/ordenadores/nmap";
        });
    }

    /**
     * Muestra la pestaña de monitores.
     * @return
     */
    @GetMapping("/monitores")
    public String obtenerMonitores() {
        return "/panel/explorar/monitores/tabMonitores";
    }

    /**
     * Busca en los monitores por una cadena de texto. Al igual que antes, si la búsqueda es vacía, como esto se carga
     * onload, se cargan todos los monitores (sus IDs, no obejtos).
     * @param busqueda
     * @param model
     * @return
     */
    @GetMapping("/queryMonitores")
    public String buscarEnMonitores(@RequestParam(value = "busqueda", required = false) String busqueda, Model model) {
        List<Integer> monitores;
        long inicioQuery = System.nanoTime();

        if (busqueda != null && !busqueda.trim().isEmpty()) {
            monitores = monitorService.getIdByQueryInMonitores(busqueda);
        } else {
            monitores = monitorService.getIdByAllMonitores();
        }

        long finQuery = System.nanoTime();
        long millis = (finQuery - inicioQuery) / 1_000_000;
        model.addAttribute("tiempoQuery", millis);
        model.addAttribute("monitores", monitores);
        return "/panel/explorar/monitores/tablaMonitores";
    }

    /**
     * Muestra la entidad de un monitor por su ID.
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/monitorById")
    public String buscarMonitorPorId(@RequestParam(value = "id", required = true) Integer id, Model model) {
        Monitor monitor = monitorService.obtenerMonitorPorId(id);
        model.addAttribute("monitor", monitor);
        return "/panel/explorar/monitores/entidadMonitor";
    }

    /**
     * Muestra el formulario de edición de un monitor por su ID.
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/editarMonitorById")
    public String editarMonitorPorId(@RequestParam(value = "id", required = true) Integer id, Model model) {
        Monitor monitor = monitorService.obtenerMonitorPorId(id);
        List<Etiqueta> etiquetasPc = etiquetaService.obtenerEtiquetasOrdenadoresServidores();
        model.addAttribute("etiquetasAsoc", etiquetasPc);
        model.addAttribute("monitor", monitor);
        return "/panel/explorar/monitores/editarMonitor";
    }

    /**
     * Procesa la edición de un monitor por su ID.
     * @param monitorForm
     * @param model
     * @return
     */
    @Transactional
    @PostMapping("/editarMonitorById")
    public String editarMonitorPorId(@ModelAttribute Monitor monitorForm, Model model) {
        Monitor monitor = monitorService.obtenerMonitorPorId(monitorForm.getId());
        Etiqueta etiqueta = etiquetaService.procesarEtiqueta(monitorForm.getEtiqueta().getReferencia());
        monitor.setEtiqueta(etiqueta);
        monitor.setOrdenador(monitorForm.getOrdenador());
        monitor.setModeloMonitor(monitorForm.getModeloMonitor());
        monitor.setGarantia(monitorForm.getGarantia());
        monitor.setActivo(monitorForm.getActivo());
        monitor.setPropuestoRetirada(monitorForm.getPropuestoRetirada());
        monitorService.guardarMonitor(monitor);
        return "redirect:/panel/aplicativo/explorar/monitorById?id=" + monitor.getId();
    }

    /**
     * Muestra la pestaña para crer un monitor.
     * @return
     */
    @GetMapping("/crearMonitor")
    public String crearMonitor(Model model) {
        List<Etiqueta> etiquetasPc = etiquetaService.obtenerEtiquetasOrdenadoresServidores();
        Monitor monitor = new Monitor();
        monitor.setFechaAlta(LocalDateTime.now());
        model.addAttribute("monitor", monitor);
        model.addAttribute("etiquetasAsoc", etiquetasPc);
        return "/panel/explorar/monitores/crearMonitor";
    }

    /**
     * Muestra el HTML la creación de un fabricante.
     * @param model
     * @return
     */
    @GetMapping("/crearFabricante")
    public String crearFabricante(Model model) {
        Fabricante fabricante = new Fabricante();
        model.addAttribute("fabricante", fabricante);
        return "/panel/explorar/fabricante/crearFabricante";
    }

    /**
     * Procesa la creación de un fabricante.
     * @param fabricante
     * @param model
     * @return
     */
    @PostMapping("/crearFabricante")
    public String crearFabricante(@ModelAttribute Fabricante fabricante, Model model) {
        try {
            boolean status = fabricanteService.procesarFabricante(fabricante);
            if (status) {
                model.addAttribute("msg", "Fabricante creado con éxito.");
                return "fragments/exitoAlert";
            }
            model.addAttribute("msg", "El fabricante ya existe.");
            return "fragments/errorAlert";
        } catch (Exception e) {
            model.addAttribute("msg", "Error al crear el fabricante.");
            return "fragments/errorAlert";
        }

    }

    @GetMapping("/crearModeloMonitor")
    public String crearModeloMonitor(Model model) {
        ModeloMonitor modeloMonitor = new ModeloMonitor();
        List<Fabricante> fabricantes = fabricanteService.obtenerTodos();
        model.addAttribute("modeloMonitor", modeloMonitor);
        model.addAttribute("fabricantes", fabricantes);
        return "/panel/explorar/monitores/crearModeloMonitor";
    }

    @Transactional
    @PostMapping("/crearModeloMonitor")
    public String crearModeloMonitor(@ModelAttribute ModeloMonitor modeloMonitorForm, Model model) {
        try {
            modeloMonitorForm.setFabricante(modeloMonitorForm.getFabricante());
            monitorService.guardarModeloMonitor(modeloMonitorForm);
            model.addAttribute("msg", "Modelo Monitor creado con éxito.");
            return "fragments/exitoAlert";
        } catch (Exception e) {
            model.addAttribute("msg", "Error al crear el Modelo Monitor.");
            return "fragments/errorAlert";
        }
    }

    @Transactional
    @GetMapping("/crearModeloOrdenador")
    public String crearModeloPc(Model model) {
        ModeloOrdenador modeloOrdenador = new ModeloOrdenador();
        List<Fabricante> fabricantes = fabricanteService.obtenerTodos();
        model.addAttribute("modeloPc", modeloOrdenador);
        model.addAttribute("fabricantes", fabricantes);
        return "/panel/explorar/ordenadores/crearModeloOrdenador";
    }

    @Transactional
    @GetMapping("/crearOrdenador")
    public String crearOrdenador(Model model) {
        List<Etiqueta> etiquetasPc = etiquetaService.obtenerEtiquetasOrdenadoresServidores();
        Ordenador ordenador = new Ordenador();
        ordenador.setFechaAlta(LocalDateTime.now());
        model.addAttribute("ordenador", ordenador);
        model.addAttribute("etiquetasAsoc", etiquetasPc);
        return "/panel/explorar/ordenadores/crearOrdenador";
    }

    @Transactional
    @GetMapping("/eliminarOrdenadorById")
    public String eliminarOrdenadorById(@RequestParam(value = "id", required = true) Integer id, Model model) {
        Ordenador ordenador = ordenadorService.obtenerOrdenadorPorId(id);
        boolean resultado = ordenadorService.eliminarOrdenador(ordenador);
        if (resultado) {
            model.addAttribute("msg", "Ordenador eliminado con éxito.");
            return "fragments/exitoAlert";
        }
        model.addAttribute("msg", "Error al eliminar el ordenador.");
        return "fragments/errorAlert";
    }

    /**
     * Devuelve el parcial de los grupos
     * @param model
     * @return
     */
    @GetMapping("/grupos")
    public String overview(Model model) {
        return "/panel/explorar/grupos/indexGrupos";
    }

    @GetMapping("/grupos/general")
    public String general(Model model) {
        return "/panel/explorar/grupos/general";
    }

    @GetMapping("/grupos/servicios")
    public String buscarPorServicio(Model model) {
        List<Servicio> servicios = localizacionService.obtenerServicios();
        model.addAttribute("servicios", servicios);
        return "/panel/explorar/grupos/gruposPorServicio";
    }

    @GetMapping("/registros")
    public String registros(Model model) {
        logService.obtenerUltimoTrimestreRegistros();
        model.addAttribute("registrosTrimestre", logService.obtenerUltimosLogs());
        return "/panel/explorar/registros/indexRegistros";
    }

    @GetMapping("/contratos")
    public String contratos(@ModelAttribute Contrato contrato, Model model) {
        return "/panel/explorar/contratos/indexContratos";
    }

    @GetMapping("/queryContratos")
    public String buscarEnContratos(@RequestParam(value = "busqueda", required = false) String busqueda, Model model) {
        List<Contrato> contratos;
        long inicioQuery = System.nanoTime();
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            contratos = contratoService.buscarContratos(busqueda);
        } else {
            contratos = contratoService.obtenerTodos();
        }
        long finQuery = System.nanoTime();
        long millis = (finQuery - inicioQuery) / 1_000_000;
        model.addAttribute("contratos", contratos);
        model.addAttribute("tiempoQuery", millis);
        model.addAttribute("contratos", contratos);
        return "/panel/explorar/contratos/listaContratos";
    }

    @GetMapping("/crearContrato")
    public String crearContrato(Model model) {
        Contrato contrato = new Contrato();
        contrato.setFechaAlta(LocalDateTime.now());
        model.addAttribute("contrato", contrato);
        return "/panel/explorar/contratos/crearContrato";
    }

    @PostMapping("/crearContrato")
    public String crearContrato(@ModelAttribute Contrato contrato, HttpServletResponse httpServletResponse, Model model) {
        try {
            boolean res = contratoService.guardarContrato(contrato);
            if (res) {
                model.addAttribute("msg", "Contrato creado con éxito.");
                httpServletResponse.addHeader("HX-Trigger", "actualizarTabla");
                return "fragments/exitoAlert";
            }
            model.addAttribute("msg", "El contrato ya existe.");
            return "fragments/errorAlert";
        } catch (Exception e) {
            model.addAttribute("msg", "Error al crear el contrato: " + e.getMessage());
            return "fragments/errorAlert";
        }
    }

    @PostMapping("/editarContratoById")
    public String modificarContratoPorId(@ModelAttribute Contrato contrato, Model model) {
        try {
            contratoService.actualizarContrato(contrato);
            Contrato contratoActualizado = contratoService.obtenerContratoPorId(contrato.getId());
            // si la garantia ha cambiado hay que recalcula la garantia de los dispositivos asociados monitores y
            // ordenadores
            int antiguaGarantia = contratoService.obtenerContratoPorId(contrato.getId()).getPeriodoGarantia();
            int nuevaGarantia = contrato.getPeriodoGarantia();
            if (antiguaGarantia != nuevaGarantia) {
                List<Ordenador> ordenadores = ordenadorService.obtenerOrdenadoresByContrato(contratoActualizado);
                List<Monitor> monitores = monitorService.obtenerMonitoresByContrato(contratoActualizado);
                ordenadores.forEach(ordenador -> {
                    ordenador.setGarantia(contrato.getFechaCompra().plusMonths(contrato.getPeriodoGarantia()));
                    ordenadorService.guardarOrdenador(ordenador);
                });
                monitores.forEach(monitor -> {
                    monitor.setGarantia(contrato.getFechaCompra().plusMonths(contrato.getPeriodoGarantia()));
                    monitorService.guardarMonitor(monitor);
                });
            }

            model.addAttribute("msg", "Contrato modificado con éxito.");
            return "fragments/exitoAlert";
        } catch (Exception e) {
            model.addAttribute("msg", "Error al modificar el contrato: " + e.getMessage());
            return "fragments/errorAlert";
        }
    }

    /**
     * Procesa la eliminación de un contrato vacio, su alguna FK depende de él se negará.
     * @param id
     * @param model
     * @return
     */
    @PostMapping("/eliminarContratoById")
    public String eliminarContratoById(@RequestParam(value = "id", required = true) Integer id, Model model) {
        try {
            contratoService.eliminarContrato(id);
            model.addAttribute("msg", "Contrato eliminado con éxito.");
            return "fragments/exitoAlert";
        } catch (Exception e) {
            model.addAttribute("msg", "Error al eliminar el contrato: " + e.getMessage());
            return "fragments/errorAlert";
        }
    }

    /**
     * Procesa la eliminación de un contrato forzando la eliminación de todas las FK que dependen de él. (Las deja a NULL)
     * @param id
     * @param model
     * @return
     */
    @PostMapping("/forzarEliminarContratoById")
    public String forzarEliminarContratoById(@RequestParam(value = "id", required = true) Integer id, Model model) {
        try {
            contratoService.forzarEliminarContrato(id);
            model.addAttribute("msg", "Contrato eliminado con éxito.");
            return "fragments/exitoAlert";
        } catch (Exception e) {
            model.addAttribute("msg", "Error al eliminar el contrato: " + e.getMessage());
            return "fragments/errorAlert";
        }
    }

    @GetMapping("/etiquetas")
    public String etiquetas(Model model) {
        List<Etiqueta> etiquetas = etiquetaService.obtenerEtiquetas();
        model.addAttribute("etiquetas", etiquetas);
        return "/panel/explorar/etiquetas/indexEtiquetas";
    }

    /**
     * Muestra el formulario de eliminar una etiqueta siempre y cuando su FK esté libre de relaciones
     * @param id
     * @param model
     * @return
     */
    @Transactional
    @PostMapping("/eliminarEtiquetaById")
    public String eliminarEtiquetaById(@RequestParam(value = "id", required = true) Integer id, Model model) {
        try {
            boolean resultado = etiquetaService.eliminarEtiquetaById(id);
            if (!resultado) {
                model.addAttribute("msg", "La etiqueta no existe.");
                return "fragments/errorAlert";
            }
            model.addAttribute("msg", "Etiqueta eliminada con éxito.");
            return "fragments/exitoAlert";
        } catch (DataIntegrityViolationException | SQLException | UnexpectedRollbackException se) {
            model.addAttribute("msg", "Error al eliminar la etiqueta: " + se.getMessage());
            return "fragments/errorAlert";
        }
    }

    @GetMapping("/asociarContratos")
    public String asociarContratos(Model model) {
        List<Contrato> contratos = contratoService.obtenerTodos();
        List<Monitor> monitores = monitorService.obtenerMonitoresByOrderDesc();
        List<Ordenador> ordenadores = ordenadorService.obtenerOrdenadoresByOrderDesc();
        model.addAttribute("contratos", contratos);
        model.addAttribute("ordenadores", ordenadores);
        model.addAttribute("monitores", monitores);
        return "/panel/explorar/contratos/asociarContratos";
    }

    /**
     * Procesamos el formulario de contratos para los PCs, si cambiamos en algún momento la garantía, siempre se recalcula!
     * @param ordenadorContratos
     * @param model
     * @return
     */
    @Transactional
    @PostMapping("/asociarContratosOrdenadores")
    public String asociarContratosOrdenadores(@RequestParam Map<String, String> ordenadorContratos, Model model) {
        try {
            ordenadorContratos.forEach((key, value) -> {
                if (key.startsWith("ordenadorContratos[")) {
                    Integer ordenadorId = Integer.valueOf(key.substring(key.indexOf('[') + 1, key.indexOf(']')));
                    Integer contratoId = value.isEmpty() ? null : Integer.valueOf(value);

                    Ordenador ordenador = ordenadorService.obtenerOrdenadorPorId(ordenadorId);
                    if (ordenador != null) {
                        if (contratoId != null) {
                            Contrato contrato = contratoService.obtenerContratoPorId(contratoId);
                            ordenador.setContrato(contrato);
                            ordenador.setGarantia(contrato.getFechaCompra().plusMonths(contrato.getPeriodoGarantia()));
                        } else {
                            ordenador.setContrato(null);
                            ordenador.setGarantia(null);
                        }
                        ordenadorService.guardarOrdenador(ordenador);
                    }
                }
            });
            model.addAttribute("msg", "Contratos asociados a ordenaodres con éxito.");
            return "fragments/exitoAlert";

        } catch (Exception e) {
            String msg = "Error al asociar contratos a ordenadores: " + e.getMessage();
            model.addAttribute("msg", msg);
            return "fragments/errorAlert";
        }
    }

    /**
     * Procesamos el formulario de contratos para los monitores, si cambiamos en algún momento la garantía, siempre se recalcula!
     * @param monitorContratos
     * @param model
     * @return
     */
    @Transactional
    @PostMapping("/asociarContratosMonitores")
    public String asociarContratosMonitores(@RequestParam Map<String, String> monitorContratos, Model model) {
        try {
            monitorContratos.forEach((key, value) -> {
                if (key.startsWith("monitorContratos[")) {
                    Integer monitorId = Integer.valueOf(key.substring(key.indexOf('[') + 1, key.indexOf(']')));
                    Integer contratoId = value.isEmpty() ? null : Integer.valueOf(value);

                    Monitor monitor = monitorService.obtenerMonitorPorId(monitorId);
                    if (monitor != null) {
                        if (contratoId != null) {
                            Contrato contrato = contratoService.obtenerContratoPorId(contratoId);
                            monitor.setContrato(contrato);
                            monitor.setGarantia(contrato.getFechaCompra().plusMonths(contrato.getPeriodoGarantia()));
                        } else {
                            monitor.setContrato(null);
                            monitor.setGarantia(null);
                        }
                        monitorService.guardarMonitor(monitor);
                    }
                }
            });
            model.addAttribute("msg", "Contratos asociados a monitores con éxito.");
            return "fragments/exitoAlert";
        } catch (Exception e) {
            String msg = "Error al asociar contratos a monitores: " + e.getMessage();
            model.addAttribute("msg", msg);
            return "fragments/errorAlert";
        }
    }

    @GetMapping("/buscarRegistros")
    public String buscarRegistros(@RequestParam(value = "busqueda", required = false) String busqueda, Model model) {
        List<Log> logs;
        long inicioQuery = System.nanoTime();
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            log.info("Buscando registros por -> " + busqueda);
            logs = logService.obtenerLogsByQuery(busqueda);
        } else {
            log.info("Buscando registros por -> " + busqueda);
            logs = logService.obtenerUltimoTrimestreRegistros();
        }
        long finQuery = System.nanoTime();
        long millis = (finQuery - inicioQuery) / 1_000_000;
        model.addAttribute("tiempoQuery", millis);
        model.addAttribute("registrosTrimestre", logs);
        return "/panel/explorar/registros/tablaRegistros";
    }

    @GetMapping("/crearIncidenciaByEtiquetaId")
    public String crearIncidenciaById(@RequestParam(value = "id", required = true) Integer id, Model model) {
        Etiqueta etiqueta = etiquetaService.obtenerEtiquetaPorId(id);
        Incidencia incidencia = new Incidencia();
        incidencia.setEtiqueta(etiqueta);
        incidencia.setFechaAlta(LocalDateTime.now());
        model.addAttribute("incidencia", incidencia);

        List<Estado> estados = estadoService.obtenerTodosLosEstados();
        model.addAttribute("estados", estados);

        return "/panel/explorar/incidencias/crearIncidencia";
    }

    @GetMapping("/editarIncidenciaById")
    public String editarIncidenciaById(@RequestParam(value = "id", required = true) Integer id, Model model) {
        Incidencia incidencia = incidenciaService.obtenerIncidenciaPorId(id);
        model.addAttribute("incidencia", incidencia);
        List<Estado> estados = estadoService.obtenerTodosLosEstados();
        model.addAttribute("estados", estados);
        return "/panel/explorar/incidencias/editarIncidencia";
    }

    @Transactional
    @PostMapping("/editarIncidenciaById")
    public String editarIncidenciaById(@ModelAttribute Incidencia incidencia, Model model) {
        try {
            incidenciaService.guardarIncidencia(incidencia);
            model.addAttribute("msg", "Incidencia modificada con éxito.");
            return "fragments/exitoAlert";
        } catch (Exception e) {
            model.addAttribute("msg", "Error al modificar la incidencia: " + e.getMessage());
            return "fragments/errorAlert";
        }
    }

    @Transactional
    @PostMapping("/crearIncidenciaByEtiquetaId")
    public String crearIncidenciaById(@ModelAttribute Incidencia incidencia, Model model) {
        try {
            log.error("Incidencia creada para" + incidencia.getEtiqueta().getReferencia());
            incidenciaService.guardarIncidencia(incidencia);
            model.addAttribute("msg", "Incidencia creada con éxito.");
            return "fragments/exitoAlert";
        } catch (Exception e) {
            model.addAttribute("msg", "Error al crear la incidencia: " + e.getMessage());
            return "fragments/errorAlert";
        }
    }

    @Transactional
    @PostMapping("/eliminarIncidenciaById")
    public String eliminarIncidenciaById(@RequestParam(value = "id", required = true) Integer id, Model model) {
        try {
            Etiqueta etiqueta = incidenciaService.obtenerEtiquetaPorIncidenciaId(id);
            incidenciaService.eliminarIncidencia(id);
            logService.autoLog("INFO", etiqueta, "Se ha eliminado una incidencia");
            model.addAttribute("msg", "Incidencia eliminada con éxito.");
            return "fragments/exitoAlert";
        } catch (Exception e) {
            model.addAttribute("msg", "Error al eliminar la incidencia: " + e.getMessage());
            return "fragments/errorAlert";
        }
    }

    @GetMapping("/garantias")
    public String garantias(Model model) {

        List<Ordenador> ordenadoresConGarantia = ordenadorService.obtenerOrdenadoresConGarantia();
        List<Ordenador> ordenadoresSinGarantia = ordenadorService.obtenerOrdenadoresSinGarantia();

        List<Monitor> monitoresConGarantia = monitorService.obtenerMonitoresConGarantia();
        List<Monitor> monitoresSinGarantia = monitorService.obtenerMonitoresSinGarantia();

        model.addAttribute("ordenadoresConGarantia", ordenadoresConGarantia);
        model.addAttribute("ordenadoresSinGarantia", ordenadoresSinGarantia);

        model.addAttribute("monitoresConGarantia", monitoresConGarantia);
        model.addAttribute("monitoresSinGarantia", monitoresSinGarantia);

        model.addAttribute("garantiaService", garantiaService);

        return "/panel/explorar/garantia/indexGarantias";
    }

    /**
     * Pestaña que nos carga las 3 vistas creadas en el SQL para poder procesar la exportación de los datos a CSV / XML.
     * @param model
     * @return
     */
    @GetMapping("/exportar")
    public String exportar(Model model) {
        return "/panel/explorar/exportar/indexExportar";
    }

    @GetMapping("/exportarOrdenadores")
    public String exportarOrdenadores(Model model) {
        List<VistaOrdenadores> vistaOrdenadores = vistaService.getOrdenadores();
        model.addAttribute("vistaOrdenadores", vistaOrdenadores);
        return "/panel/explorar/exportar/ordenadores";
    }

    @GetMapping("/exportarMonitores")
    public String exportarMonitores(Model model) {
        List<VistaMonitores> vistaMonitores = vistaService.getMonitores();
        model.addAttribute("vistaMonitores", vistaMonitores);
        return "/panel/explorar/exportar/monitores";
    }

    @GetMapping("/exportarIncidencias")
    public String exportarIncidencias(Model model) {
        List<VistaIncidencias> vistaIncidencias = vistaService.getIncidencias();
        model.addAttribute("vistaIncidencias", vistaIncidencias);
        return "/panel/explorar/exportar/incidencias";
    }

    @GetMapping("/exportarOrdenadoresCsv")
    public void exportarOrdenadoresCsv(HttpServletResponse response) throws IOException {
        log.info("Exportando ordenadores a CSV");
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; file=ordenadores.csv");

        List<VistaOrdenadores> vistaOrdenadores = vistaService.getOrdenadores();

        try (CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.DEFAULT.withHeader("ID", "Etiqueta", "Número de Serie", "Modelo", "Servicio", "Localización", "Contrato", "Garantía", "Activo", "Propuesto retirada", "Fecha de Alta", "Fecha de Actualización", "Fecha de Baja"))) {
            for (VistaOrdenadores ordenador : vistaOrdenadores) {
                csvPrinter.printRecord(ordenador.getId(), ordenador.getEtiqueta(), ordenador.getNMeroDeSerie(), ordenador.getModelo(), ordenador.getServicio(), ordenador.getLocalización(), ordenador.getContrato(), ordenador.getGarantía(), ordenador.getActivo(), ordenador.getPropuestoRetirada(), ordenador.getFechaDeAlta(), ordenador.getFechaDeActualizaciN(), ordenador.getFechaDeBaja());
            }
            csvPrinter.flush();
        }
    }


    @GetMapping("/exportarMonitoresCsv")
    public void exportarMonitoresCsv(HttpServletResponse response) throws IOException {
        log.info("Exportando monitores a CSV");
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; file=monitores.csv");

        List<VistaMonitores> vistaMonitores = vistaService.getMonitores();

        try (CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.DEFAULT.withHeader("ID", "Etiqueta", "Número de Serie", "Modelo", "Servicio", "Localización", "Contrato", "Garantía", "Activo", "Propuesto retirada", "Fecha de Alta", "Fecha de Actualización", "Fecha de Baja"))) {
            for (VistaMonitores monitor : vistaMonitores) {
                csvPrinter.printRecord(monitor.getId(), monitor.getEtiqueta(), monitor.getNMeroDeSerie(), monitor.getModelo(), monitor.getServicio(), monitor.getLocalización(), monitor.getContrato(), monitor.getGarantía(), monitor.getActivo(), monitor.getPropuestoRetirada(), monitor.getFechaDeAlta(), monitor.getFechaDeActualizaciN(), monitor.getFechaDeBaja());
            }
            csvPrinter.flush();
        }
    }

    @GetMapping("/exportarIncidenciasCsv")
    public void exportarIncidenciasCsv(HttpServletResponse response) throws IOException {
        log.info("Exportando incidencias a CSV");
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; file=incidencias.csv");

        List<VistaIncidencias> vistaIncidencias = vistaService.getIncidencias();

        try (CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.DEFAULT.withHeader("ID", "Etiqueta", "Estado", "Descripción", "Servicio", "Localización", "Fecha de Alta", "Fecha de Actualización", "Fecha de Baja"))) {
            for (VistaIncidencias incidencia : vistaIncidencias) {
                csvPrinter.printRecord(incidencia.getId(), incidencia.getEtiqueta(), incidencia.getEstado(), incidencia.getDescripción(), incidencia.getServicio(), incidencia.getLocalización(), incidencia.getFechaDeAlta(), incidencia.getFechaDeActualizaciN(), incidencia.getFechaDeBaja());
            }
            csvPrinter.flush();
        }
    }

    @GetMapping("/exportarOrdenadoresXml")
    public void exportarOrdenadoresXml(HttpServletResponse response) throws IOException {
        log.info("Exportando ordenadores a XML");
        response.setContentType("application/xml");
        response.setHeader("Content-Disposition", "attachment; filename=ordenadores.xml");

        List<VistaOrdenadores> vistaOrdenadores = vistaService.getOrdenadores();

        try (PrintWriter writer = response.getWriter()) {
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.println("<ordenadores>");
            for (VistaOrdenadores ordenador : vistaOrdenadores) {
                writer.println("  <ordenador>");
                writer.println("    <id>" + ordenador.getId() + "</id>");
                writer.println("    <etiqueta>" + ordenador.getEtiqueta() + "</etiqueta>");
                writer.println("    <numeroDeSerie>" + ordenador.getNMeroDeSerie() + "</numeroDeSerie>");
                writer.println("    <modelo>" + ordenador.getModelo() + "</modelo>");
                writer.println("    <servicio>" + ordenador.getServicio() + "</servicio>");
                writer.println("    <localizacion>" + ordenador.getLocalización() + "</localizacion>");
                writer.println("    <contrato>" + ordenador.getContrato() + "</contrato>");
                writer.println("    <garantia>" + ordenador.getGarantía() + "</garantia>");
                writer.println("    <activo>" + ordenador.getActivo() + "</activo>");
                writer.println("    <propuestoRetirada>" + ordenador.getPropuestoRetirada() + "</propuestoRetirada>");
                writer.println("    <fechaDeAlta>" + ordenador.getFechaDeAlta() + "</fechaDeAlta>");
                writer.println("    <fechaDeActualizacion>" + ordenador.getFechaDeActualizaciN() + "</fechaDeActualizacion>");
                writer.println("    <fechaDeBaja>" + ordenador.getFechaDeBaja() + "</fechaDeBaja>");
                writer.println("  </ordenador>");
            }
            writer.println("</ordenadores>");
            writer.flush();
        }
    }

    @GetMapping("/exportarMonitoresXml")
    public void exportarMonitoresXml(HttpServletResponse response) throws IOException {
        log.info("Exportando monitores a XML");
        response.setContentType("application/xml");
        response.setHeader("Content-Disposition", "attachment; filename=monitores.xml");

        List<VistaMonitores> vistaMonitores = vistaService.getMonitores();

        try (PrintWriter writer = response.getWriter()) {
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.println("<monitores>");
            for (VistaMonitores monitor : vistaMonitores) {
                writer.println("  <monitor>");
                writer.println("    <id>" + monitor.getId() + "</id>");
                writer.println("    <etiqueta>" + monitor.getEtiqueta() + "</etiqueta>");
                writer.println("    <numeroDeSerie>" + monitor.getNMeroDeSerie() + "</numeroDeSerie>");
                writer.println("    <modelo>" + monitor.getModelo() + "</modelo>");
                writer.println("    <servicio>" + monitor.getServicio() + "</servicio>");
                writer.println("    <localizacion>" + monitor.getLocalización() + "</localizacion>");
                writer.println("    <contrato>" + monitor.getContrato() + "</contrato>");
                writer.println("    <garantia>" + monitor.getGarantía() + "</garantia>");
                writer.println("    <activo>" + monitor.getActivo() + "</activo>");
                writer.println("    <propuestoRetirada>" + monitor.getPropuestoRetirada() + "</propuestoRetirada>");
                writer.println("    <fechaDeAlta>" + monitor.getFechaDeAlta() + "</fechaDeAlta>");
                writer.println("    <fechaDeActualizacion>" + monitor.getFechaDeActualizaciN() + "</fechaDeActualizacion>");
                writer.println("    <fechaDeBaja>" + monitor.getFechaDeBaja() + "</fechaDeBaja>");
                writer.println("  </monitor>");
            }
            writer.println("</monitores>");
            writer.flush();
        }
    }

    @GetMapping("/exportarIncidenciasXml")
    public void exportarIncidenciasXml(HttpServletResponse response) throws IOException {
        log.info("Exportando incidencias a XML");
        response.setContentType("application/xml");
        response.setHeader("Content-Disposition", "attachment; filename=incidencias.xml");

        List<VistaIncidencias> vistaIncidencias = vistaService.getIncidencias();

        try (PrintWriter writer = response.getWriter()) {
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.println("<incidencias>");
            for (VistaIncidencias incidencia : vistaIncidencias) {
                writer.println("  <incidencia>");
                writer.println("    <id>" + incidencia.getId() + "</id>");
                writer.println("    <etiqueta>" + incidencia.getEtiqueta() + "</etiqueta>");
                writer.println("    <estado>" + incidencia.getEstado() + "</estado>");
                writer.println("    <descripcion>" + incidencia.getDescripción() + "</descripcion>");
                writer.println("    <servicio>" + incidencia.getServicio() + "</servicio>");
                writer.println("    <localizacion>" + incidencia.getLocalización() + "</localizacion>");
                writer.println("    <fechaDeAlta>" + incidencia.getFechaDeAlta() + "</fechaDeAlta>");
                writer.println("    <fechaDeActualizacion>" + incidencia.getFechaDeActualizaciN() + "</fechaDeActualizacion>");
                writer.println("    <fechaDeBaja>" + incidencia.getFechaDeBaja() + "</fechaDeBaja>");
                writer.println("  </incidencia>");
            }
            writer.println("</incidencias>");
            writer.flush();
        }
    }
}