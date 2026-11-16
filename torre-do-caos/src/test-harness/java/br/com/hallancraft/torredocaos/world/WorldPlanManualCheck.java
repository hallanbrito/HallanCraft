package br.com.hallancraft.torredocaos.world;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WorldPlanManualCheck {
    static int count;
    static void check(boolean ok, String label) {
        if (!ok) throw new AssertionError(label);
        System.out.println("PASS " + (++count) + "/8 " + label);
    }
    public static void main(String[] args) {
        check(TowerTopology.slots().size() == 12, "12 slots across 3 dimensions");
        check(TowerTopology.slots().stream().map(s -> s.realm()).distinct().count() == 3, "all three realms");
        check(TowerTopology.slots().stream().map(s -> s.id()).distinct().count() == 12, "unique blueprint IDs");
        boolean disjoint = true;
        for (var a : TowerTopology.slots()) for (var b : TowerTopology.slots()) {
            if (a == b || a.realm() != b.realm()) continue;
            disjoint &= a.maxX() < b.minX() || b.maxX() < a.minX() || a.maxZ() < b.minZ() || b.maxZ() < a.minZ();
        }
        check(disjoint, "no overlap within each world");
        TowerAllocator al = new TowerAllocator();
        UUID p = UUID.randomUUID();
        var first = al.reserve(p).orElseThrow();
        check(al.reserve(p).orElseThrow().equals(first) && al.occupied() == 1, "idempotent join");
        Set<String> seen = new HashSet<>(); seen.add(first.id());
        for (int i = 0; i < 11; i++) seen.add(al.reserve(UUID.randomUUID()).orElseThrow().id());
        check(seen.size() == 12 && al.occupied() == 12, "12 players always have unique tower");
        check(al.reserve(UUID.randomUUID()).isEmpty(), "fail closed when full");
        al.release(p);
        check(al.reserve(UUID.randomUUID()).orElseThrow().equals(first), "released slot can be reused");
        System.out.println("OFFLINE_WORLD_PLAN: 8/8 PASS");
    }
}
