$ErrorActionPreference = 'Stop'
Set-Location (Split-Path $PSScriptRoot -Parent)
New-Item -ItemType Directory -Path 'build/offline-domain' -Force | Out-Null
javac -d build/offline-domain `
  src/main/java/br/com/hallancraft/torredocaos/domain/RunState.java `
  src/main/java/br/com/hallancraft/torredocaos/domain/RunRules.java `
  src/test-harness/java/br/com/hallancraft/torredocaos/domain/RunRulesManualCheck.java
if ($LASTEXITCODE -ne 0) { throw 'Falha na compilacao do dominio Java' }
java -cp build/offline-domain br.com.hallancraft.torredocaos.domain.RunRulesManualCheck
if ($LASTEXITCODE -ne 0) { throw 'Falha nos testes de dominio Java' }
