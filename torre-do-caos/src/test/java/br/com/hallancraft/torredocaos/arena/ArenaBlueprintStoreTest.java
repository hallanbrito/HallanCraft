package br.com.hallancraft.torredocaos.arena;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ArenaBlueprintStoreTest {
    @TempDir Path temp;
    ArenaBlueprint sample() {
        return new ArenaBlueprint("arena-1",
            UUID.fromString("12345678-1234-1234-1234-123456789abc"),
            new ArenaBounds(-1,60,4,0,61,5),
            List.of("minecraft:stone","minecraft:air","minecraft:oak_stairs[facing=north]",
                "minecraft:bedrock","minecraft:cobblestone","minecraft:air",
                "minecraft:stone_bricks","minecraft:air"));
    }
    @Test void preservesBoundsWorldAndBlockData() throws IOException {
        ArenaBlueprintStore store = new ArenaBlueprintStore(temp);
        store.create(sample());
        assertEquals(sample(), store.read("arena-1"));
    }
    @Test void refusesOverwrite() throws IOException {
        ArenaBlueprintStore store = new ArenaBlueprintStore(temp);
        store.create(sample());
        assertThrows(IOException.class, () -> store.create(sample()));
        assertEquals(sample(), store.read("arena-1"));
    }
    @Test void detectsTampering() throws IOException {
        ArenaBlueprintStore store = new ArenaBlueprintStore(temp);
        Path path = store.create(sample());
        byte[] bytes = Files.readAllBytes(path);
        bytes[bytes.length / 2] ^= 0x01;
        Files.write(path, bytes);
        assertThrows(IOException.class, () -> store.read("arena-1"));
    }
    @Test void detectsTruncation() throws IOException {
        ArenaBlueprintStore store = new ArenaBlueprintStore(temp);
        Path path = store.create(sample());
        byte[] bytes = Files.readAllBytes(path);
        Files.write(path, Arrays.copyOf(bytes, bytes.length - 7));
        assertThrows(IOException.class, () -> store.read("arena-1"));
    }
    @Test void rejectsTraversalOversizedOrInvertedArena() {
        ArenaBlueprintStore store = new ArenaBlueprintStore(temp);
        assertThrows(IllegalArgumentException.class, () -> store.pathFor("../world"));
        assertThrows(IllegalArgumentException.class, () -> new ArenaBounds(0,0,0,100,100,100));
        assertThrows(IllegalArgumentException.class, () -> new ArenaBounds(2,0,0,1,1,1));
    }
    @Test void copiesAndProtectsInputBlockList() {
        List<String> blocks = new ArrayList<>(sample().blockData());
        ArenaBlueprint snapshot = new ArenaBlueprint("safe", UUID.randomUUID(), sample().bounds(), blocks);
        blocks.set(0, "minecraft:dirt");
        assertEquals("minecraft:stone", snapshot.blockData().get(0));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.blockData().set(0, "minecraft:lava"));
    }
}
