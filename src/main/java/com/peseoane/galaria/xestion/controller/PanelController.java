package com.peseoane.galaria.xestion.controller;

import com.peseoane.galaria.xestion.etc.ThymeleafLink;
import com.peseoane.galaria.xestion.service.ActiveDirectoryLdapQuery;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/panel")
@ControllerAdvice
public class PanelController {

    private static final Logger log = LoggerFactory.getLogger(PanelController.class);

    /* LÓGICA DE ENRUTAMIENTO
     * La navbar se genera de forma dinámica, pero HTMX ve las rutas relativas desde su posición actual, cuando le digo
     * que haga push-url se refiere A LA VISTA, pero cuando pide, solo recupera el contenido de la vista y hace el
     * reemplazo,
     * por lo que URL NAVEGACION != URL PETICION dado que, reactividad y esas cositas...
     */
    private final ThymeleafLink estadisticas = new ThymeleafLink("/panel", "Estadísticas", "estadisticas");
    private final ThymeleafLink xml = new ThymeleafLink("/panel", "Subir XML", "xml");
    private final ThymeleafLink explorar = new ThymeleafLink("/panel", "Explorar", "explorar");
    /* Lista de links para la navbar */
    private final List<ThymeleafLink> links = new ArrayList<>();
    @Autowired
    ActiveDirectoryLdapQuery ldapQueryService;

    /* EXPLICACIÓN
     * La ruta de: href_aplicativo es el HTML del aplicativo en sí.
     * La ruta de: href_vista nos devuelve la vista ENTERA con el aplicativo ya cargado.
     */
    public PanelController() {
        links.add(estadisticas);
        links.add(xml);
        links.add(explorar);
    }

    @ModelAttribute("currentUser")
    public Authentication getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /* Común a todos los controladores en caso de necesitar refrescar la navbar */
    @ModelAttribute("links")
    public List<ThymeleafLink> links() {
        return links;
    }

    @ModelAttribute("currentIp")
    public String getCurrentIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    @ModelAttribute("currentHost")
    public String getCurrentHost(HttpServletRequest request) {
            return request.getRemoteHost();
    }

    /* ZONA DE VISTAS
     * Realmente solo hay una, el parámetro que le mandamos es para que al hacer el push-url HTMX nos recupere
     * la vista "actual" y que no sea una SPA innavegable
     */

    @GetMapping({"/", ""})
    public String index() {
        return "redirect:/panel/estadisticas";
    }

    @GetMapping({"/estadisticas"})
    public String index(Model model) {
        model.addAttribute("rutaDestino", estadisticas.getHrefAplicativo());
        return "panel/indexPanel";
    }

    @GetMapping("/xml")
    public String subirXml(Model model) {
        model.addAttribute("rutaDestino", xml.getHrefAplicativo());
        return "panel/indexPanel";
    }

    @GetMapping("/explorar")
    public String explorar(Model model) {
        model.addAttribute("rutaDestino", explorar.getHrefAplicativo());
        return "panel/indexPanel";
    }


    /* HACKS
     * La única finalidad de esto es engañar a IntelliJ porque no resuelve variables por fragmentos, no se usa nunca
     */

    @GetMapping("/navbar")
    public String navbar(Model model) {
        model.addAttribute("links", links);
        return "fragments/navbar";
    }

    @GetMapping("/footer")
    public String footer() {
        return "fragments/footer";
    }

}