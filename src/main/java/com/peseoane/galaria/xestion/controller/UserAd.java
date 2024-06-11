package com.peseoane.galaria.xestion.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
public class UserAd {

    @GetMapping("/user")
    public Authentication getLoggedUserDeatil() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String authorityString = auth.toString();

        return auth;
    }

}