package br.com.hallancraft.torredocaos.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

/**
 * F1.1: One SQLite connection and versioned migrations.
 * Do not share this connection with arbitrary async Paper tasks.
 */
public final class DatabaseManager implements AutoCloseable {
    private static final List<String> MIGRATIONS = List.of("V001__baseline.sql");
    private final Path file;
    private Connection connection;

    public DatabaseManager(Path file) {
        this.file = Objects.requireNonNull(file).toAbsolutePath();
    }

    public synchronized void openAndMigrate() throws SQLException, IOException {
        if (connection != null) throw new IllegalStateException("Database already open");
        Files.createDirectories(file.getParent());
        try { Class.forName("org.sqlite.JDBC"); }
        catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found. Paper bundles this driver.", e);
        }
        connection = DriverManager.getConnection("jdbc:sqlite:" + file);
        try {
            try (Statement st = connection.createStatement()) {
                st.execute("PRAGMA busy_timeout=5000");
                st.execute("PRAGMA foreign_keys=ON");
                st.execute("PRAGMA journal_mode=WAL");
                st.execute("PRAGMA synchronous=FULL");
                st.execute("""
                    CREATE TABLE IF NOT EXISTS schema_migrations (
                      version TEXT PRIMARY KEY NOT NULL,
                      applied_at INTEGER NOT NULL
                    )
                    """);
            }
            for (String migration : MIGRATIONS) applyMigration(migration);
        } catch (SQLException | IOException | RuntimeException e) {
            close();
            throw e;
        }
    }

    private void applyMigration(String resourceName) throws SQLException, IOException {
        try (PreparedStatement check = connection.prepareStatement(
                "SELECT 1 FROM schema_migrations WHERE version = ?")) {
            check.setString(1, resourceName);
            try (ResultSet rs = check.executeQuery()) { if (rs.next()) return; }
        }
        String sql;
        String resource = "/db/migration/" + resourceName;
        try (InputStream in = DatabaseManager.class.getResourceAsStream(resource)) {
            if (in == null) throw new IOException("Missing migration resource: " + resource);
            sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        StringBuilder cleaned = new StringBuilder();
        for (String line : sql.split("\\R")) {
            if (!line.stripLeading().startsWith("--")) cleaned.append(line).append('\n');
        }
        connection.setAutoCommit(false);
        try {
            try (Statement st = connection.createStatement()) {
                for (String statement : cleaned.toString().split(";")) {
                    if (!statement.isBlank()) st.execute(statement);
                }
            }
            try (PreparedStatement record = connection.prepareStatement(
                    "INSERT INTO schema_migrations(version, applied_at) VALUES (?, ?)")) {
                record.setString(1, resourceName);
                record.setLong(2, System.currentTimeMillis());
                record.executeUpdate();
            }
            connection.commit();
        } catch (SQLException | RuntimeException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public synchronized Connection connection() {
        if (connection == null) throw new IllegalStateException("Database not open");
        return connection;
    }

    @Override
    public synchronized void close() throws SQLException {
        if (connection != null) {
            try { connection.close(); }
            finally { connection = null; }
        }
    }
}
