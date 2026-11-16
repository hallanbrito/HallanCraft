package br.com.hallancraft.torredocaos.world;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TowerAllocatorTest {
    @Test void parallelPlayersNeverShareAnArena() {
        TowerAllocator manager = new TowerAllocator();
        Set<String> reservedIds = new HashSet<>();
        for (int i = 0; i < 12; i++) {
            var slot = manager.reserve(UUID.randomUUID()).orElseThrow();
            assertTrue(reservedIds.add(slot.id()));
        }
        assertEquals(12, manager.occupied());
        assertTrue(manager.reserve(UUID.randomUUID()).isEmpty());
    }

    @Test void repeatedRequestKeepsTheSameSlot() {
        TowerAllocator manager = new TowerAllocator();
        UUID id = UUID.randomUUID();
        assertEquals(manager.reserve(id), manager.reserve(id));
        assertEquals(1, manager.occupied());
    }

    @Test void releasedArenaBecomesAvailableAgain() {
        TowerAllocator manager = new TowerAllocator();
        UUID first = UUID.randomUUID();
        var initial = manager.reserve(first).orElseThrow();
        manager.release(first);
        assertTrue(manager.assigned(first).isEmpty());
        assertEquals(initial, manager.reserve(UUID.randomUUID()).orElseThrow());
    }

    @Test void cannotReleaseOtherPlayersSlot() {
        TowerAllocator manager = new TowerAllocator();
        UUID player = UUID.randomUUID();
        manager.reserve(player);
        manager.release(UUID.randomUUID());
        assertEquals(1, manager.occupied());
    }
}
