package br.com.hallancraft.torredocaos.arena;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.security.*;
import java.util.*;

/** Versioned SHA-256-checked snapshot file, written once with CREATE_NEW. */
public final class ArenaBlueprintStore {
    private static final String MAGIC = "TDC-BP01";
    private static final int HASH_SIZE = 32, MAX_BYTES = 12_000_000;
    private final Path directory;
    public ArenaBlueprintStore(Path directory) {
        this.directory = Objects.requireNonNull(directory).toAbsolutePath().normalize();
    }
    public Path pathFor(String id) {
        if (id == null || !id.matches("[a-z0-9][a-z0-9_-]{0,31}"))
            throw new IllegalArgumentException("Invalid arena ID");
        return directory.resolve("arena_blueprint_" + id + ".dat");
    }
    public boolean exists(String id) { return Files.exists(pathFor(id)); }

    /** An interrupted write leaves an invalid, non-overwritable file: fail closed and repair offline. */
    public synchronized Path create(ArenaBlueprint blueprint) throws IOException {
        Path target = pathFor(blueprint.arenaId());
        Files.createDirectories(directory);
        byte[] content = encode(blueprint);
        try (FileChannel channel = FileChannel.open(target, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
            ByteBuffer buffer = ByteBuffer.wrap(content);
            while (buffer.hasRemaining()) channel.write(buffer);
            channel.force(true);
        }
        return target;
    }
    public ArenaBlueprint read(String id) throws IOException {
        Path file = pathFor(id);
        long length = Files.size(file);
        if (length < HASH_SIZE + 20 || length > MAX_BYTES) throw new IOException("Invalid blueprint size");
        byte[] all = Files.readAllBytes(file);
        byte[] payload = Arrays.copyOf(all, all.length - HASH_SIZE);
        byte[] checksum = Arrays.copyOfRange(all, payload.length, all.length);
        if (!MessageDigest.isEqual(sha256(payload), checksum)) throw new IOException("Blueprint checksum mismatch");
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(payload))) {
            if (!MAGIC.equals(in.readUTF())) throw new IOException("Unknown blueprint version");
            String storedId = in.readUTF();
            if (!id.equals(storedId)) throw new IOException("Arena mismatch");
            UUID worldId = UUID.fromString(in.readUTF());
            ArenaBounds b = new ArenaBounds(in.readInt(), in.readInt(), in.readInt(),
                in.readInt(), in.readInt(), in.readInt());
            int count = in.readInt();
            if (count != b.volume()) throw new IOException("Block count mismatch");
            List<String> blocks = new ArrayList<>(count);
            for (int i = 0; i < count; i++) blocks.add(in.readUTF());
            if (in.available() != 0) throw new IOException("Unexpected trailing bytes");
            return new ArenaBlueprint(storedId, worldId, b, blocks);
        } catch (RuntimeException e) { throw new IOException("Corrupted blueprint", e); }
    }
    private static byte[] encode(ArenaBlueprint blueprint) throws IOException {
        ByteArrayOutputStream raw = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(raw)) {
            out.writeUTF(MAGIC); out.writeUTF(blueprint.arenaId()); out.writeUTF(blueprint.worldId().toString());
            ArenaBounds b = blueprint.bounds();
            out.writeInt(b.minX()); out.writeInt(b.minY()); out.writeInt(b.minZ());
            out.writeInt(b.maxX()); out.writeInt(b.maxY()); out.writeInt(b.maxZ());
            out.writeInt(blueprint.blockData().size());
            for (String block : blueprint.blockData()) out.writeUTF(block);
        }
        byte[] bytes = raw.toByteArray();
        if (bytes.length + HASH_SIZE > MAX_BYTES) throw new IOException("Blueprint exceeds size limit");
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        result.write(bytes);
        result.write(sha256(bytes));
        return result.toByteArray();
    }
    private static byte[] sha256(byte[] data) throws IOException {
        try { return MessageDigest.getInstance("SHA-256").digest(data); }
        catch (NoSuchAlgorithmException ex) { throw new IOException("SHA-256 unavailable", ex); }
    }
}
