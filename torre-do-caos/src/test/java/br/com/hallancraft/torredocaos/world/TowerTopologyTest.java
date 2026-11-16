package br.com.hallancraft.torredocaos.world;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TowerTopologyTest {
    @Test void exactlyFourIndependentTowersInEachDimension() {
        assertEquals(12, TowerTopology.slots().size());
        for (var realm : TowerTopology.Realm.values())
            assertEquals(4, TowerTopology.slots().stream().filter(s -> s.realm() == realm).count());
    }

    @Test void allArenaIdsAreUniqueAndBlueprintSafe() {
        Set<String> ids = new HashSet<>();
        for (var slot : TowerTopology.slots()) {
            assertTrue(ids.add(slot.id()));
            assertTrue(slot.id().matches("[a-z0-9][a-z0-9_-]{0,31}"));
        }
    }

    @Test void allSlotsHaveDistinctNonOverlappingRegions() {
        for (var a : TowerTopology.slots()) for (var b : TowerTopology.slots()) {
            if (a == b || a.realm() != b.realm()) continue;
            boolean disjoint = a.maxX() < b.minX() || b.maxX() < a.minX()
                || a.maxZ() < b.minZ() || b.maxZ() < a.minZ();
            assertTrue(disjoint, a.id() + " overlaps " + b.id());
        }
    }

    @Test void lobbyNeverTouchesTower() {
        for (var s : TowerTopology.slots())
            assertFalse(s.contains(0, TowerTopology.ARENA_FLOOR_Y + 2, 0));
    }

    @Test void snapshotVolumeFitsBlueprintLimit() {
        int size = (TowerTopology.ARENA_RADIUS * 2 + 1);
        assertTrue(size * size * (TowerTopology.ARENA_TOP_Y - TowerTopology.ARENA_FLOOR_Y + 1) <= 20_000);
    }
}
