package com.peseoane.galaria.xestion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicController {

    /** QUEDA PENDIENTE DE IMPLEMENTAR LOS MENUS DE CALIDADE **/
    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

}