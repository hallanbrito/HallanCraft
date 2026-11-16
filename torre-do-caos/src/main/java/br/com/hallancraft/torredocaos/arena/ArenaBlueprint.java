package br.com.hallancraft.torredocaos.arena;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** Immutable block-only baseline, excluding tile entity contents. */
public record ArenaBlueprint(String arenaId, UUID worldId, ArenaBounds bounds, List<String> blockData) {
    public ArenaBlueprint {
        if (arenaId == null || !arenaId.matches("[a-z0-9][a-z0-9_-]{0,31}"))
            throw new IllegalArgumentException("Invalid arena ID");
        Objects.requireNonNull(worldId);
        Objects.requireNonNull(bounds);
        blockData = List.copyOf(blockData);
        if (blockData.size() != bounds.volume()) throw new IllegalArgumentException("Unexpected block count");
        for (String data : blockData)
            if (data == null || data.isBlank() || data.length() > 512)
                throw new IllegalArgumentException("Invalid block data");
    }
}
