#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
mkdir -p build/offline-domain
javac -d build/offline-domain \
  src/main/java/br/com/hallancraft/torredocaos/domain/RunState.java \
  src/main/java/br/com/hallancraft/torredocaos/domain/RunRules.java \
  src/test-harness/java/br/com/hallancraft/torredocaos/domain/RunRulesManualCheck.java
java -cp build/offline-domain br.com.hallancraft.torredocaos.domain.RunRulesManualCheck
