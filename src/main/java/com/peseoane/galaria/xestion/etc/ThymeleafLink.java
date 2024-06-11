package com.peseoane.galaria.xestion.etc;

import lombok.Getter;

@Getter
public class ThymeleafLink {

    @Getter
    public final String hrefVista;
    @Getter
    public final String hrefAplicativo;
    @Getter
    public final String humanName;
    private final String base;
    private final String baseAplicativo;
    private final String urlName;

    /**
     * Representación de las rutas para HTMX cuando haga los push-url que diferencie de donde pedir el contenido
     * y a donde redirigir la vista.
     * @param base URL base (el controlador que lo maneja)
     * @param humanName Nombre humano de la vista (lo que sale en la navbar)
     * @param urlName Nombre de la URL (lo que se pone en la URL)
     */
    public ThymeleafLink(String base, String humanName, String urlName) {
        this.base = base;
        this.baseAplicativo = base + "/aplicativo";
        this.humanName = humanName;
        this.urlName = urlName;
        this.hrefVista = base + "/" + urlName;
        this.hrefAplicativo = baseAplicativo + "/" + urlName;
    }

}