#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
javac -encoding UTF-8 -d "$TMP" \
  src/main/java/br/com/hallancraft/torredocaos/arena/ArenaBounds.java \
  src/main/java/br/com/hallancraft/torredocaos/arena/ArenaBlueprint.java \
  src/main/java/br/com/hallancraft/torredocaos/arena/ArenaBlueprintStore.java \
  src/test-harness/java/br/com/hallancraft/torredocaos/arena/ArenaBlueprintManualCheck.java
java -cp "$TMP" br.com.hallancraft.torredocaos.arena.ArenaBlueprintManualCheck
