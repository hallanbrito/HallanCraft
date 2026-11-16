#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
mkdir -p build/offline-worlds
javac -d build/offline-worlds \
  src/main/java/br/com/hallancraft/torredocaos/world/TowerTopology.java \
  src/main/java/br/com/hallancraft/torredocaos/world/TowerAllocator.java \
  src/test-harness/java/br/com/hallancraft/torredocaos/world/WorldPlanManualCheck.java
java -cp build/offline-worlds br.com.hallancraft.torredocaos.world.WorldPlanManualCheck
