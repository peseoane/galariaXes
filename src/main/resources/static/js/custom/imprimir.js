function imprimir(id) {
    console.log("Impresión de " + id + " solicitada");
    let currentTheme = document.body.getAttribute("data-bs-theme");
    document.body.setAttribute("data-bs-theme", "light");
    let accordions = document.getElementsByClassName("accordion-collapse");
    for (let i = 0; i < accordions.length; i++) {
        accordions[i].classList.add("show");
    }
    let printContents = document.getElementById(id).cloneNode(true);

    // remove all table-hover classes
    let tables = printContents.getElementsByTagName("table");
    for (let i = 0; i < tables.length; i++) {
        tables[i].classList.remove("table-hover");
    }

    let infoElement = document.createElement("div");
    infoElement.classList.add("d-flex", "justify-content-between", "font-monospace", "small");
    infoElement.innerHTML = `
        <p class="my-auto" >Fecha: ${new Date().toLocaleString()}</p>
`;
    printContents.appendChild(infoElement);

    let img = document.createElement("img");
    let srcData = "/svg/galariaPositivo.svg";
    img.setAttribute("src", srcData);
    img.setAttribute("width", "15%");
    img.setAttribute("alt", "logo");
    img.style.position = "absolute";
    img.style.bottom = "0";
    img.style.left = "50%";
    img.style.transform = "translateX(-50%), translateY(-10%)";
    printContents.appendChild(img);

    let originalContents = document.body.innerHTML;
    document.body.innerHTML = printContents.innerHTML;

    img.onload = function () {
        window.print();
        document.body.innerHTML = originalContents;
        for (let i = 0; i < accordions.length; i++) {
            accordions[i].classList.remove("show");
        }
        document.body.setAttribute("data-bs-theme", currentTheme);
    };
}