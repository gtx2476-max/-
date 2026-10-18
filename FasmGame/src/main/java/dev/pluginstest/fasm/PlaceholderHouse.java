package dev.pluginstest.fasm;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

/**
 * Простой дом-заготовка для этапа 1, чтобы было куда телепортироваться.
 * На этапе 2 заменим на детальный двухэтажный дом.
 */
public final class PlaceholderHouse {

    private PlaceholderHouse() {
    }

    public static Location build(World w) {
        // газон
        for (int x = -22; x <= 22; x++) {
            for (int z = -16; z <= 16; z++) {
                set(w, x, 62, z, Material.DIRT);
                set(w, x, 63, z, Material.GRASS_BLOCK);
            }
        }
        // дом: x от -10 до 10, z от -7 до 7, стены на высоте 64..67
        for (int x = -10; x <= 10; x++) {
            for (int z = -7; z <= 7; z++) {
                boolean edge = Math.abs(x) == 10 || Math.abs(z) == 7;
                set(w, x, 63, z, Material.OAK_PLANKS);
                for (int y = 64; y <= 67; y++) {
                    set(w, x, y, z, edge ? Material.LIGHT_GRAY_TERRACOTTA : Material.AIR);
                }
                set(w, x, 68, z, Material.DARK_OAK_PLANKS);
            }
        }
        // окна на длинных стенах
        int[] winX = {-6, -5, 5, 6};
        for (int x : winX) {
            for (int y = 65; y <= 66; y++) {
                set(w, x, y, -7, Material.GLASS_PANE);
                set(w, x, y, 7, Material.GLASS_PANE);
            }
        }
        // окна на коротких стенах
        int[] winZ = {-4, -3, 3, 4};
        for (int z : winZ) {
            for (int y = 65; y <= 66; y++) {
                set(w, -10, y, z, Material.GLASS_PANE);
                set(w, 10, y, z, Material.GLASS_PANE);
            }
        }
        // входной проём
        for (int x = 0; x <= 1; x++) {
            set(w, x, 64, 7, Material.AIR);
            set(w, x, 65, 7, Material.AIR);
        }
        // внутренняя перегородка с проёмом
        for (int x = -9; x <= 9; x++) {
            for (int y = 64; y <= 67; y++) {
                set(w, x, y, 0, Material.WHITE_TERRACOTTA);
            }
        }
        for (int x = 3; x <= 4; x++) {
            set(w, x, 64, 0, Material.AIR);
            set(w, x, 65, 0, Material.AIR);
        }
        // немного света
        set(w, -5, 64, -3, Material.LANTERN);
        set(w, 5, 64, 3, Material.LANTERN);
        set(w, -5, 64, 4, Material.LANTERN);

        return new Location(w, 0.5, 64, 4.5, 180f, 0f);
    }

    private static void set(World w, int x, int y, int z, Material m) {
        w.getBlockAt(x, y, z).setType(m);
    }
}
