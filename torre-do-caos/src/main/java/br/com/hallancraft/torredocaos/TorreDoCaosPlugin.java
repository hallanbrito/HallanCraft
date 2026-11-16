package br.com.hallancraft.torredocaos;

import br.com.hallancraft.torredocaos.storage.DatabaseManager;
import java.io.IOException;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/** Paper entrypoint for the F0/F1.1 foundation ONLY. Gameplay is disabled until recovery is implemented. */
public final class TorreDoCaosPlugin extends JavaPlugin {
    private DatabaseManager db;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (getConfig().getBoolean("enabled-gameplay", false)) {
            getLogger().severe("Gameplay not yet implemented. Disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        try {
            db = new DatabaseManager(getDataFolder().toPath().resolve("torredocaos.db"));
            db.openAndMigrate();
            getLogger().info("SQLite F1.1 migrations applied. Gameplay disabled by design.");
        } catch (SQLException | IOException e) {
            getLogger().severe("Database initialization failed: " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (db != null) {
            try { db.close(); }
            catch (SQLException e) { getLogger().warning("SQLite closing error: " + e.getMessage()); }
        }
    }
}
