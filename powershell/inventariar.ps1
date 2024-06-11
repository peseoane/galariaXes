<# Importar módulo de Active Directory #>
Import-Module ActiveDirectory

<# RUTAS DE ARCHIVOS #>
$LOCAL_BASE_APP = ".\"
$LOCAL_GALARIA = "C:\Galaria\"
$SUFFIX_SCRIPTS = "scripts\"
$SUFFIX_XML = "XML\"

$LOCAL_SCRIPTS = $LOCAL_BASE_APP + $SUFFIX_SCRIPTS
$LOCAL_XML = $LOCAL_GALARIA + $SUFFIX_XML

$REMOTE_BASE = "C:\Galaria\"
$REMOTE_SCRIPTS = $REMOTE_BASE + $SUFFIX_SCRIPTS
$REMOTE_XML = $REMOTE_BASE + $SUFFIX_XML

$VAULT = "\\DIRECCION_REMOTA"
$SCRIPT_IDENTIFICAR_MONITOR = "Get-Monitor.psm1"
$SCRIPT_GENERAR_XML = "generarReporteXml.ps1"
$SCRIPT_SERVIDOR_XML = "mandarXmlServidor.ps1"
$SCRIPT_ANADIR_CA = "anadirCaGalaria.ps1"

$SUFFIX_CERTS = "certs\"
$CA_GALARIA = "CA-GALARIA.crt"
$CERTS = $LOCAL_GALARIA + $SUFFIX_CERTS
$CA_GALARIA_PATH = $CERTS + $CA_GALARIA

<# CREDENCIALES

--- AQUÍ DEBERÁS ESCOGER UN MÉTODO DE AUTENTICACIÓN ---

#>
$USERNAME = "dominio\usuario"
$PASSWORD = ConvertTo-SecureString "contrasinal" -AsPlainText -Force

$CRED = New-Object System.Management.Automation.PSCredential($USERNAME, $PASSWORD)

function copiarCertificado
{
    param([System.Management.Automation.Runspaces.PSSession]$session)

    Write-Host "[$computer] Copiando certificado CA-GALARIA..."

    try
    {
        Copy-Item -Path $CA_GALARIA_PATH -Destination $CA_GALARIA_PATH -ToSession $session -ErrorAction Stop
        Write-Host "[$computer] Copiado certificado CA-GALARIA"
    }
    catch
    {
        Write-Error "[$computer] No se ha podido copiar el certificado CA-GALARIA"
    }
}

<# Función para iniciar conexión remota #>
function iniciarConexion
{
    param([string]$Computer)

    Write-Host "[$Computer] Intentando conectar mediante PS-SSH"

    try
    {
        $session = New-PSSession -ComputerName $Computer -Credential $CRED -ErrorAction Stop

        if ($session)
        {
            Write-Host "[$Computer] Se ha establecido la conexión PS-SSH"
            return $session
        }
    }
    catch
    {
        Write-Error "[$Computer] No se ha podido establecer la conexión PS-SSH"
    }
}

<# Función para desconectar sesión remota #>
function cerrarConexion
{
    param([System.Management.Automation.Runspaces.PSSession]$session)

    try
    {
        Write-Host "[$computer] Cerrando sesión..."
        Remove-PSSession $session
    }
    catch
    {
        Write-Error "No se ha podido cerrar la sesión de $( $computer )"
    }
}

<# Función para copiar los scripts #>
function copiarScripts
{
    param([System.Management.Automation.Runspaces.PSSession]$session)

    Write-Host "[$computer] Copiando scripts..."

    $scripts = @($SCRIPT_IDENTIFICAR_MONITOR, $SCRIPT_GENERAR_XML, $SCRIPT_SERVIDOR_XML, $SCRIPT_ANADIR_CA)  # Añadir los nombres de los scripts aquí

    foreach ($script in $scripts)
    {
        $local = $LOCAL_SCRIPTS + $script
        $remote = $REMOTE_SCRIPTS + $script

        # Obtener el hash del archivo local
        $localHash = (Get-FileHash -Path $local).Hash

        # Obtener el hash del archivo remoto
        $remoteHash = Get-RemoteFileHash -session $session -filePath $remote

        if (-not $remoteHash -or $localHash -ne $remoteHash)
        {
            try
            {
                Write-Host "[$computer] Intentando copiar $script"
                Copy-Item -Path $local -Destination $remote -ToSession $session -ErrorAction Stop
                Write-Host "[$computer] Copiado $script"
            }
            catch
            {
                Write-Error "[$computer] No se ha podido copiar $script"
            }
        }
        else
        {
            Write-Host "[$computer] El archivo $script no ha cambiado y no necesita ser copiado."
        }
    }
}

