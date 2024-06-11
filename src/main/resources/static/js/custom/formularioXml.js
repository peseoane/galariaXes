(function () {
    let uploadDropZone = document.querySelector(".upload_dropZone");
    let uploadInput = document.querySelector("#upload_xml");
    let submitButton = document.querySelector("#submit_button");
    let clearButton = document.querySelector("#clear_button");
    let fileList = document.querySelector("#fileList");

    function handleFiles(files) {
        fileList.innerHTML = "";

        for (let file of files) {
            let reader = new FileReader();

            reader.onload = (e) => {
                let icon = document.createElement("i");
                icon.className = "bi bi-filetype-xml";
                let listItem = document.createElement("li");
                listItem.className = "list-group-item d-flex align-items-center";
                listItem.appendChild(icon);
                let textNode = document.createTextNode(" " + file.name);
                listItem.appendChild(textNode);
                fileList.appendChild(listItem);
            };

            reader.readAsDataURL(file);
        }
        submitButton.disabled = false;
    }

    function handleChange() {
        handleFiles(uploadInput.files);
    }

    function handleDragover(e) {
        e.preventDefault();
        uploadDropZone.classList.add("dragover");
    }

    function handleDragleave() {
        uploadDropZone.classList.remove("dragover");
    }

    function handleDrop(e) {
        e.preventDefault();
        uploadDropZone.classList.remove("dragover");
        let files = e.dataTransfer.files;
        uploadInput.files = files;
        handleFiles(files);
    }

    function handleClear() {
        fileList.innerHTML = "";
        uploadInput.value = "";
        submitButton.disabled = true;
    }

    uploadInput.addEventListener("change", handleChange);
    uploadDropZone.addEventListener("dragover", handleDragover);
    uploadDropZone.addEventListener("dragleave", handleDragleave);
    uploadDropZone.addEventListener("drop", handleDrop);
    clearButton.addEventListener("click", handleClear);

    // Función para hacer scroll hacia el último elemento agregado
    function scrollToBottom(elementId) {
        let element = document.getElementById(elementId);
        if (element) {
            let items = element.getElementsByClassName("ws-element");
            if (items.length > 0) {
                let lastItem = items[items.length - 1];
                lastItem.scrollIntoView();
            }
        }
    }

    // Crear un observador de mutaciones para la lista de archivos a procesar
    let xmlNotifications = document.getElementById("xmlNotifications");
    if (xmlNotifications) {
        let observer = new MutationObserver(function (mutations) {
            mutations.forEach(function (mutation) {
                if (mutation.type === "childList") {
                    scrollToBottom("xmlNotifications");
                }
            });
        });

        // Configurar y empezar a observar cambios en la lista de archivos a procesar
        let observerConfig = { childList: true };
        observer.observe(xmlNotifications, observerConfig);
    }
})();