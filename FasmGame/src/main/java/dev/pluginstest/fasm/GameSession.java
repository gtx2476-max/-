package dev.pluginstest.fasm;

import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class GameSession {

    private final World world;
    private final GameSettings settings;
    private final Set<UUID> players = new HashSet<>();

    public GameSession(World world, GameSettings settings) {
        this.world = world;
        this.settings = settings;
    }

    public World world() {
        return world;
    }

    public GameSettings settings() {
        return settings;
    }

    public Set<UUID> players() {
        return players;
    }
}
