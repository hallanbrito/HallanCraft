package br.com.hallancraft.torredocaos.arena;

import br.com.hallancraft.torredocaos.storage.DatabaseManager;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/** F1.2: exports and verifies arena block snapshots. Does not restore or enable gameplay. */
public final class ArenaBlueprintService {
    private static final int BLOCKS_PER_TICK = 128;
    private final JavaPlugin plugin;
    private final DatabaseManager database;
    private final ArenaBlueprintStore store;
    private final Map<String, ConfiguredArena> configured = new HashMap<>();
    private final Set<String> busy = new HashSet<>();

    public ArenaBlueprintService(JavaPlugin plugin, DatabaseManager database) {
        this.plugin = plugin;
        this.database = database;
        this.store = new ArenaBlueprintStore(plugin.getDataFolder().toPath().resolve("arena-blueprints"));
        ConfigurationSection arenas = plugin.getConfig().getConfigurationSection("arenas");
        if (arenas != null) for (String id : arenas.getKeys(false)) {
            store.pathFor(id);
            ConfigurationSection section = arenas.getConfigurationSection(id);
            if (section == null) throw new IllegalArgumentException("Invalid arena config: " + id);
            String name = section.getString("world");
            if (name == null || name.isBlank()) throw new IllegalArgumentException("Missing arena world");
            ArenaBounds bounds = new ArenaBounds(section.getInt("min.x"), section.getInt("min.y"),
                section.getInt("min.z"), section.getInt("max.x"),
                section.getInt("max.y"), section.getInt("max.z"));
            configured.put(id, new ConfiguredArena(name, bounds));
        }
    }

    public boolean isLocked(Block block) {
        for (Map.Entry<String, ConfiguredArena> entry : configured.entrySet()) {
            ConfiguredArena arena = entry.getValue();
            if (arena.worldName.equals(block.getWorld().getName())
                && arena.bounds.contains(block.getX(), block.getY(), block.getZ())
                && (store.exists(entry.getKey()) || busy.contains(entry.getKey())))
                return true;
        }
        return false;
    }

    public void export(CommandSender sender, String id) {
        ConfiguredArena config = configured.get(id);
        if (config == null) { sender.sendMessage("Arena não configurada: " + id); return; }
        if (store.exists(id)) { sender.sendMessage("Blueprint já existe: sobrescrita proibida."); return; }
        if (!busy.isEmpty()) { sender.sendMessage("Outra operação de blueprint está ativa."); return; }
        World world = Bukkit.getWorld(config.worldName);
        if (world == null) { sender.sendMessage("Mundo não carregado."); return; }
        if (!checkBounds(sender, world, config.bounds)) return;
        try {
            if (hasPendingRun()) {
                sender.sendMessage("Exportação bloqueada: run ativa ou alterações aguardando restauração.");
                return;
            }
        } catch (SQLException e) {
            sender.sendMessage("Erro consultando SQLite; exportação bloqueada.");
            return;
        }
        busy.add(id);
        sender.sendMessage("Exportação iniciada em lotes; aguarde confirmação.");
        scan(sender, id, config.bounds, world, null);
    }

    public void verify(CommandSender sender, String id) {
        ConfiguredArena config = configured.get(id);
        if (config == null) { sender.sendMessage("Arena não configurada."); return; }
        if (!busy.isEmpty()) { sender.sendMessage("Outra operação está ativa."); return; }
        if (!store.exists(id)) { sender.sendMessage("Blueprint ainda não existe."); return; }
        World world = Bukkit.getWorld(config.worldName);
        if (world == null) { sender.sendMessage("Mundo não carregado."); return; }
        if (!checkBounds(sender, world, config.bounds)) return;
        try {
            ArenaBlueprint expected = store.read(id);
            if (!expected.worldId().equals(world.getUID()) || !expected.bounds().equals(config.bounds)) {
                sender.sendMessage("UUID ou limites de mundo diferentes do blueprint.");
                return;
            }
            busy.add(id);
            scan(sender, id, config.bounds, world, expected);
        } catch (IOException e) { sender.sendMessage("Blueprint inválido: " + e.getMessage()); }
    }

    private boolean hasPendingRun() throws SQLException {
        try (Statement s = database.connection().createStatement();
             ResultSet rs = s.executeQuery("SELECT "
                  + "(SELECT COUNT(*) FROM active_run_state) + "
                  + "(SELECT COUNT(*) FROM arena_block_journal)")) {
            return rs.next() && rs.getLong(1) > 0;
        }
    }

    private boolean checkBounds(CommandSender sender, World world, ArenaBounds b) {
        if (b.minY() < world.getMinHeight() || b.maxY() >= world.getMaxHeight()) {
            sender.sendMessage("Limites verticais inválidos."); return false;
        }
        for (int x = b.minX() >> 4; x <= b.maxX() >> 4; x++)
            for (int z = b.minZ() >> 4; z <= b.maxZ() >> 4; z++)
                if (!world.isChunkLoaded(x, z)) {
                    sender.sendMessage("Chunk não carregado (" + x + "," + z + ").");
                    return false;
                }
        return true;
    }

    /** Reads 128 blocks per tick on the main thread. The active region stays locked. */
    private void scan(CommandSender sender, String id, ArenaBounds bounds, World world, ArenaBlueprint expected) {
        List<String> blocks = expected == null ? new ArrayList<>(bounds.volume()) : null;
        new BukkitRunnable() {
            int processed, differences;
            @Override public void run() {
                try {
                    int dx = bounds.maxX() - bounds.minX() + 1;
                    int dz = bounds.maxZ() - bounds.minZ() + 1;
                    for (int i = 0; i < BLOCKS_PER_TICK && processed < bounds.volume(); i++, processed++) {
                        int x = bounds.minX() + processed % dx;
                        int z = bounds.minZ() + (processed / dx) % dz;
                        int y = bounds.minY() + processed / (dx * dz);
                        if (!world.isChunkLoaded(x >> 4, z >> 4))
                            throw new IOException("Chunk descarregado durante operação.");
                        Block block = world.getBlockAt(x, y, z);
                        if (block.getState(false) instanceof TileState)
                            throw new IOException("Block entity não suportada em " + x + "," + y + "," + z);
                        String data = block.getBlockData().getAsString();
                        if (blocks != null) blocks.add(data);
                        else if (!data.equals(expected.blockData().get(processed))) differences++;
                    }
                    if (processed >= bounds.volume()) {
                        cancel();
                        if (blocks != null) {
                            ArenaBlueprint snapshot = new ArenaBlueprint(id, world.getUID(), bounds, blocks);
                            store.create(snapshot);
                            if (!snapshot.equals(store.read(id))) throw new IOException("Read-back divergente");
                            sender.sendMessage("Blueprint criado: " + id + " (" + bounds.volume() + " blocos).");
                        } else {
                            sender.sendMessage("Integridade " + id + ": " + differences + " blocos divergentes.");
                        }
                        busy.remove(id);
                    }
                } catch (Exception e) {
                    cancel();
                    busy.remove(id);
                    plugin.getLogger().warning("Erro F1.2 em " + id + ": " + e.getMessage());
                    sender.sendMessage("Operação falhou; mundo não restaurado/modificado: " + e.getMessage());
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }
    private record ConfiguredArena(String worldName, ArenaBounds bounds) { }
}
