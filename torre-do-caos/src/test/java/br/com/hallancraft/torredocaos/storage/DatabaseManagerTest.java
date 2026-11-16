package br.com.hallancraft.torredocaos.storage;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DatabaseManagerTest {
    @TempDir Path temp;

    @Test
    void migrationIsRepeatableAndSurvivesReconnection() throws Exception {
        Path file = temp.resolve("survive.db");
        try (DatabaseManager db = new DatabaseManager(file)) {
            db.openAndMigrate();
            verifySchema(db.connection());
        }
        try (DatabaseManager db = new DatabaseManager(file)) {
            db.openAndMigrate();
            verifySchema(db.connection());
        }
    }

    private static void verifySchema(Connection connection) throws SQLException {
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM schema_migrations")) {
            assertTrue(rs.next());
            assertEquals(1, rs.getInt(1), "Migration must never be applied twice");
        }
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("PRAGMA foreign_keys")) {
            assertTrue(rs.next());
            assertEquals(1, rs.getInt(1));
        }
    }

    @Test
    void permanentCoinsAndJournalSurviveReconnectWithoutChangingOriginalBlock() throws Exception {
        Path file = temp.resolve("progress.db");
        UUID player = UUID.randomUUID();
        var original = new ArenaJournalRepository.BlockSnapshot(
            "run-1", "world-1", "arena-1", 5, 64, 8, "minecraft:stone_bricks");
        var replaced = new ArenaJournalRepository.BlockSnapshot(
            "run-1", "world-1", "arena-1", 5, 64, 8, "minecraft:dirt");
        try (DatabaseManager db = new DatabaseManager(file)) {
            db.openAndMigrate();
            var profiles = new PlayerProfileRepository(db.connection());
            profiles.ensurePlayer(player, "JogadorTeste");
            profiles.addCoins(player, 15);
            profiles.ensurePlayer(player, "JogadorRenomeado");
            assertEquals(15, profiles.coins(player).orElseThrow());
            var journal = new ArenaJournalRepository(db.connection());
            assertTrue(journal.recordOriginal(original));
            assertFalse(journal.recordOriginal(replaced));
        }
        try (DatabaseManager db = new DatabaseManager(file)) {
            db.openAndMigrate();
            assertEquals(15, new PlayerProfileRepository(db.connection()).coins(player).orElseThrow());
            assertEquals(original,
                new ArenaJournalRepository(db.connection()).allPending().getFirst());
        }
    }

    @Test
    void foreignKeyConstraintAndSingleActiveRunAreEnforced() throws Exception {
        try (DatabaseManager db = new DatabaseManager(temp.resolve("constraints.db"))) {
            db.openAndMigrate();
            try (Statement st = db.connection().createStatement()) {
                assertThrows(SQLException.class, () -> st.executeUpdate("""
                  INSERT INTO player_permanent_upgrades(player_uuid,upgrade_id,upgrade_level)
                  VALUES('missing-player','life',1)
                  """));
                st.executeUpdate("""
                  INSERT INTO active_run_state(session_id,slot,state,current_world_id,current_arena_id,current_floor,current_wave,started_at)
                  VALUES('r1',1,'WAVE_COMBAT','w','a',1,1,1)
                  """);
                assertThrows(SQLException.class, () -> st.executeUpdate("""
                  INSERT INTO active_run_state(session_id,slot,state,current_world_id,current_arena_id,current_floor,current_wave,started_at)
                  VALUES('r2',1,'WAVE_COMBAT','w','b',1,1,1)
                  """));
            }
        }
    }
}
