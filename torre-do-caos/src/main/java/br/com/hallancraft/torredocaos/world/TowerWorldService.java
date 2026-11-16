package br.com.hallancraft.torredocaos.world;

import br.com.hallancraft.torredocaos.arena.ArenaBlueprint;
import br.com.hallancraft.torredocaos.arena.ArenaBlueprintStore;
import br.com.hallancraft.torredocaos.arena.ArenaBounds;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * F1.2.2: builds three isolated void worlds and tower shells in controlled batches.
 * Never deletes, replaces or adopts any unmanaged world. Not a combat engine.
 */
public final class TowerWorldService {
    private static final int SCHEMA_VERSION = 1;
    private static final int BLOCKS_PER_TICK = 384;
    private static final String MARKER_FILE = "managed-worlds.properties";
    private static final String STATUS = "status";
    private static final String INITIALIZING = "INITIALIZING";
    private static final String READY = "READY";

    private final JavaPlugin plugin;
    private final Path marker;
    private final Map<TowerTopology.Realm, World> worlds = new EnumMap<>(TowerTopology.Realm.class);
    private final TowerAllocator allocator = new TowerAllocator();
    private final ArenaBlueprintStore blueprints;
    private boolean lobbyReady;
    private boolean ready;

    public TowerWorldService(JavaPlugin plugin) {
        this.plugin = plugin;
        this.marker = plugin.getDataFolder().toPath().resolve(MARKER_FILE);
        this.blueprints = new ArenaBlueprintStore(plugin.getDataFolder().toPath().resolve("arena-blueprints"));
    }

