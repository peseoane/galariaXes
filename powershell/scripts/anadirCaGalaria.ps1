$certPath = "C:\Galaria\certs\CA-GALARIA.crt"
$storeScope = "LocalMachine"
$storeName = "Root"
$cert = New-Object System.Security.Cryptography.X509Certificates.X509Certificate2
$cert.Import($certPath)
$store = New-Object System.Security.Cryptography.X509Certificates.X509Store($storeName, $storeScope)
$store.Open([System.Security.Cryptography.X509Certificates.OpenFlags]::ReadWrite)
$store.Add($cert)
$store.Close()