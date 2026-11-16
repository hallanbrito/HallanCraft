package br.com.hallancraft.torredocaos.arena;

/** Inclusive arena bounds, limited to a safe MVP size. */
public record ArenaBounds(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
    public static final int MAX_BLOCKS = 20_000;
    public ArenaBounds {
        if (minX > maxX || minY > maxY || minZ > maxZ) throw new IllegalArgumentException("Inverted bounds");
        long x = (long) maxX - minX + 1, y = (long) maxY - minY + 1, z = (long) maxZ - minZ + 1;
        if (x > MAX_BLOCKS || y > MAX_BLOCKS || z > MAX_BLOCKS || x * y * z > MAX_BLOCKS)
            throw new IllegalArgumentException("Arena exceeds " + MAX_BLOCKS + " blocks");
    }
    public int volume() {
        return (int) (((long) maxX - minX + 1) * ((long) maxY - minY + 1) * ((long) maxZ - minZ + 1));
    }
    public boolean contains(int x, int y, int z) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }
}
