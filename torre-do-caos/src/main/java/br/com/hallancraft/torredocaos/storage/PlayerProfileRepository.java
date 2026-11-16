package br.com.hallancraft.torredocaos.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.UUID;

/** Permanent progression; reward increment is one SQL statement. */
public final class PlayerProfileRepository {
    private final Connection db;
    public PlayerProfileRepository(Connection db) { this.db = Objects.requireNonNull(db); }

    public void ensurePlayer(UUID uuid, String name) throws SQLException {
        Objects.requireNonNull(uuid);
        Objects.requireNonNull(name);
        try (PreparedStatement ps = db.prepareStatement("""
                INSERT INTO player_profiles(player_uuid, player_name, updated_at)
                VALUES (?, ?, ?)
                ON CONFLICT(player_uuid) DO UPDATE SET
                    player_name = excluded.player_name,
                    updated_at = excluded.updated_at
                """)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, name);
            ps.setLong(3, System.currentTimeMillis());
            ps.executeUpdate();
        }
    }

    public void addCoins(UUID uuid, int amount) throws SQLException {
        if (amount < 0) throw new IllegalArgumentException("amount must be >= 0");
        try (PreparedStatement ps = db.prepareStatement("""
                UPDATE player_profiles
                SET permanent_coins = permanent_coins + ?, updated_at = ?
                WHERE player_uuid = ?
                """)) {
            ps.setInt(1, amount);
            ps.setLong(2, System.currentTimeMillis());
            ps.setString(3, uuid.toString());
            if (ps.executeUpdate() != 1) throw new SQLException("Unknown player: " + uuid);
        }
    }

    public OptionalLong coins(UUID uuid) throws SQLException {
        try (PreparedStatement ps = db.prepareStatement(
                "SELECT permanent_coins FROM player_profiles WHERE player_uuid=?")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? OptionalLong.of(rs.getLong(1)) : OptionalLong.empty();
            }
        }
    }
}
