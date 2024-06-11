Import-Module "C:/Galaria/Scripts/Get-Monitor.psm1"
$xmlDocument = New-Object System.Xml.XmlDocument
$ordenador = $xmlDocument.CreateElement("ordenador")
$xmlDocument.AppendChild($ordenador)
$computerSystem = Get-CimInstance -ClassName Win32_ComputerSystem
$processor = Get-CimInstance -ClassName Win32_Processor
$bios = Get-CimInstance -ClassName Win32_BIOS
$os = Get-CimInstance -ClassName Win32_OperatingSystem
$gpuInfo = Get-CimInstance -ClassName Win32_VideoController
$ordenador.SetAttribute("generado",(Get-Date).ToString("yyyy-MM-dd HH:mm:ss"))
$ordenador.AppendChild($xmlDocument.CreateElement("etiqueta")).InnerText = $computerSystem.Name
$serialNumber = $bios.SerialNumber
$uuid = (Get-WmiObject Win32_ComputerSystemProduct).UUID

$rutaCarpeta = "C:/Galaria/"
$rutaDatosXml = "C:/Galaria/datos.xml"

if (-not (Test-Path $rutaDatosXml))
{
    $metadatosXmlPc = New-Object System.Xml.XmlDocument
    $metadatosXmlPc.AppendChild($metadatosXmlPc.CreateXmlDeclaration("1.0", "UTF-8", $null)) | Out-Null
    $datosNode = $metadatosXmlPc.CreateElement("datos")
    $archivosXML = Get-ChildItem -Path $rutaCarpeta -Filter *.xml | Sort-Object LastWriteTime -Descending
    if ($archivosXML.Count -gt 0)
    {
        $archivoXMLMasReciente = $archivosXML[0]
        $xmlMasReciente = New-Object System.Xml.XmlDocument
        $xmlMasReciente.Load($archivoXMLMasReciente.FullName)
        $nombre = $xmlMasReciente.SelectSingleNode("//Nombre").InnerText
        $bocaRed = $xmlMasReciente.SelectSingleNode("//Boca_Red").InnerText
        $nombreNode = $metadatosXmlPc.CreateElement("Nombre")
        $nombreNode.InnerText = $nombre
        $datosNode.AppendChild($nombreNode) | Out-Null
        if (-not [string]::IsNullOrEmpty($bocaRed) -and $bocaRed -ne "Sin asignar")
        {
            $bocaRedNode = $metadatosXmlPc.CreateElement("Boca_Red")
            $bocaRedNode.InnerText = $bocaRed
            $datosNode.AppendChild($bocaRedNode) | Out-Null
        }
    }
    else
    {
        $nombreNode = $metadatosXmlPc.CreateElement("etiqueta")
        $datosNode.AppendChild($nombreNode) | Out-Null
        $bocaRedNode = $metadatosXmlPc.CreateElement("Boca_Red")
        $datosNode.AppendChild($bocaRedNode) | Out-Null
    }
    $metadatosXmlPc.AppendChild($datosNode) | Out-Null
    $metadatosXmlPc.Save($rutaDatosXml)
}

if ([string]::IsNullOrWhiteSpace($serialNumber) -or $serialNumber -eq "System Serial Number")
{
    $serialNumber = (Get-WmiObject Win32_ComputerSystemProduct).UUID
}

$ordenador.AppendChild($xmlDocument.CreateElement("numero_serie")).InnerText = $serialNumber

$ordenador.AppendChild($xmlDocument.CreateElement("fabricante")).InnerText = $computerSystem.Manufacturer
$networkInfo = Get-WmiObject Win32_NetworkAdapterConfiguration |
        Where-Object { $_.IPAddress -ne $null }
$ipsNode = $xmlDocument.CreateElement("network")
foreach ($info in $networkInfo)
{
    $ip = $info.IPAddress[0]
    $mac = $info.MACAddress
    $gateway = $info.DefaultIPGateway[0]
    $dns = $info.DNSDomain
    $description = $info.Description
    $dhcpEnabled = $info.DHCPEnabled

    $ipNode = $xmlDocument.CreateElement("ip")
    $ipv4Node = $xmlDocument.CreateElement("ipv4")
    $ipv4Node.InnerText = $ip
    $macNode = $xmlDocument.CreateElement("mac")
    $macNode.InnerText = $mac
    $gatewayNode = $xmlDocument.CreateElement("gateway")
    $gatewayNode.InnerText = $gateway
    $dnsNode = $xmlDocument.CreateElement("dns")
    $dnsNode.InnerText = $dns
    $descriptionNode = $xmlDocument.CreateElement("description")
    $descriptionNode.InnerText = $description
    $dhcpEnabledNode = $xmlDocument.CreateElement("dhcp_enabled")
    $dhcpEnabledNode.InnerText = $dhcpEnabled

    $ipNode.AppendChild($ipv4Node) | Out-Null
    $ipNode.AppendChild($macNode) | Out-Null
    $ipNode.AppendChild($gatewayNode) | Out-Null
    $ipNode.AppendChild($dnsNode) | Out-Null
    $ipNode.AppendChild($descriptionNode) | Out-Null
    $ipNode.AppendChild($dhcpEnabledNode) | Out-Null

    $ipsNode.AppendChild($ipNode) | Out-Null
}
$ordenador.AppendChild($ipsNode) | Out-Null


