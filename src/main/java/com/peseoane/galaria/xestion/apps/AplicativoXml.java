package com.peseoane.galaria.xestion.apps;

import com.peseoane.galaria.xestion.handler.XmlWebSocketHandler;
import com.peseoane.galaria.xestion.model.Log;
import com.peseoane.galaria.xestion.service.LogService;
import com.peseoane.galaria.xestion.service.MetaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Controller
@RequestMapping("/panel/aplicativo/xml")
public class AplicativoXml {

    private static final Logger log = LoggerFactory.getLogger(AplicativoXml.class);
    private final MetaService metaService;
    private final LogService logService;
    private final HttpServletResponse httpServletResponse;
    private final SpringTemplateEngine templateEngine;
    private final XmlWebSocketHandler xmlWebSocketHandler;

    public AplicativoXml(MetaService metaService, LogService logService, HttpServletResponse httpServletResponse, XmlWebSocketHandler xmlWebSocketHandler, SpringTemplateEngine templateEngine) {
        this.metaService = metaService;
        this.logService = logService;
        this.httpServletResponse = httpServletResponse;
        this.xmlWebSocketHandler = xmlWebSocketHandler;
        this.templateEngine = templateEngine;
    }


    /**
     * Muestra el formulario de subida de archivos XML.
     * @param model
     * @return
     */
    @GetMapping("")
    public String xeral(Model model) {
        String wsUuid = UUID.randomUUID().toString();
        model.addAttribute("wsUuid", wsUuid);
        log.info("UUID: {}", wsUuid);
        return "/panel/xml/formularioSubidaXml";
    }

    /*
    private String convertirXml(MultipartFile uploadXml) throws Exception {
        try (InputStream inputStream = uploadXml.getInputStream(); BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder xmlContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                xmlContent.append(line).append("\n");
            }
            return xmlContent.toString();
        } catch (Exception e) {
            throw new Exception("Error al convertir el archivo XML a cadena -> " + e.getMessage(), e);
        }
    }
    */


    /**
     * Procesa los archivos XML subidos por el usuario. Puede procesar tres tipos de archivo, los cuales pueden generarse
     * dese la carpeta de scripts de powershell.
     *
     * Hay un script que genera "datos.xml" y otro que genera "propietarios.xml". Son para saber offline los grupos del AD
     * y, por otro lado, recuperar las etiquetas viejas que puso Greco en los diferentes PCs.
     *
     * En realidad solo hace falta trabajar con el XML normal de inventariar dato que es lo importante una vez realizada
     * la primera migración.
     *
     * @param upload_xml Lista de archivos XML subidos por el usuario.
     * @param wsUuid    UUID de la sesión WebSocket, para mostrar el progreso de la carga.
     * @param model
     * @return
     * @throws Exception
     */
    @PostMapping("/procesar")
    public String uploadXml(@RequestParam("upload_xml") List<MultipartFile> upload_xml, @RequestParam("wsUuid") String wsUuid, Model model) throws Exception {

        String htmlMessage = templateEngine.process("panel/xml/wsProcesamiento", new Context());
        xmlWebSocketHandler.sendMessage(wsUuid, htmlMessage);

        LocalDateTime inicio = LocalDateTime.now();
        for (MultipartFile file : upload_xml) {
            // Renderizar y enviar el fragmento 'procesandoArchivo'
            Context context = new Context();
            context.setVariable("filename", file.getOriginalFilename());
            htmlMessage = templateEngine.process("panel/xml/wsArchivoActual", context);
            xmlWebSocketHandler.sendMessage(wsUuid, htmlMessage);

            try {
                if (file.getOriginalFilename().toLowerCase().contains("datos")) {
                    metaService.procesarEtiquetasXml(file);
                } else if (file.getOriginalFilename().toLowerCase().contains("propietarios")) {
                    metaService.procesarPropietarios(file);
                } else {
                    metaService.procesarBackupXml(file, file.getOriginalFilename());
                }
            } catch (Exception e) {
                xmlWebSocketHandler.closeSession(wsUuid);
                String error_titulo = "Error general";
                String error_mensaxe = "Ha ocurrido un error";
                logService.autoLog("ERROR", "Error general" + e.getMessage());
                model.addAttribute("error_titulo", error_titulo);
                model.addAttribute("error_mensaxe", error_mensaxe);
                model.addAttribute("error_trace", e.getMessage());
                return "/fragments/error";
            }
        }

        xmlWebSocketHandler.closeSession(wsUuid);

        LocalDateTime fin = LocalDateTime.now();
        List<Log> logs = logService.getLogsBetween(inicio, fin);
        model.addAttribute("logs", logs);
        return "/panel/xml/respuestaLogsXml";
    }

    @GetMapping("/actualizarInformacion")
    @ResponseBody
    public String actualizarInformacion() {
        return "No se permiten peticiones GET en este endpoint.";
    }

    /**
     * Actualiza la información de la base de datos a partir de un archivo XML enviado desde la tarea programada de
     * Powershell. No requiere CSRF este endpoint está exento (consultar clase de seguridad para conocer más detalles).
     *
     * @param xml     Archivo XML con la información a actualizar.
     * @param request Petición HTTP.
     * @return Respuesta HTTP.
     */
    @PostMapping("/actualizarInformacion")
    public ResponseEntity<String> actualizarInformacion(@RequestBody String xml, HttpServletRequest request) {
        try {
            String host = request.getRemoteAddr();
            metaService.procesarBackupXml(xml, host);
            log.info("Archivo XML procesado exitosamente. Host: {}", host);
            logService.autoLog("INFO", "Archivo XML procesado exitosamente. Host: " + host);
            return ResponseEntity.ok("Archivo XML procesado exitosamente.");
        } catch (Exception e) {
            log.error("Error al procesar el archivo XML: {}", e.getMessage());
            logService.autoLog("ERROR", "Error al procesar el archivo XML: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar el archivo XML: " + e.getMessage());
        }
    }



}