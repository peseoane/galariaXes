package com.peseoane.galaria.xestion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {


    /*
    La identificación POST se realiza mediante el builder de la clase WebSecurityConfig,
    dado que ahí le especificamos que use LDAP y otras cosas.
     */

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, Model model) {
        return "/login/login";
    }

    @GetMapping("/login/error")
    public String loginError(Model model) {
        model.addAttribute("msg", "Usuario o contraseña incorrectos");
        model.addAttribute("loginError", true);
        return "/fragments/errorAlert";
    }


    @GetMapping("/logout")
    public String logoutPage() {
        return "/login/logout";
    }



}