![Totem Galaria Xes](./src/main/resources/static/svg/totem.svg)

En base a las directivas de la AMTEGA (Axencia para a Modernización Tecnolóxica de Galicia) en la siguiente página: https://amtega.xunta.gal/es/node/1203

> El fomento del uso del software libre y de fuentes abiertas  en el sector empresarial, en la sociedad y en la Administración 
> supone una  apuesta estratégica del Gobierno gallego  en consonancia con el marco estratégico tecnológico de la Xunta de Galicia, 
> la Agenda Digital 2020 (ADG 2020), que recoge el software libre como uno de los principios para la gestión eficiente de las distintas iniciativas.

Esta pequeña aplicación, destinada en un inicio para la gestión interna del inventario informático de Galaria EPSA y seguimiento
de contratos públicos / menores, se libera bajo las licencias recomendades de la Unión Europea.

![EUPL License Logo](./src/main/resources/static/svg/euplBottom.svg)

La licencia puede leerse detalladamente en la copia redistribuída en este proyecto o bien en la página oficial de la Unión Europea.

[Licencia en PDF (Español)](https://joinup.ec.europa.eu/sites/default/files/custom-page/attachment/eupl_v1.2_es.pdf)

# GalariaXes

GalariaXes es un software que nos facilita una interacción mediante pequeños aplicativos e utilidades, usando WinRM en una
red de dominio (en el caso del proyecto se usó la del Servizo Galego de Saúde) para mantener inventariado en SQL de modo
automático dónde están los equipos, sus contratos públicos / menores, garantías, monitores asociados, configuraciones, puesto
del Directorio Activo etc...

Uno de los problemas de llevar el inventario de modo manual y más en un Hospital, es que la naturaleza de las incidencias
que debe atender el CAU, son casi siempre urgentes, a veces se hacen cambios de versiones, equipos, compras... rápidamente
con el fin de subsanar la incidencia lo antes posible, y el inventariado manual puede quedarse desfasado rápidamente.

De este modo, creamos un agente en cada uno de los equipos, que, mandará un reporte periódico a un aplicativo Springboot
el cual procesará en la base de datos todo lo necesario.

## Características

- Usamos la lógica del SERGAS, es decir, todo equipo su referencia es la ETIQUETA asignada, para facilitar la trazabilidad del equipo.
- El software es capaz de decodificar las pantallas asociadas al equipo de modo automático, si estas son movidas de puesto, se actualizarán
  de modo automático en el nuevo puesto.
- Para el CAU, ofrece un modo rápido de realizar utilidades de ping o nmap para atender las incidencias más típicas inter-centros y poder
  así dar paso a comunicaciones si es necesario.
- Permite un modo más rápido que el controlador WUSUS de consultar versiones desplegadas (podemos hacer una consulta en milisegundos empleando lenguaje natural).
- Consultamos las garantías y contratos sin tener que acceder a software específico de facturación.
- Exportación general de vistas en formato `CSV` o  `XML` para los departamentos que así necesiten la información.
- Creación de incidencias por etiquetas.
- Creación de campos dinámicos adicionales tales como podrían ser:
  - Configuraciones DICOM.
  - Restricciones de software.
  - Configuraciones de red atípicas (Equipos de resonancia móviles por ejemplo).
- Consultar parámetros de los equipos basados en Active Directory.
- Autenticarse en el aplicativo con las cuentas Active Directory.

Y todo esto de un modo relacional, y multiusuario.

## Especificaciones

### SQL

El proyecto está pensado para usarse con Microsoft SQL Server.

Sin embargo, Hibernate / JPA, permiten muchos dialectos, adaptando los scripts y entidades en poco tiempo puede adaptarse.

### Tecnología principal

Se ha empleado principalmente:

- Java 21 LTS (Eclipse Adoptium) como base, aunque Springboot 3 y Spring Security 6 funcionan también en Java 17 y así el fichero Maven `pom.xml` nos permitirá
  compilar en versiones 17 LTS.
- JPA / Hibernate.
- Spring Security 6.
- Spring LDAP Active Directory.
- Thymeleaf (es una aplicación 100% Server Side Rendering).

El frontend que hay está basado en:

- AJAX / HTMX (para interacciones dinámicas).
- Bootstrap CSS 5.3 adaptado al [DECRETO 112/2021, de 22 de julio, por el que se aprueba el uso de los elementos básicos de la identidad corporativa de la Xunta de Galicia.](https://www.xunta.gal/dog/Publicados/2021/20210802/AnuncioG0595-270721-0001_es.html)
- Fuentes de la Xunta de Galicia y esquema de colores corporativo.
- Se requiere NodeJS o BunJS para compilar Bootstrap (SASS)

### Toolchain

La aplicación está pensada para ser fácilmente replicada, si no deseas cambiar estilos:

```bash
git clone https://github.com/peseoane/galariaXes.git
cd galariaXes
npm i
npm prod
```

Esto ya dejará en las carpetas de `resources` de Java todo lo necesario, y compilado para que el aplicativo sea funcional.

Para la compilación, usamos Maven y con diferentes perfiles.

> La aplicación por motivos de seguridad, viene sin configurar URLs a servidores de base de datos ni Directorio Activo.
> Por lo que, deberás crear y personalizar tus perfiles según necesidad.

### Despliegue

La aplicación puede generarse tanto en un contenedor tomcat de Docker autocontenido como empaquetar un `war` que puedes
alojar en un Tomcat 17 o superior.

## Requisitos

Las herramientas RSAT de Active Directory son opcionales pero necesarias para cargar los scripts de WinRM en los equipos,
como alternativa podrías cargar mediante una Política de Grupo las tareas programadas que mandan al endpoint la información
de los equipos y cargar mediante `IEX` el Script desde el aplicativo.

## Capturas de pantalla

[![Demo GALARIAXES](./src/main/resources/static/svg/demo.svg)](./src/main/resources/static/video/demo.webm)


## Contacto

Informática Galaria
informaticagalaria@sergas.es