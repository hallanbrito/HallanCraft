package br.com.hallancraft.torredocaos.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Preview-only slot ownership. A production run must persist its session before enabling waves. */
public final class TowerAllocator {
    private final Map<UUID, TowerTopology.Slot> byPlayer = new HashMap<>();
    private final Map<String, UUID> bySlot = new HashMap<>();

    public synchronized Optional<TowerTopology.Slot> reserve(UUID playerId) {
        TowerTopology.Slot existing = byPlayer.get(playerId);
        if (existing != null) return Optional.of(existing);
        for (TowerTopology.Slot candidate : TowerTopology.slots()) {
            if (!bySlot.containsKey(candidate.id())) {
                bySlot.put(candidate.id(), playerId);
                byPlayer.put(playerId, candidate);
                return Optional.of(candidate);
            }
        }
        return Optional.empty();
    }

    public synchronized Optional<TowerTopology.Slot> assigned(UUID playerId) {
        return Optional.ofNullable(byPlayer.get(playerId));
    }

    public synchronized void release(UUID playerId) {
        TowerTopology.Slot slot = byPlayer.remove(playerId);
        if (slot != null) bySlot.remove(slot.id(), playerId);
    }

    public synchronized int occupied() { return bySlot.size(); }
}
