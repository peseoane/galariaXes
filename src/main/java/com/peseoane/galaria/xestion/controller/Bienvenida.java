package com.peseoane.galaria.xestion.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;

@Slf4j
@Controller
@RequestMapping("/bienvenida")
public class Bienvenida {

    @GetMapping("")
    public String bienvenida(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // Separamos "dn": "CN=Seoane Prado\\, Pedro,OU=Informatica,OU=Usuarios,OU=Vigo,OU=tuOuAd,dc=TuDominiolocal",
        // En una lista, eliminamos también las " \\ " que se añaden en el nombre

        // Una vez logeado puedes ver el esquema en https://localhost:8080/user

        Object usuarioAutenticado = auth.getPrincipal();
        log.info("Usuario autenticado: {}", usuarioAutenticado.toString());
        for (String parametro : usuarioAutenticado.toString().split(",")) {
            if (parametro.toUpperCase().contains("INFORMATICA") || username.toUpperCase().contains("GALARIA") ) {
                model.addAttribute("departamento", "GRUPO_ADMIN");
                break;
            } else if (parametro.toUpperCase().contains("RT")) {
                model.addAttribute("departamento", "GRUPO_USUARIOS");
                break;
            } else if (parametro.toUpperCase().contains("FM")){
                model.addAttribute("departamento", "GRUPO_USUARIOS");
                break;
            } else if (parametro.toUpperCase().contains("DI")){
                model.addAttribute("departamento", "GRUPO_USUARIOS");
                break;
            } else if (parametro.toUpperCase().contains("FM")) {
                model.addAttribute("departamento", "GRUPO_USUARIOS");
                break;
            } else {
                model.addAttribute("departamento", "NO_AUTORIZADO");
            }
        }

        // if for authority in authoritis is GGAR_FICINFORMATICA

        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("GGAR_FICINFORMATICA")) {
                model.addAttribute("departamento", "GRUPO_ADMIN");
                break;
            }
        }

        return "bienvenida";
    }

}