    public boolean isReady() { return ready; }
    public boolean isLobbyReady() { return lobbyReady; }
    public TowerAllocator allocator() { return allocator; }
    public boolean manages(World world) {
        if (world == null) return false;
        return worlds.values().stream().anyMatch(w -> w.getUID().equals(world.getUID()));
    }
    public Location lobby() {
        World w = worlds.get(TowerTopology.Realm.OVERWORLD);
        if (!lobbyReady || w == null) throw new IllegalStateException("Lobby not ready");
        return new Location(w, 0.5, TowerTopology.LOBBY_Y + 2, 0.5);
    }
    public Location spawn(TowerTopology.Slot slot) {
        World w = worlds.get(slot.realm());
        return new Location(w, slot.centerX() + 0.5, TowerTopology.ARENA_FLOOR_Y + 2, slot.centerZ() + 0.5);
    }
    public boolean withinLobby(Location location) {
        World lobbyWorld = worlds.get(TowerTopology.Realm.OVERWORLD);
        return location.getWorld() != null && lobbyWorld != null
            && location.getWorld().getUID().equals(lobbyWorld.getUID())
            && Math.abs(location.getBlockX()) <= 10 && Math.abs(location.getBlockZ()) <= 10
            && location.getBlockY() >= TowerTopology.LOBBY_Y
            && location.getBlockY() <= TowerTopology.LOBBY_Y + 10;
    }
    public boolean withinAssigned(UUID player, Location location) {
        if (location.getWorld() == null) return false;
        var assigned = allocator.assigned(player);
        if (assigned.isEmpty()) return false;
        TowerTopology.Slot slot = assigned.get();
        World world = worlds.get(slot.realm());
        return world != null && world.getUID().equals(location.getWorld().getUID())
            && slot.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /** Stable, generated arena IDs are also registered with the existing F1.2 blueprint admin. */
    public void registerArenaConfig() {
        boolean changed = false;
        for (TowerTopology.Slot slot : TowerTopology.slots()) {
            String path = "arenas." + slot.id();
            if (plugin.getConfig().contains(path)) {
                if (!slot.realm().worldName().equals(plugin.getConfig().getString(path + ".world"))
                    || plugin.getConfig().getInt(path + ".min.x") != slot.minX()
                    || plugin.getConfig().getInt(path + ".min.y") != TowerTopology.ARENA_FLOOR_Y
                    || plugin.getConfig().getInt(path + ".min.z") != slot.minZ()
                    || plugin.getConfig().getInt(path + ".max.x") != slot.maxX()
                    || plugin.getConfig().getInt(path + ".max.y") != TowerTopology.ARENA_TOP_Y
                    || plugin.getConfig().getInt(path + ".max.z") != slot.maxZ())
                    throw new IllegalStateException("Conflicting arena definition: " + slot.id());
                continue;
            }
            plugin.getConfig().set(path + ".world", slot.realm().worldName());
            plugin.getConfig().set(path + ".min.x", slot.minX());
            plugin.getConfig().set(path + ".min.y", TowerTopology.ARENA_FLOOR_Y);
            plugin.getConfig().set(path + ".min.z", slot.minZ());
            plugin.getConfig().set(path + ".max.x", slot.maxX());
            plugin.getConfig().set(path + ".max.y", TowerTopology.ARENA_TOP_Y);
            plugin.getConfig().set(path + ".max.z", slot.maxZ());
            changed = true;
        }
        if (changed) plugin.saveConfig();
    }

    public void start() throws IOException {
        Files.createDirectories(plugin.getDataFolder().toPath());
        Properties props = new Properties();
        boolean firstStart = Files.notExists(marker);
        if (firstStart) {
            for (TowerTopology.Realm realm : TowerTopology.Realm.values()) {
                Path worldFolder = Bukkit.getWorldContainer().toPath().resolve(realm.worldName());
                if (Files.exists(worldFolder) || Bukkit.getWorld(realm.worldName()) != null)
                    throw new IOException("Existing unmanaged world, refusing to adopt: " + realm.worldName());
            }
            props.setProperty("version", String.valueOf(SCHEMA_VERSION));
            props.setProperty(STATUS, INITIALIZING);
            writeMarker(props); // record intent BEFORE creating any world
        } else {
            try (InputStream in = Files.newInputStream(marker)) { props.load(in); }
            if (!String.valueOf(SCHEMA_VERSION).equals(props.getProperty("version"))
                || (!INITIALIZING.equals(props.getProperty(STATUS))
                    && !READY.equals(props.getProperty(STATUS))))
                throw new IOException("Invalid managed-worlds marker. Refusing to modify worlds.");
        }

        for (TowerTopology.Realm realm : TowerTopology.Realm.values()) {
            World.Environment environment = switch (realm) {
                case OVERWORLD -> World.Environment.NORMAL;
                case NETHER -> World.Environment.NETHER;
                case END -> World.Environment.THE_END;
            };
            String expected = props.getProperty(realm.worldName() + ".uuid");
            World world = new WorldCreator(realm.worldName())
                .environment(environment)
                .generator(new VoidWorldGenerator())
                .generateStructures(false)
                .createWorld();
            if (world == null) throw new IOException("Unable to create " + realm.worldName());
            if (expected != null && !expected.equals(world.getUID().toString()))
                throw new IOException("UUID mismatch in managed world: " + realm.worldName());
            if (world.getEnvironment() != environment)
                throw new IOException("World environment mismatch: " + realm.worldName());
            worlds.put(realm, world);
            props.setProperty(realm.worldName() + ".uuid", world.getUID().toString());
            writeMarker(props);
            world.setSpawnFlags(false, false);
            world.setPVP(false);
            world.setDifficulty(Difficulty.PEACEFUL); // Preview only; gameplay must explicitly override.
        }

        if (READY.equals(props.getProperty(STATUS))) {
            verifyBlueprints();
            verifyCriticalWorldBlocks();
            lobbyReady = true;
            ready = true;
            plugin.getLogger().info("3 managed void worlds + 12 preserved towers loaded (no rebuild).");
            return;
        }

        buildLobby();
        lobbyReady = true;
        plugin.getLogger().info("Lobby ready. Preparing 12 tower shells in tick-limited batches.");
        beginTowerConstruction(props);
    }

    private void buildLobby() {
        World world = worlds.get(TowerTopology.Realm.OVERWORLD);
        for (int x = -10; x <= 10; x++) {
            for (int z = -10; z <= 10; z++) {
                world.getBlockAt(x, TowerTopology.LOBBY_Y, z).setType(
                    Math.abs(x) == 10 || Math.abs(z) == 10 ? Material.DEEPSLATE_BRICKS : Material.SMOOTH_STONE, false);
                for (int y = TowerTopology.LOBBY_Y + 1; y <= TowerTopology.LOBBY_Y + 5; y++) {
                    boolean edge = Math.abs(x) == 10 || Math.abs(z) == 10;
                    Material type = (edge && y <= TowerTopology.LOBBY_Y + 3) ? Material.GLASS : Material.AIR;
                    world.getBlockAt(x, y, z).setType(type, false);
                }
                if ((Math.abs(x) == 8 && Math.abs(z) == 8))
                    world.getBlockAt(x, TowerTopology.LOBBY_Y + 1, z).setType(Material.SEA_LANTERN, false);
            }
        }
        world.setSpawnLocation(0, TowerTopology.LOBBY_Y + 2, 0);
    }

    private void beginTowerConstruction(Properties props) {
        new BukkitRunnable() {
            private int towerIndex;
            private int blockIndex;
            private final int side = TowerTopology.ARENA_RADIUS * 2 + 1;
            private final int height = TowerTopology.ARENA_TOP_Y - TowerTopology.ARENA_FLOOR_Y + 1;
            private final int volume = side * side * height;

            @Override public void run() {
                try {
                    int budget = BLOCKS_PER_TICK;
                    while (budget > 0 && towerIndex < TowerTopology.slots().size()) {
                        TowerTopology.Slot slot = TowerTopology.slots().get(towerIndex);
                        if (blockIndex == 0 && blueprints.exists(slot.id())) {
                            verifyTower(slot); // on restart, never silently overwrite an existing blueprint
                            towerIndex++;
                            continue;
                        }
                        int ix = blockIndex % side;
                        int iz = (blockIndex / side) % side;
                        int iy = blockIndex / (side * side);
                        int x = slot.minX() + ix;
                        int y = TowerTopology.ARENA_FLOOR_Y + iy;
                        int z = slot.minZ() + iz;
                        Material desired = towerMaterial(slot, ix, iy, iz, side);
                        Block block = worlds.get(slot.realm()).getBlockAt(x, y, z);
                        if (block.getType() != desired) block.setType(desired, false);
                        budget--;
                        blockIndex++;
                        if (blockIndex == volume) {
                            saveTowerBlueprint(slot);
                            towerIndex++;
                            blockIndex = 0;
                        }
                    }
                    if (towerIndex == TowerTopology.slots().size()) {
                        verifyBlueprints();
                        for (World world : worlds.values()) world.save(true);
                        verifyCriticalWorldBlocks();
                        props.setProperty(STATUS, READY);
                        writeMarker(props);
                        ready = true;
                        cancel();
                        plugin.getLogger().info("12 private towers READY across Overworld, Nether and End. No combat enabled.");
                    }
                } catch (Exception ex) {
                    cancel();
                    ready = false;
                    plugin.getLogger().severe("Tower bootstrap stopped: " + ex.getMessage()
                        + ". Failing closed; no automatic world deletion.");
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    private Material towerMaterial(TowerTopology.Slot slot, int ix, int iy, int iz, int side) {
        Material floor = switch (slot.realm()) {
            case OVERWORLD -> Material.POLISHED_ANDESITE;
            case NETHER -> Material.POLISHED_BLACKSTONE;
            case END -> Material.END_STONE_BRICKS;
        };
        Material wall = switch (slot.realm()) {
            case OVERWORLD -> Material.STONE_BRICKS;
            case NETHER -> Material.POLISHED_BLACKSTONE_BRICKS;
            case END -> Material.PURPUR_BLOCK;
        };
        boolean edge = ix == 0 || iz == 0 || ix == side - 1 || iz == side - 1;
        if (iy == 0) return floor;
        if (edge && iy <= 8) return wall;
        if (edge && iy == 9 && (ix % 2 == 0 || iz % 2 == 0)) return wall;
        boolean lantern = (ix == 3 || ix == side - 4) && (iz == 3 || iz == side - 4);
        if (lantern && iy == 1) return Material.SEA_LANTERN;
        boolean pillar = (ix == 5 || ix == side - 6) && (iz == 5 || iz == side - 6);
        if (pillar && iy <= 4) return wall;
        return Material.AIR;
    }

    private ArenaBounds bounds(TowerTopology.Slot slot) {
        return new ArenaBounds(slot.minX(), TowerTopology.ARENA_FLOOR_Y, slot.minZ(),
            slot.maxX(), TowerTopology.ARENA_TOP_Y, slot.maxZ());
    }

    private void saveTowerBlueprint(TowerTopology.Slot slot) throws IOException {
        World w = worlds.get(slot.realm());
        List<String> data = new ArrayList<>(bounds(slot).volume());
        for (int y = TowerTopology.ARENA_FLOOR_Y; y <= TowerTopology.ARENA_TOP_Y; y++)
            for (int z = slot.minZ(); z <= slot.maxZ(); z++)
                for (int x = slot.minX(); x <= slot.maxX(); x++)
                    data.add(w.getBlockAt(x, y, z).getBlockData().getAsString());
        // ArenaBlueprintService's scan order differs; the blueprint MUST share that order (x -> z -> y).
        blueprints.create(new ArenaBlueprint(slot.id(), w.getUID(), bounds(slot), data));
        verifyTower(slot);
    }

    private void verifyTower(TowerTopology.Slot slot) throws IOException {
        World world = worlds.get(slot.realm());
        ArenaBlueprint blueprint = blueprints.read(slot.id());
        if (!blueprint.worldId().equals(world.getUID()) || !blueprint.bounds().equals(bounds(slot)))
            throw new IOException("Blueprint world/bounds mismatch for " + slot.id());
        int i = 0;
        for (int y = TowerTopology.ARENA_FLOOR_Y; y <= TowerTopology.ARENA_TOP_Y; y++)
            for (int z = slot.minZ(); z <= slot.maxZ(); z++)
                for (int x = slot.minX(); x <= slot.maxX(); x++)
                    if (!blueprint.blockData().get(i++).equals(world.getBlockAt(x, y, z).getBlockData().getAsString()))
                        throw new IOException("Changed tower structure in " + slot.id() + "; refusal to overwrite");
    }

    private void verifyCriticalWorldBlocks() throws IOException {
        World lobby = worlds.get(TowerTopology.Realm.OVERWORLD);
        if (lobby.getBlockAt(0, TowerTopology.LOBBY_Y, 0).getType() != Material.SMOOTH_STONE)
            throw new IOException("Lobby platform missing after reload");
        for (TowerTopology.Slot slot : TowerTopology.slots()) {
            World world = worlds.get(slot.realm());
            Material expected = towerMaterial(slot, TowerTopology.ARENA_RADIUS, 0,
                TowerTopology.ARENA_RADIUS, TowerTopology.ARENA_RADIUS * 2 + 1);
            if (world.getBlockAt(slot.centerX(), TowerTopology.ARENA_FLOOR_Y, slot.centerZ()).getType() != expected)
                throw new IOException("Tower floor missing: " + slot.id());
        }
    }

    private void verifyBlueprints() throws IOException {
        for (TowerTopology.Slot slot : TowerTopology.slots()) {
            ArenaBlueprint bp = blueprints.read(slot.id());
            World w = worlds.get(slot.realm());
            if (!bp.worldId().equals(w.getUID()) || !bp.bounds().equals(bounds(slot)))
                throw new IOException("Blueprint mismatch: " + slot.id());
        }
    }

    private void writeMarker(Properties props) throws IOException {
        Path temp = marker.resolveSibling(MARKER_FILE + ".tmp");
        try (OutputStream output = Files.newOutputStream(temp)) {
            props.store(output, "Torre do Caos - managed worlds, do not edit");
        }
        try {
            Files.move(temp, marker, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (java.nio.file.AtomicMoveNotSupportedException ex) {
            Files.move(temp, marker, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