<# Función para generar las carpetas necesarias #>
function generarCarpetasRemotas
{
    param([System.Management.Automation.Runspaces.PSSession]$session)

    Write-Host "[$computer] Creando carpetas..."

    try
    {
        Invoke-Command -Session $session -ScriptBlock {
            param ($REMOTE_BASE, $REMOTE_SCRIPTS, $REMOTE_XML, $CERTS)

            if (-not (Test-Path $REMOTE_BASE))
            {
                New-Item -Path $REMOTE_BASE -ItemType Directory
            }

            if (-not (Test-Path $REMOTE_SCRIPTS))
            {
                New-Item -Path $REMOTE_SCRIPTS -ItemType Directory
            }

            if (-not (Test-Path $REMOTE_XML))
            {
                New-Item -Path $REMOTE_XML -ItemType Directory
            }

            if (-not (Test-Path $CERTS))
            {
                New-Item -Path $CERTS -ItemType Directory
            }

        } -ArgumentList $REMOTE_BASE, $REMOTE_SCRIPTS, $REMOTE_XML, $CERTS
    }

    catch
    {
        Write-Error "[$computer] No se han podido crear las carpetas remotas"
    }
}

function generarCarpetasLocales
{
    Write-Host "Creando carpetas locales..."

    if (-not (Test-Path $LOCAL_BASE_APP))
    {
        New-Item -Path $LOCAL_BASE_APP -ItemType Directory
    }

    if (-not (Test-Path $LOCAL_GALARIA))
    {
        New-Item -Path $LOCAL_GALARIA -ItemType Directory
    }

    if (-not (Test-Path $LOCAL_SCRIPTS))
    {
        New-Item -Path $LOCAL_SCRIPTS -ItemType Directory
    }

    if (-not (Test-Path $LOCAL_XML))
    {
        New-Item -Path $LOCAL_XML -ItemType Directory
    }
}



<# Función para ejecutar un script #>
function ejecutarScript
{
    param([System.Management.Automation.Runspaces.PSSession]$session, [string]$script)

    Write-Host "[$computer] Ejecutando $script"

    try
    {
        Invoke-Command -Session $session -ScriptBlock {
            param ($REMOTE_SCRIPTS, $script)
            Set-Location $REMOTE_SCRIPTS
            & $script
        } -ArgumentList $REMOTE_SCRIPTS, $script
    }
    catch
    {
        Write-Error "[$computer] No se ha podido ejecutar $script"
    }
}

<# Función para obtener los ordenadores del Directorio Activo #>
function obtenerOrdenadoresDirectorioActivo
{
    param([string]$Query)

    try
    {
        if ($Query)
        {
            Write-Host "Buscando $Query"
            $filter = "Name -like '*$Query*'"
            return Get-ADComputer -Filter $filter -SearchBase "OU=tuOuAd,dc=TuDominiolocal" -Properties name, operatingsystem, operatingsystemversion, IPv4Address | Select-Object -ExpandProperty name
        }
        else
        {
            Write-Host "Buscando todos los ordenadores"
            return Get-ADComputer -Filter * -SearchBase "OU=tuOuAd,dc=TuDominiolocal" -Properties name, operatingsystem, operatingsystemversion, IPv4Address | Select-Object -ExpandProperty name
        }
    }
    catch
    {
        Write-Error "Imposible acceder al Directorio activo"
    }
}

function Get-RemoteFileHash
{
    param ([System.Management.Automation.Runspaces.PSSession]$session, [string]$filePath)

    try
    {
        $hash = Invoke-Command -Session $session -ScriptBlock {
            param ($filePath)
            if (Test-Path $filePath)
            {
                return (Get-FileHash -Path $filePath).Hash
            }
            else
            {
                return $null
            }
        } -ArgumentList $filePath -ErrorAction Stop

        return $hash
    }
    catch
    {
        Write-Error "Error obteniendo el hash del archivo remoto: $filePath"
        return $null
    }
}

