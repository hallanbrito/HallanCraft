package br.com.hallancraft.torredocaos;

import br.com.hallancraft.torredocaos.arena.ArenaAdminCommand;
import br.com.hallancraft.torredocaos.arena.ArenaBlueprintService;
import br.com.hallancraft.torredocaos.arena.ArenaProtectionListener;
import br.com.hallancraft.torredocaos.storage.DatabaseManager;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/** F1.2: blueprint admin + locked arena. Gameplay is still disabled. */
public final class TorreDoCaosPlugin extends JavaPlugin {
    private DatabaseManager db;

    @Override public void onEnable() {
        saveDefaultConfig();
        if (getConfig().getBoolean("enabled-gameplay", false)) {
            getLogger().severe("Gameplay F1.3+ not ready; disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        try {
            db = new DatabaseManager(getDataFolder().toPath().resolve("torredocaos.db"));
            db.openAndMigrate();
            ArenaBlueprintService blueprints = new ArenaBlueprintService(this, db);
            Objects.requireNonNull(getCommand("torre"), "Missing /torre declaration")
                .setExecutor(new ArenaAdminCommand(blueprints));
            Bukkit.getPluginManager().registerEvents(new ArenaProtectionListener(blueprints), this);
            getLogger().info("F1.2 blueprint admin enabled. Gameplay remains disabled.");
        } catch (SQLException | IOException | RuntimeException e) {
            getLogger().severe("Initialization failed: " + e.getMessage());
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
