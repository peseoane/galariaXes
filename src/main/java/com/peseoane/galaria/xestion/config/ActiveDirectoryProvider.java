package com.peseoane.galaria.xestion.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class ActiveDirectoryProvider {

    @Value("${spring.ldap.domain}")
    private String domain;

    @Value("${spring.ldap.url.primary}")
    private String urlPrimary;

    @Value("${spring.ldap.url.secondary}")
    private String urlSecondary;

    @Value("${spring.ldap.rootDn}")
    private String rootDn;

    @Bean
    public FailoverActiveDirectory activeDirectoryLdapAuthenticationProviderPrimary() {
        return new FailoverActiveDirectory(domain, urlPrimary, rootDn);
    }

    @Bean
    public FailoverActiveDirectory activeDirectoryLdapAuthenticationProviderSecondary() {
        return new FailoverActiveDirectory(domain, urlSecondary, rootDn);
    }


}