package dev.pluginstest.fasm;

import org.bukkit.Material;
import org.bukkit.World;

/** Строит остров-лобби. Строится один раз. */
public final class LobbyBuilder {

    private static final int RADIUS = 14;

    private LobbyBuilder() {
    }

    public static void buildIfNeeded(World w) {
        if (w.getBlockAt(0, 63, 0).getType() == Material.POLISHED_ANDESITE) {
            return;
        }
        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int z = -RADIUS; z <= RADIUS; z++) {
                int d = x * x + z * z;
                if (d > RADIUS * RADIUS) {
                    continue;
                }
                set(w, x, 61, z, Material.DIRT);
                set(w, x, 62, z, Material.DIRT);
                set(w, x, 63, z, Material.GRASS_BLOCK);
                if (d >= (RADIUS - 1) * (RADIUS - 1)) {
                    set(w, x, 64, z, Material.OAK_FENCE);
                }
            }
        }
        // площадка под NPC
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                set(w, x, 63, z, Material.POLISHED_ANDESITE);
            }
        }
        // четыре фонаря по углам
        int[][] posts = {{-2, -2}, {2, -2}, {-2, 2}, {2, 2}};
        for (int[] p : posts) {
            set(w, p[0], 64, p[1], Material.OAK_FENCE);
            set(w, p[0], 65, p[1], Material.OAK_FENCE);
            set(w, p[0], 66, p[1], Material.LANTERN);
        }
    }

    private static void set(World w, int x, int y, int z, Material m) {
        w.getBlockAt(x, y, z).setType(m);
    }
}