<# Copiar XMLs de remoto a VAULT #>
function copiarXmls
{
    param([System.Management.Automation.Runspaces.PSSession]$session)

    Write-Host "[$( $session.ComputerName )] Copiando XMLs a LOCAL..."

    $files = Invoke-Command -Session $session -ScriptBlock {
        param($REMOTE_XML)
        Get-ChildItem -Path $REMOTE_XML -Filter *.xml | Sort-Object LastWriteTime -Descending
    } -ArgumentList $REMOTE_XML

    if ($files -and $files.Count -gt 0)
    {
        $latestFile = $files[0]

        try
        {
            Write-Host "[$( $session.ComputerName )] Intentando copiar $( $latestFile.Name ) a la carpeta temporal local"
            $destinationPath = Join-Path $LOCAL_XML $latestFile.Name

            # Asegurarse de que el directorio de destino existe
            if (-not (Test-Path $LOCAL_XML))
            {
                New-Item -Path $LOCAL_XML -ItemType Directory -Force
            }

            Copy-Item -Path $latestFile.FullName -Destination $destinationPath -FromSession $session -ErrorAction Stop
            Write-Host "[$( $session.ComputerName )] Copiado $( $latestFile.Name ) a la carpeta temporal local"
        }
        catch
        {
            Write-Error "[$( $session.ComputerName )] No se ha podido copiar $( $latestFile.Name ) a la carpeta temporal local + $_"
        }
    }
    else
    {
        Write-Host "[$( $session.ComputerName )] No se encontraron archivos XML en el directorio remoto"
    }
}

