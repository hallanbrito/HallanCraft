# Windows PowerShell — requires Gradle 8.14.3 installed.
$ErrorActionPreference = 'Stop'
Set-Location (Split-Path $PSScriptRoot -Parent)
if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
  throw 'Java 21 necessario: java nao encontrado no PATH.'
}
if (-not (Get-Command gradle -ErrorAction SilentlyContinue)) {
  Write-Host 'Gradle nao instalado. Instale Gradle 8.14.3 e execute novamente.'
  Write-Host 'https://gradle.org/install/'
  exit 1
}
gradle wrapper --gradle-version 8.14.3 --distribution-type bin
if ($LASTEXITCODE -ne 0) { throw 'Falha ao gerar Gradle Wrapper.' }
Write-Host 'Wrapper gerado. Execute .\gradlew.bat test e .\gradlew.bat jar'
