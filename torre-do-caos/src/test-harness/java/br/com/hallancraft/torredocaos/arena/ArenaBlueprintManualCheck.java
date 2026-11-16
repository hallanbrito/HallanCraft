package br.com.hallancraft.torredocaos.arena;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/** Offline pure Java 21 checks, independent of Paper and Gradle. */
public final class ArenaBlueprintManualCheck {
    public static void main(String[] args) throws Exception {
        Path tmp = Files.createTempDirectory("tdc-f12");
        ArenaBlueprintStore store = new ArenaBlueprintStore(tmp);
        ArenaBlueprint bp = new ArenaBlueprint("arena_1", UUID.randomUUID(),
            new ArenaBounds(2,60,3,3,61,4),
            List.of("minecraft:air","minecraft:stone","minecraft:air","minecraft:bedrock",
                "minecraft:stone","minecraft:air","minecraft:air","minecraft:cobblestone"));
        int pass = 0;
        store.create(bp);
        pass += check(bp.equals(store.read("arena_1")), "round trip");
        try { store.create(bp); throw new AssertionError("overwrite"); }
        catch (FileAlreadyExistsException expected) { pass++; }
        pass += check(bp.bounds().contains(2,60,3), "contains");
        pass += check(!bp.bounds().contains(9,60,3), "outside");
        try { store.pathFor("../../escape"); throw new AssertionError("path traversal"); }
        catch (IllegalArgumentException expected) { pass++; }
        Path file = store.pathFor("arena_1");
        byte[] corrupted = Files.readAllBytes(file);
        corrupted[15] ^= 1;
        Files.write(file, corrupted);
        try { store.read("arena_1"); throw new AssertionError("tampered"); }
        catch (IOException expected) { pass++; }
        System.out.println("F1.2: " + pass + "/6 offline checks passed");
    }
    private static int check(boolean condition, String label) {
        if (!condition) throw new AssertionError(label);
        return 1;
    }
}
