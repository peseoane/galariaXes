package com.peseoane.galaria.xestion.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class MultipleAuthProvidersSecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(MultipleAuthProvidersSecurityConfig.class);

    @Autowired
    private ActiveDirectoryProvider activeDirectoryProvider;

    @Autowired
    private BasicAuthProvider basicAuthProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector)
            throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);
        http.authorizeHttpRequests((authorize) -> authorize.requestMatchers("/favicon.png",
                                                                            "/",
                                                                            "/login",
                                                                            "/logout",
                                                                            "/pwa/**",
                                                                            "/ws/**",
                                                                            "/panel/aplicativo/xml/actualizarInformacion",
                                                                            "/actuator/**",
                                                                            "/css/**",
                                                                            "/fonts/**",
                                                                            "/svg/**",
                                                                            "/js/**",
                                                                            "/logo/**",
                                                                            "/video/**",
                                                                            "/img/**",
                                                                            "/calidade/**").permitAll().anyRequest()
                        .fullyAuthenticated()).csrf((csrf) -> csrf
                .ignoringRequestMatchers("/panel/aplicativo/xml/actualizarInformacion"))
                .formLogin(formLogin -> formLogin.loginPage("/login").usernameParameter("username")
                        .passwordParameter("password").failureHandler((request, response, exception) -> {
                            response.sendRedirect("/login/error");
                        }).defaultSuccessUrl("/bienvenida", true).permitAll())
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/logout").permitAll())
                .authenticationProvider(basicAuthProvider.authenticationProvider())
                .authenticationProvider(activeDirectoryProvider.activeDirectoryLdapAuthenticationProviderPrimary())
                .authenticationProvider(activeDirectoryProvider.activeDirectoryLdapAuthenticationProviderSecondary());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}