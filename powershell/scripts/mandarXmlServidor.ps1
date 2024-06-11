$headers = New-Object "System.Collections.Generic.Dictionary[[String],[String]]"
$headers.Add("Content-Type", "application/xml")

$rutaCarpeta = "C:/Galaria/XML/"

# Obtener el archivo XML más reciente
$archivosXML = Get-ChildItem -Path $rutaCarpeta -Filter *.xml | Sort-Object LastWriteTime -Descending
if ($archivosXML.Count -gt 0)
{
    $archivoXMLMasReciente = $archivosXML[0].FullName

    try
    {
        $headers = New-Object "System.Collections.Generic.Dictionary[[String],[String]]"
        $headers.Add("Content-Type", "application/xml")

        $body = Get-Content -Path $archivoXMLMasReciente -Raw

        $response = Invoke-RestMethod 'https://DIRECCIONSERVIDOR:9000/panel/aplicativo/xml/actualizarInformacion' -Method 'POST' -Headers $headers -Body $body
        $response | ConvertTo-Json

    }
    catch
    {
        Write-Error "Error al enviar el archivo XML al servidor. Error: $( $_.Exception.Message )"
    }

}