$sistema_operativo = $ordenador.AppendChild($xmlDocument.CreateElement("sistema_operativo"))
$sistema_operativo.AppendChild($xmlDocument.CreateElement("nombre")).InnerText = $os.Caption
$sistema_operativo.AppendChild($xmlDocument.CreateElement("compilacion")).InnerText = $os.BuildNumber
$sistema_operativo.AppendChild($xmlDocument.CreateElement("version")).InnerText = $os.Version
$sistema_operativo.AppendChild($xmlDocument.CreateElement("codename")).InnerText = (Get-ComputerInfo).OSDisplayVersion
$modelo_pc = $ordenador.AppendChild($xmlDocument.CreateElement("modelo_pc"))
$modelo = if ($computerSystem.Model -ne "System Product Name" -and $computerSystem.Model)
{
    $computerSystem.Model
}
else
{
    "generico"
}
$strName = $env:username
$strFilter = "(&(objectCategory=User)(samAccountName=$strName))"

$objSearcher = New-Object System.DirectoryServices.DirectorySearcher
$objSearcher.Filter = $strFilter

$modelo_pc.AppendChild($xmlDocument.CreateElement("modelo")).InnerText = $modelo

# Obtener información de la CPU
$processors = Get-CimInstance -ClassName Win32_Processor

# Crear el nodo para los procesadores
$cpusNode = $xmlDocument.CreateElement("cpu_socket")
$ordenador.AppendChild($cpusNode) | Out-Null

# Iterar sobre cada procesador y crear los nodos XML correspondientes
foreach ($processor in $processors)
{
    $cpuNode = $xmlDocument.CreateElement("cpu")

    $referenciaNode = $xmlDocument.CreateElement("referencia")
    $referenciaNode.InnerText = $processor.Name
    $cpuNode.AppendChild($referenciaNode) | Out-Null

    $fabricanteNode = $xmlDocument.CreateElement("fabricante")
    $fabricanteNode.InnerText = $processor.Manufacturer
    $cpuNode.AppendChild($fabricanteNode) | Out-Null

    $velocidadNode = $xmlDocument.CreateElement("velocidad")
    $velocidadNode.InnerText = $processor.MaxClockSpeed
    $cpuNode.AppendChild($velocidadNode) | Out-Null

    $nucleosNode = $xmlDocument.CreateElement("nucleos")
    $nucleosNode.InnerText = $processor.NumberOfCores
    $cpuNode.AppendChild($nucleosNode) | Out-Null

    $hilosNode = $xmlDocument.CreateElement("hilos")
    $hilosNode.InnerText = $processor.NumberOfLogicalProcessors
    $cpuNode.AppendChild($hilosNode) | Out-Null

    $cpusNode.AppendChild($cpuNode) | Out-Null
}

$gpusElement = $ordenador.AppendChild($xmlDocument.CreateElement("gpu_adapter"))

foreach ($gpu in $gpuInfo)
{
    $gpuElement = $gpusElement.AppendChild($xmlDocument.CreateElement("gpu"))
    $gpuElement.AppendChild($xmlDocument.CreateElement("referencia")).InnerText = $gpu.Name
    $gpuElement.AppendChild($xmlDocument.CreateElement("fabricante")).InnerText = $gpu.AdapterCompatibility
}

$ordenador.AppendChild($xmlDocument.CreateElement("ram")).InnerText = $computerSystem.TotalPhysicalMemory

$monitores = $xmlDocument.CreateElement("monitores")
$ordenador.AppendChild($monitores)
$Monitors = Get-Monitor
ForEach ($Monitor in $Monitors)
{
    $monitorElement = $xmlDocument.CreateElement("monitor")
    $monitorElement.AppendChild($xmlDocument.CreateElement("fabricante")).InnerText = $Monitor.Manufacturer
    $monitorElement.AppendChild($xmlDocument.CreateElement("referencia")).InnerText = $Monitor.Model
    $monitorElement.AppendChild($xmlDocument.CreateElement("numero_serie")).InnerText = $Monitor.SerialNumber
    $monitores.AppendChild($monitorElement)
}

$settings = New-Object System.Xml.XmlWriterSettings
$settings.Indent = $true
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$path = "C:/Galaria/XML/" + $computerSystem.Name + "_" + $timestamp + ".xml"
$writer = [System.Xml.XmlWriter]::Create($path, $settings)
$xmlDocument.WriteTo($writer)
$writer.Flush()
$writer.Close()