package dev.pluginstest.fasm;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

/** Пустой мир: никакой генерации, всё строим сами. */
public final class VoidGenerator extends ChunkGenerator {

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0.5, 64, 0.5);
    }
}