function procesarEquipo
{
    param([string]$computer, [System.Management.Automation.Runspaces.PSSession]$session)

    try
    {
        Write-Information "[$computer] Procesando equipo..."
        generarCarpetasRemotas -Session $session
        Write-Information "[$computer] Copiando scripts..."
        copiarScripts -Session $session
        Write-Information "[$computer] Copiando certificado..."
        copiarCertificado -Session $session
        Write-Information "[$computer] Ejecutando script..."
        ejecutarScript -Session $session -Script ($REMOTE_SCRIPTS + $SCRIPT_GENERAR_XML)
        Write-Information "[$computer] Copiando XMLs a local..."
        copiarXmls -Session $session
        Write-Information "[$computer] Instalando certificado..."
        ejecutarScript -Session $session -Script ($REMOTE_SCRIPTS + $SCRIPT_ANADIR_CA)
        Write-Information "[$computer] Enviando XML al servidor..."
        ejecutarScript -Session $session -Script ($REMOTE_SCRIPTS + $SCRIPT_SERVIDOR_XML)
    }

    catch
    {
        Write-Error "[$computer] Error al procesar el equipo: $_"
    }
}
function AgregarTareaProgramada
{
    param(
        [System.Management.Automation.Runspaces.PSSession]$session,
        [string]$NombreTarea,
        [string]$RutaScriptGenerar,
        [string]$RutaScriptMandar
    )

    try
    {
        # Argumento para ejecutar ambos scripts en secuencia
        $argument = "-ExecutionPolicy Bypass -Command `"& '$RutaScriptGenerar'; & '$RutaScriptMandar'`""
        $accion = New-ScheduledTaskAction -Execute 'PowerShell.exe' -Argument $argument

        # Trigger para iniciar la tarea al encender el equipo
        $triggerInicio = New-ScheduledTaskTrigger -AtStartup

        # Crear la tarea programada en el equipo remoto
        Invoke-Command -Session $session -ScriptBlock {
            param ($NombreTarea, $accion, $triggerInicio)
            $Principal = New-ScheduledTaskPrincipal -UserId "SYSTEM" -LogonType ServiceAccount -RunLevel Highest
            Register-ScheduledTask -TaskName $NombreTarea -Action $accion -Trigger $triggerInicio -Principal $Principal -Description "INVENTARIO GALARIA -> Ejecuta los scripts de inventario al iniciar el equipo" -Force
        } -ArgumentList $NombreTarea, $accion, $triggerInicio

        Write-Host "[$($session.ComputerName)] Tarea programada '$NombreTarea' creada exitosamente."
    }
    catch
    {
        Write-Error "[$($session.ComputerName)] Error al crear la tarea programada -> $_"
    }
}

function eliminarTareaProgramada
{
    param(
        [System.Management.Automation.Runspaces.PSSession]$session,
        [string]$NombreTarea
    )

    try
    {
        Invoke-Command -Session $session -ScriptBlock {
            param ($NombreTarea)
            Unregister-ScheduledTask -TaskName $NombreTarea -Confirm:$false
        } -ArgumentList $NombreTarea

        Write-Host "[$($session.ComputerName)] Tarea programada '$NombreTarea' eliminada exitosamente."
    }
    catch
    {
        Write-Error "[$($session.ComputerName)] Error al eliminar la tarea programada -> $_"
    }
}

function main
{
    Write-Host """
ÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕ       ÕÕ
ÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕ       ÕÕ
ÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕ       ÕÕ        ÕÕÕÕÕÕÕ   ÕÕ    Õ       ÕÕ   ÕÕÕÕÕÕ Õ    ÕÕ
ÕÕÕÕÕÕÕÕÕÕ ÕÕÕÕÕÕÕ ÕÕÕÕÕÕÕÕ       ÕÕ        ÕÕ       ÕÕÕÕ   Õ      ÕÕÕÕ  ÕÕ  ÕÕ Õ   ÕÕÕÕ
ÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕ       ÕÕ        ÕÕ ÕÕÕÕ ÕÕÕÕÕÕ  Õ     ÕÕÕÕÕÕ ÕÕÕÕ   Õ  ÕÕÕÕÕÕ
ÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕ       ÕÕ        ÕÕÕÕÕÕÕ ÕÕ   ÕÕ ÕÕÕÕÕÕÕ   ÕÕ ÕÕ ÕÕÕ Õ ÕÕ   ÕÕ
ÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕ       ÕÕ                                                                     ÕÕ
ÕÕÕÕÕ ÕÕÕÕÕÕÕ ÕÕÕÕÕÕÕ ÕÕÕÕÕ       ÕÕ                                                                    Õ
ÕÕÕÕÕÕÕÕÕÕÕÕÕ ÕÕÕÕÕÕÕÕÕÕÕÕÕ       ÕÕ        ÕÕÕÕÕ ÕÕ    ÕÕ  ÕÕÕÕÕ ÕÕÕÕÕ ÕÕÕÕÕÕÕÕÕÕÕÕ   ÕÕ      ÕÕÕÕÕÕÕÕ   ÕÕ ÕÕÕÕÕÕ ÕÕ    ÕÕ ÕÕÕÕÕÕÕ  ÕÕÕ      ÕÕÕÕÕÕ  ÕÕÕÕÕ
ÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕ       ÕÕ        ÕÕÕÕ  ÕÕÕ  ÕÕÕ  Õ  ÕÕ ÕÕ ÕÕ ÕÕÕÕÕ  ÕÕÕ    ÕÕÕÕ     ÕÕ  ÕÕÕÕ   ÕÕ ÕÕÕÕÕÕ ÕÕ    ÕÕ ÕÕ      ÕÕ ÕÕ     ÕÕ   ÕÕ ÕÕÕÕ
ÕÕÕÕÕÕÕÕÕÕÕ     ÕÕÕÕÕÕÕÕÕÕÕ       ÕÕ        ÕÕ    ÕÕÕÕÕÕÕÕ  ÕÕÕÕ  ÕÕÕÕ  ÕÕ       ÕÕÕ ÕÕÕÕÕÕ    ÕÕÕÕ  ÕÕ   ÕÕ ÕÕ  ÕÕ ÕÕ    ÕÕ ÕÕ      ÕÕÕÕÕ     ÕÕ   ÕÕ Õ
ÕÕÕÕÕ  ÕÕÕÕÕ   ÕÕÕÕÕ  ÕÕÕÕÕ       ÕÕ        ÕÕÕÕÕ ÕÕ ÕÕ ÕÕ  Õ     ÕÕ ÕÕ ÕÕÕÕÕÕÕÕÕÕÕÕÕÕ    ÕÕ   ÕÕ     ÕÕÕÕÕÕ ÕÕÕÕÕÕ ÕÕÕÕÕ ÕÕ  ÕÕÕÕÕÕÕÕ   ÕÕ    ÕÕÕÕÕÕ  ÕÕÕÕÕ
ÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕ       ÕÕ
ÕÕÕÕÕÕÕÕÕÕ       ÕÕÕÕÕÕÕÕÕÕ       ÕÕ
ÕÕÕÕÕÕÕÕÕÕÕ    ÕÕÕÕÕÕÕÕÕÕÕÕ       ÕÕ        ÕÕÕÕÕ ÕÕÕÕÕ ÕÕÕÕÕÕÕÕ   ÕÕÕÕ ÕÕÕÕÕÕ ÕÕÕÕÕÕ ÕÕÕÕÕÕ   ÕÕÕÕÕ   ÕÕÕ   ÕÕ   Õ ÕÕ ÕÕÕÕÕÕÕ  ÕÕÕ  ÕÕÕÕÕÕ ÕÕ  ÕÕÕÕÕÕ ÕÕÕÕÕ
ÕÕÕÕÕ ÕÕÕÕÕÕÕ ÕÕÕÕÕÕÕ ÕÕÕÕÕ       ÕÕ        ÕÕÕ   ÕÕÕÕÕ ÕÕ ÕÕÕ ÕÕ ÕÕ ÕÕ    ÕÕ ÕÕ    ÕÕÕÕÕÕ     ÕÕÕ    ÕÕ ÕÕ  ÕÕÕÕ Õ ÕÕ    ÕÕ   ÕÕÕÕ  ÕÕ  ÕÕ ÕÕ ÕÕ   ÕÕ ÕÕÕ
ÕÕÕÕÕÕÕÕÕÕÕÕÕ ÕÕÕÕÕÕÕÕÕÕÕÕÕ       ÕÕ           ÕÕ ÕÕ    ÕÕÕÕ   ÕÕÕÕ  ÕÕ  ÕÕ   ÕÕ    ÕÕ   ÕÕÕ      ÕÕ  ÕÕÕÕÕ  ÕÕ ÕÕÕ ÕÕ    ÕÕ  ÕÕÕÕÕÕ ÕÕÕÕ   ÕÕ ÕÕ   ÕÕ    ÕÕ
ÕÕÕÕÕÕÕÕÕÕÕÕ   ÕÕÕÕÕÕÕÕÕÕÕÕ       ÕÕ        ÕÕÕÕÕ ÕÕÕÕÕ ÕÕ ÕÕÕ  ÕÕ   ÕÕ ÕÕÕÕÕÕ ÕÕÕÕÕÕ ÕÕÕÕÕ    ÕÕÕÕÕ ÕÕ   ÕÕ ÕÕ  ÕÕ ÕÕ    ÕÕ ÕÕ    ÕÕÕÕ  ÕÕ ÕÕ  ÕÕÕÕÕÕ ÕÕÕÕÕ
ÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕ       ÕÕ
ÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕÕ       ÕÕ
 """
    Write-Host "INVENTARIO - GALARIA XES"
    Write-Host "1. Inventariar equipo/s"
    Write-Host "2. Crear tarea programada"
    Write-Host "3. Eliminar tarea programada"

    $opcion = Read-Host -Prompt 'Selecciona una opción (1/2/3)'

    switch ($opcion)
    {
        1 {
            $hostQuery = Read-Host -Prompt 'Introduce un host o deja en blanco para buscar todos los ordenadores'
            $computers = obtenerOrdenadoresDirectorioActivo -Query $hostQuery

            if (-not $computers)
            {
                Write-Error "No se han encontrado ordenadores."
                return
            }

            generarCarpetasLocales

            $totalComputers = $computers.Count
            $completedCount = 0

            foreach ($computer in $computers)
            {
                $session = iniciarConexion -Computer $computer
                if ($session)
                {
                    procesarEquipo -Computer $computer -Session $session
                }

                $completedCount++
                $progressPercent = ($completedCount / $totalComputers) * 100
                Write-Progress -Activity "Procesando equipos" -Status "Completadas: $completedCount de $totalComputers" -PercentComplete $progressPercent
            }
        }
        2 {
            $hostQuery = Read-Host -Prompt 'Introduce un host o deja en blanco para buscar todos los ordenadores'
            $computers = obtenerOrdenadoresDirectorioActivo -Query $hostQuery

            if (-not $computers)
            {
                Write-Error "No se han encontrado ordenadores."
                return
            }

            foreach ($computer in $computers)
            {
                $session = iniciarConexion -Computer $computer
                if ($session)
                {
                    procesarEquipo -Computer $computer -Session $session
                    AgregarTareaProgramada -Session $session -NombreTarea "INVENTARIO_GALARIA" -RutaScriptGenerar "C:/Galaria/scripts/generarReporteXml.ps1" -RutaScriptMandar "C:/Galaria/scripts/mandarXmlServidor.ps1"
                }
            }
        }
        3 {
            $hostQuery = Read-Host -Prompt 'Introduce un host o deja en blanco para buscar todos los ordenadores'
            $computers = obtenerOrdenadoresDirectorioActivo -Query $hostQuery

            if (-not $computers)
            {
                Write-Error "No se han encontrado ordenadores."
                return
            }

            foreach ($computer in $computers)
            {
                $session = iniciarConexion -Computer $computer
                if ($session)
                {
                    eliminarTareaProgramada -Session $session -NombreTarea "INVENTARIO_GALARIA"
                }
            }
        }
        default {
            Write-Host "Opción no válida. Por favor, selecciona una opción válida."
        }
    }
}
main