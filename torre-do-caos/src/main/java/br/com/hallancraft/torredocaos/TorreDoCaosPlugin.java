package br.com.hallancraft.torredocaos;

import br.com.hallancraft.torredocaos.arena.ArenaAdminCommand;
import br.com.hallancraft.torredocaos.arena.ArenaBlueprintService;
import br.com.hallancraft.torredocaos.arena.ArenaProtectionListener;
import br.com.hallancraft.torredocaos.storage.DatabaseManager;
import br.com.hallancraft.torredocaos.world.TowerLobbyCommand;
import br.com.hallancraft.torredocaos.world.TowerWorldListener;
import br.com.hallancraft.torredocaos.world.TowerWorldService;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/** F1.2.2: empty worlds, isolated arena shells and lobby preview. Gameplay still disabled. */
public final class TorreDoCaosPlugin extends JavaPlugin {
    private DatabaseManager db;

    @Override public void onEnable() {
        saveDefaultConfig();
        if (getConfig().getBoolean("enabled-gameplay", false)) {
            getLogger().severe("Gameplay F1.3-F1.6 not ready; disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        try {
            db = new DatabaseManager(getDataFolder().toPath().resolve("torredocaos.db"));
            db.openAndMigrate();
            TowerWorldService worlds = new TowerWorldService(this);
            worlds.registerArenaConfig();
            worlds.start();
            ArenaBlueprintService blueprints = new ArenaBlueprintService(this, db);
            Objects.requireNonNull(getCommand("torre"), "Missing /torre declaration")
                .setExecutor(new TowerLobbyCommand(worlds, new ArenaAdminCommand(blueprints)));
            Bukkit.getPluginManager().registerEvents(new ArenaProtectionListener(blueprints), this);
            Bukkit.getPluginManager().registerEvents(new TowerWorldListener(this, worlds), this);
            getLogger().info("F1.2.2 managed worlds enabled. Gameplay remains disabled.");
        } catch (SQLException | IOException | RuntimeException e) {
            getLogger().severe("Initialization failed (no world cleanup attempted): " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }
    @Override public void onDisable() {
        if (db != null) {
            try { db.close(); }
            catch (SQLException e) { getLogger().warning("SQLite close failed: " + e.getMessage()); }
        }
    }
}
