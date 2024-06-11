package com.peseoane.galaria.xestion.service;

import com.peseoane.galaria.xestion.model.*;
import jakarta.transaction.Transactional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MetaService {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MetaService.class);
    private final FabricanteService fabricanteService;
    private final ProcesadorService procesadorService;
    private final SistemaOperativoService sistemaOperativoService;
    private final TipoElementoService tipoElementoService;
    private final GpuService gpuService;
    private final EtiquetaService etiquetaService;
    private final NetworkService networkService;
    private final OrdenadorService ordenadorService;
    private final MonitorService monitorService;
    private final LogService logService;
    private final LocalizacionService localizacionService;

    public MetaService(FabricanteService fabricanteService,
                       ProcesadorService procesadorService,
                       SistemaOperativoService sistemaOperativoService,
                       TipoElementoService tipoElementoService,
                       GpuService gpuService,
                       EtiquetaService etiquetaService,
                       NetworkService networkService,
                       OrdenadorService ordenadorService,
                       MonitorService monitorService,
                       LogService logService,
                       LocalizacionService localizacionService) {
        this.fabricanteService = fabricanteService;
        this.procesadorService = procesadorService;
        this.sistemaOperativoService = sistemaOperativoService;
        this.tipoElementoService = tipoElementoService;
        this.gpuService = gpuService;
        this.etiquetaService = etiquetaService;
        this.networkService = networkService;
        this.ordenadorService = ordenadorService;
        this.monitorService = monitorService;
        this.logService = logService;
        this.localizacionService = localizacionService;
    }

    public boolean procesarBackupXml(String encodedFile, String nombreHost) {
        Document doc = Jsoup.parse(encodedFile, "", org.jsoup.parser.Parser.xmlParser());
        return parseFile(doc, nombreHost);
    }

    public boolean procesarBackupXml(MultipartFile file,String nombreFichero) throws IOException {
        InputStream inputStream = file.getInputStream();
        Document doc = Jsoup.parse(inputStream, "UTF-8", "", org.jsoup.parser.Parser.xmlParser());
        return parseFile(doc,nombreFichero);
    }

    @Transactional
    public boolean parseFile(Document xmlData,String nombreFichero) {
        try {
            String xmlEtiqueta = xmlData.select("ordenador > etiqueta").text().trim();
            if (xmlEtiqueta.isBlank()) {
                logService.autoLog("ERROR", "El ordenador DEBE tener una etiqueta [" + nombreFichero + "]");
                return false;
            }
            Etiqueta etiquetaOrdenador = etiquetaService.procesarEtiqueta(xmlEtiqueta);

            String numeroSerie = xmlData.select("ordenador > numero_serie").text().trim();
            List<String> invalidCommands = Arrays.asList("system serial number",
                                                         "to be filled by o.e.m.",
                                                         "default string",
                                                         "not specified",
                                                         "not available",
                                                         "system",
                                                         "serial number",
                                                         "default",
                                                         "false");

            boolean isValid = invalidCommands.stream().noneMatch(numeroSerie.toLowerCase()::contains);

            if (numeroSerie.isBlank() || numeroSerie.length() < 3 || !isValid) {
                logService.autoLog("ERROR",
                                   etiquetaService.procesarEtiqueta(xmlEtiqueta),
                                   "El ordenador DEBE tener un número de serie válido [" + etiquetaOrdenador.getReferencia() + "]");
                return false;
            }


            List<Procesador> procesadoresNuevos = new ArrayList<>();
            Elements cpus = xmlData.select("cpu_socket > cpu");
            for (Element cpuElement : cpus) {
                String xmlCpuReferencia = cpuElement.select("referencia").text();
                Fabricante xmlCpuFabricante = fabricanteService.procesarFabricante(cpuElement.select("fabricante")
                                                                                           .text()
                                                                                           .trim());
                int xmlCpuVelocidad = Integer.parseInt(cpuElement.select("velocidad").text().trim());
                int xmlCpuNucleos = Integer.parseInt(cpuElement.select("nucleos").text().trim());
                int xmlCpuHilos = Integer.parseInt(cpuElement.select("hilos").text().trim());
                Procesador procesador = procesadorService.procesarProcesador(xmlCpuReferencia,
                                                                             xmlCpuFabricante,
                                                                             xmlCpuVelocidad,
                                                                             xmlCpuNucleos,
                                                                             xmlCpuHilos);
                procesadoresNuevos.add(procesador);
            }


            List<Gpu> gpusNuevas = new ArrayList<>();
            Elements gpus = xmlData.select("gpu_adapter > gpu");
            for (Element gpuElement : gpus) {
                String xmlGpuFabricante = "";
                xmlGpuFabricante = gpuElement.select("fabricante").text().trim();
                Fabricante gpuFabricante = fabricanteService.procesarFabricante(xmlGpuFabricante);
                String xmlGpuReferencia = gpuElement.select("referencia").text().trim();
                Gpu gpu = gpuService.procesarGpu(xmlGpuReferencia, gpuFabricante);
                gpusNuevas.add(gpu);
            }


            Element xmlSistemaOperativoElement = xmlData.selectFirst("sistema_operativo");
            String xmlSistemaOperativoNombre = xmlSistemaOperativoElement.select("nombre").text().trim();
            String xmlSistemaOperativoVersion = xmlSistemaOperativoElement.select("version").text().trim();
            String xmlSistemaOperativoCompilacion = xmlSistemaOperativoElement.select("compilacion").text().trim();
            String xmlSistemaOperativoCodename = xmlSistemaOperativoElement.select("codename").text().trim();
            SistemaOperativo sistemaOperativo = sistemaOperativoService.procesarSistemaOperativo(
                    xmlSistemaOperativoNombre,
                    xmlSistemaOperativoVersion,
                    xmlSistemaOperativoCompilacion,
                    xmlSistemaOperativoCodename);

            String xmlModeloPcReferencia = xmlData.select("modelo_pc > modelo").text().trim();
            String xmlModeloPcFabricante = xmlData.selectFirst("ordenador > fabricante").text().trim();

            Fabricante modeloPcFabricante = fabricanteService.procesarFabricante(xmlModeloPcFabricante);
            ModeloOrdenador modeloOrdenador = ordenadorService.procesarModeloOrdenador(xmlModeloPcReferencia, modeloPcFabricante);

            TipoElemento tipoElemento = tipoElementoService.porEtiqueta(etiquetaOrdenador);

            long xmlOrdenadorRam = Long.parseLong(xmlData.select("ram").text());
            boolean xmlOrdenadorActivo = true;

            Ordenador ordenador = ordenadorService.procesarOrdenador(tipoElemento,
                                                                     etiquetaOrdenador,
                                                                     numeroSerie,
                                                                     modeloOrdenador,
                                                                     sistemaOperativo,
                                                                     xmlOrdenadorRam,
                                                                     xmlOrdenadorActivo);


            try {
                localizacionService.actualizarPropietarioAd(ordenador);
            } catch (Exception e) {
                logService.autoLog("ERROR",
                                   etiquetaOrdenador,
                                   "No se ha podido actualizar el propietario en el Directorio Activo");
            }

            List<OrdenadorNetwork> OrdenadorInterfacesRed = new ArrayList<>();
            Elements xmlOrdenadorInterfacesRed = xmlData.select("network > ip");
            for (Element xmlOrdenadorInterfazRed : xmlOrdenadorInterfacesRed) {
                String xmlOrdenadorInterfazRedIpv4 = xmlOrdenadorInterfazRed.select("ipv4").text().trim();
                String xmlOrdenadorInterfazRedMac = xmlOrdenadorInterfazRed.select("mac").text().trim();
                String xmlOrdenadorInterfazRedNotas = xmlOrdenadorInterfazRed.select("description").text().trim();
                String xmlOrdenadorInterfazRedBoca = "sin asignar";
                OrdenadorNetwork ordenadorNetwork = networkService.procesarNetwork(ordenador,
                                                                                   xmlOrdenadorInterfazRedIpv4,
                                                                                   xmlOrdenadorInterfazRedMac,
                                                                                   xmlOrdenadorInterfazRedBoca,
                                                                                   xmlOrdenadorInterfazRedNotas);
                OrdenadorInterfacesRed.add(ordenadorNetwork);
            }

            List<Monitor> monitoresViejos = monitorService.getMonitoresByOrdenador(ordenador);
            List<Monitor> monitoresNuevos = new ArrayList<>();
            Elements xmlMonitores = xmlData.select("monitores > monitor");

            for (Element xmlMonitor : xmlMonitores) {
                String xmlMonitorNumeroSerie = xmlMonitor.select("numero_serie").text().trim();
                log.info("Procesando monitor con SN: " + xmlMonitorNumeroSerie);
                Fabricante xmlMonitorFabricante = fabricanteService.procesarFabricante(xmlMonitor.select("fabricante")
                                                                                               .text()
                                                                                               .trim());
                String xmlMonitorReferencia = xmlMonitor.select("referencia").text().trim();

                if (xmlMonitorNumeroSerie.isBlank() || xmlMonitorNumeroSerie.length() < 3) {
                    String logMessage =
                            "El ordenador [" + etiquetaOrdenador.getReferencia() + "] tiene un monitor " + "sin " +
                                    "número de serie que no puede ser inventariado.";
                    logService.autoLog("ERROR", etiquetaOrdenador, logMessage);
                    return false;
                }

                ModeloMonitor xmlMonitorModeloMonitor = monitorService.procesarModeloMonitor(xmlMonitorReferencia,
                                                                                             xmlMonitorFabricante);
                Monitor monitor = monitorService.procesarMonitor(ordenador,
                                                                 xmlMonitorModeloMonitor,
                                                                 xmlMonitorNumeroSerie);
                monitoresNuevos.add(monitor);

                Localizacion ubicacionOrdenadorPadre = localizacionService.getLastTrazabilidad(ordenador);
                Servicio servicioOrdenadorPadre = localizacionService.getLastServicio(ordenador);

                try {
                    localizacionService.procesarLocalizacionMonitor(monitor,
                                                                    ubicacionOrdenadorPadre,
                                                                    servicioOrdenadorPadre);
                } catch (Exception e) {

                    StringBuilder logMessage = new StringBuilder();

                    if (monitor.getModeloMonitor().getReferencia().isBlank()) {
                        logMessage.append("El monitor que intenta agregar el PC no tiene etiqueta asignada. ")
                                .append(etiquetaOrdenador.getReferencia());
                        logService.autoLog("ERROR", ordenador.getEtiqueta(), logMessage.toString());
                        logMessage.setLength(0); // Limpiar el StringBuilder para el siguiente uso
                    }

                    logMessage.append("El monitor con SN [")
                            .append(xmlMonitorNumeroSerie)
                            .append("] del PC [")
                            .append(xmlEtiqueta)
                            .append("] no tiene etiqueta asignada.");
                    logService.autoLog("ERROR", ordenador.getEtiqueta(), logMessage.toString());

                }
            }

            monitorService.buscarMonitoresHuerfanos(monitoresViejos, monitoresNuevos);

        } catch (Exception e) {
            logService.autoLog("ERROR", "Error al parsear el XML [" + nombreFichero + "] - " + e.getMessage());
            return false;
        }
        return true;
    }

    public void procesarEtiquetasXml(MultipartFile file) {

        try {
            InputStream inputStream = file.getInputStream();
            Document doc = Jsoup.parse(inputStream, "UTF-8", "", org.jsoup.parser.Parser.xmlParser());

            Elements monitors = doc.select("monitor");
            for (Element monitor : monitors) {
                String fabricante = monitor.select("fabricante").text();
                String modelo = monitor.select("modelo").text();
                String serial = monitor.select("serial").text();
                String etiqueta = monitor.select("etiqueta").text();

                if (etiqueta.isBlank()) {
                    if (serial.isBlank()) {
                        String logMessage = "El monitor sin etiqueta ni número de serie no puede ser inventariado";
                        logService.autoLog("ERROR", logMessage);
                        continue;
                    }
                    String logMessage = "El monitor sin etiqueta no puede ser inventariado";
                    logService.autoLog("ERROR", logMessage);
                }

                if (etiqueta.toLowerCase().contains("sin") || etiqueta.toLowerCase()
                        .contains("quemu") || etiqueta.isBlank() || etiqueta.contains("@")) {
                    String logMessage = "La etiqueta [" + etiqueta + "] del monitor con SN [" + serial + "] y modelo "
                            + "[" + modelo + "] no es válida";
                    logService.autoLog("ERROR", logMessage);
                    continue;
                }

                if (monitorService.isEtiquetaYaAsignada(etiqueta)) {
                    String logMessage = "CONSISTENCIA! La etiqueta [" + etiqueta + "] ya está asignada a otro monitor!";
                    logService.autoLog("ERROR", logMessage);
                    continue;
                }

                Fabricante fabricanteMonitor = fabricanteService.procesarFabricante(fabricante);
                ModeloMonitor modeloMonitor = monitorService.procesarModeloMonitor(modelo, fabricanteMonitor);
                Etiqueta etiquetaMonitor = etiquetaService.procesarEtiqueta(etiqueta);
                monitorService.agregarInformacionMonitorDeGreko(etiquetaMonitor, serial, modeloMonitor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void procesarPropietarios(MultipartFile file) {
        /**
         * <Computers>
         *   <Computer>
         *     <Name>ArtFIBIO-PC</Name>
         *     <ipv4Address></ipv4Address>
         *     <FQOU dc="local" cn="ArtFIBIO-PC">
         *       <OU>Equipos</OU>
         *       <OU root="Equipos">Vigo</OU>
         *       <OU root="Vigo">MEDTEC</OU>
         *       <OU root="MEDTEC">Fundaciones</OU>
         *     </FQOU>
         *   </Computer>
         *   <Computer>
         *     <Name>IPLANRT</Name>
         *     <ipv4Address></ipv4Address>
         *     <FQOU dc="local" cn="IPLANRT">
         *       <OU>WSUS</OU>
         *       <OU root="WSUS">Equipos</OU>
         *       <OU root="Equipos">Vigo</OU>
         *       <OU root="Vigo">MEDTEC</OU>
         *       <OU root="MEDTEC">Fundaciones</OU>
         *     </FQOU>
         *   </Computer>
         */

        try {
            InputStream inputStream = file.getInputStream();
            Document doc = Jsoup.parse(inputStream, "UTF-8", "", org.jsoup.parser.Parser.xmlParser());
            Elements Computers = doc.select("Computer");
            for (Element computer : Computers) {
                String etiqueta = computer.select("Name").text();
                // Select the first OU element
                Elements ouElements = computer.select("OU");
                String servicio;
                if (ouElements.first().text().matches("\\d+")) {
                    // Si el primer elemento es numérico, seleccionamos el segundo
                    servicio = ouElements.get(1).text();
                } else {
                    // Si no, seleccionamos el primero
                    servicio = ouElements.first().text();
                }
                String localizacion = ouElements.get(ouElements.size() - 3).text();
                Etiqueta etiquetaActualizar = etiquetaService.procesarEtiqueta(etiqueta);
                // concat cn + all ou + dc
                String ous = "";
                for (Element ou : computer.select("OU")) {
                    ous += ou.text() + ", ";
                }
                Localizacion localizacionOrdenador =
                        localizacionService.procesarLocalizacionEtiqueta(etiquetaActualizar,
                                                                         servicio,
                                                                         localizacion,
                                                                         ous);
                String logMessage =
                        "Propietario actualizado offline desde XML Localización: " + localizacionOrdenador.getLocalizacion();
                logService.autoLog("INFO", etiquetaActualizar, logMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}