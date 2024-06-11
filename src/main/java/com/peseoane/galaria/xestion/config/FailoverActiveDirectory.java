package com.peseoane.galaria.xestion.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;

public class FailoverActiveDirectory implements AuthenticationProvider {

    private static final Logger log = LoggerFactory.getLogger(FailoverActiveDirectory.class);
    private ActiveDirectoryLdapAuthenticationProvider provider;

    private String domain;
    private String url;
    private String rootDn;

    public FailoverActiveDirectory(String domain, String url, String rootDn) {
        this.domain = domain;
        this.url = url;
        this.rootDn = rootDn;
        provider = new ActiveDirectoryLdapAuthenticationProvider(domain, url, rootDn);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            return provider.authenticate(authentication);
        } catch (InternalAuthenticationServiceException e) {
            log.error("Error en la autenticación con el AD: {}. Detalles del servidor: Dominio - {}, URL - {}, Root DN - {}",
                      e.getMessage(), domain, url, rootDn);
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return provider.supports(authentication);
    }
}