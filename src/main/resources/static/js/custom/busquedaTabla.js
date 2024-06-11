/** Código para hacer el syntax highlighting de la búsqueda en la tabla
 *  los cuadros de búqueda están en esta vista, pero como cargamos mediante htmx una vista,
 *  que carga según los IDs cuando sea visible cada DIV su entidad, entonces procesamos aquí
 *  lo que se ha buscado y después de hacer el swap del elemento en su tabla, buscamos la coincidencia
 *  de texto en la tabla y resaltamos la fila que contiene el texto buscado.
 *
 *  EL ID DEBE SER "tablaConBusqueda" para que funcione el resaltado de la fila de la tabla.
 *
 */
document.body.addEventListener("htmx:afterSwap", function (event) {
    let busqueda = document.querySelector('input[id="busquedaTablas"]').value.toLowerCase().trim();

    function ejecutarBusqueda(id) {
        let table = document.querySelector(id).getElementsByTagName("*");
        for (let i = 0; i < table.length; i++) {
            if (table[i].textContent.toLowerCase().includes(busqueda || busquedaMonitor)) {
                if (table[i].tagName === "TD" && table[i].children.length === 0) {
                    table[i].classList.add("bg-info", "fw-bolder", "text-white");
                }
            }
        }
    }

    if (busqueda.length > 0) {
        let ordenadores = document.querySelector("#tablaConBusqueda").getElementsByTagName("*");
        ejecutarBusqueda("#tablaConBusqueda");
    }
});