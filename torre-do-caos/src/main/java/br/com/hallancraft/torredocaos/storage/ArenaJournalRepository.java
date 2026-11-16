package br.com.hallancraft.torredocaos.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** F1.1 persistence only. Actual block restoration is not implemented yet. */
public final class ArenaJournalRepository {
    private final Connection db;
    public ArenaJournalRepository(Connection db) { this.db = Objects.requireNonNull(db); }

    /** Records the untouched original block once. NEVER overwrites the original snapshot. */
    public boolean recordOriginal(BlockSnapshot b) throws SQLException {
        try (PreparedStatement ps = db.prepareStatement("""
                INSERT OR IGNORE INTO arena_block_journal(
                    session_id, world_id, arena_id, x, y, z, original_block_data)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """)) {
            ps.setString(1, b.sessionId());
            ps.setString(2, b.worldId());
            ps.setString(3, b.arenaId());
            ps.setInt(4, b.x());
            ps.setInt(5, b.y());
            ps.setInt(6, b.z());
            ps.setString(7, b.originalBlockData());
            return ps.executeUpdate() == 1;
        }
    }

    /** Reapply even restored_in_memory=1 rows after restart until the disk flush is verified. */
    public List<BlockSnapshot> allPending() throws SQLException {
        List<BlockSnapshot> result = new ArrayList<>();
        try (PreparedStatement ps = db.prepareStatement("""
                SELECT session_id, world_id, arena_id, x, y, z, original_block_data
                FROM arena_block_journal ORDER BY rowid
                """); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new BlockSnapshot(rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getString(7)));
            }
        }
        return result;
    }

    public record BlockSnapshot(String sessionId, String worldId, String arenaId,
                                int x, int y, int z, String originalBlockData) {
        public BlockSnapshot {
            Objects.requireNonNull(sessionId);
            Objects.requireNonNull(worldId);
            Objects.requireNonNull(arenaId);
            Objects.requireNonNull(originalBlockData);
        }
    }
}
