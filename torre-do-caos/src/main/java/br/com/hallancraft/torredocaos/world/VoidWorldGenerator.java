package br.com.hallancraft.torredocaos.world;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

/** No vanilla terrain, mobs, caves, vegetation or structures in managed worlds. */
public final class VoidWorldGenerator extends ChunkGenerator {
    @Override public boolean shouldGenerateNoise() { return false; }
    @Override public boolean shouldGenerateSurface() { return false; }
    @Override public boolean shouldGenerateBedrock() { return false; }
    @Override public boolean shouldGenerateCaves() { return false; }
    @Override public boolean shouldGenerateDecorations() { return false; }
    @Override public boolean shouldGenerateMobs() { return false; }
    @Override public boolean shouldGenerateStructures() { return false; }

    @Override public void generateNoise(@NotNull WorldInfo world, @NotNull Random random,
            int x, int z, @NotNull ChunkData data) {
        // Intentionally empty. Do not access world chunks from generator threads.
    }

    @Override public Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
        return new Location(world, 0.5, TowerTopology.LOBBY_Y + 2, 0.5);
    }
}
