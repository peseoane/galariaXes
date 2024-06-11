package com.peseoane.galaria.xestion.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    /** Esto lo usaremos cuando esté desplegada la página para que Thymeleaf cargue los recursos minimizados **/
    @Value("${isProd}")
    private boolean debugStatus;

    @ModelAttribute("isProd")
    public boolean getDebugStatus() {
        return debugStatus;
    }


}