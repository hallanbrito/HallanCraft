package br.com.hallancraft.torredocaos.world;

import java.util.ArrayList;
import java.util.List;

/** Deterministic, non-overlapping slots across three dedicated void worlds. */
public final class TowerTopology {
    public static final int ARENAS_PER_REALM = 4;
    public static final int ARENA_RADIUS = 12;
    public static final int ARENA_FLOOR_Y = 78;
    public static final int ARENA_TOP_Y = 90;
    public static final int LOBBY_Y = 78;
    public static final int SPACING = 512;

    public enum Realm {
        OVERWORLD("torre_overworld", "tc-o"),
        NETHER("torre_nether", "tc-n"),
        END("torre_end", "tc-e");

        private final String worldName;
        private final String prefix;
        Realm(String worldName, String prefix) { this.worldName = worldName; this.prefix = prefix; }
        public String worldName() { return worldName; }
        public String prefix() { return prefix; }
    }

    public record Slot(String id, Realm realm, int centerX, int centerZ) {
        public int minX() { return centerX - ARENA_RADIUS; }
        public int maxX() { return centerX + ARENA_RADIUS; }
        public int minZ() { return centerZ - ARENA_RADIUS; }
        public int maxZ() { return centerZ + ARENA_RADIUS; }
        public boolean contains(int x, int y, int z) {
            return x >= minX() && x <= maxX()
                && z >= minZ() && z <= maxZ()
                && y >= ARENA_FLOOR_Y && y <= ARENA_TOP_Y + 3;
        }
    }

    private static final List<Slot> SLOTS = createSlots();
    private TowerTopology() { }
    public static List<Slot> slots() { return SLOTS; }

    private static List<Slot> createSlots() {
        List<Slot> slots = new ArrayList<>();
        for (Realm realm : Realm.values()) {
            for (int i = 0; i < ARENAS_PER_REALM; i++) {
                int row = i / 2;
                int column = i % 2;
                slots.add(new Slot(realm.prefix() + "-0" + (i + 1), realm,
                    SPACING * (column + 1), SPACING * (row + 1)));
            }
        }
        return List.copyOf(slots);
    }
}
