package dev.pluginstest.fasm;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public final class GameManager {

    private static final int MAX_PLAYERS = 4;

    private final FasmPlugin plugin;
    private final Map<UUID, GameSession> byPlayer = new HashMap<>();
    private final List<GameSession> sessions = new ArrayList<>();
    private int counter = 0;

    public GameManager(FasmPlugin plugin) {
        this.plugin = plugin;
    }

    public GameSession sessionOf(Player p) {
        return byPlayer.get(p.getUniqueId());
    }

    public void start(Player leader, GameSettings settings) {
        if (sessionOf(leader) != null) {
            leader.sendMessage(Component.text("Ты уже в игре. Выйти: /fasm", NamedTextColor.RED));
            return;
        }
        World lobby = plugin.lobby();
        List<Player> party = new ArrayList<>();
        party.add(leader);
        for (Entity e : leader.getNearbyEntities(8, 8, 8)) {
            if (e instanceof Player other
                    && other.getWorld().equals(lobby)
                    && sessionOf(other) == null
                    && party.size() < MAX_PLAYERS) {
                party.add(other);
            }
        }

        String name = FasmPlugin.GAME_PREFIX + (++counter) + "_" + (System.currentTimeMillis() % 100000);
        World world = new WorldCreator(name)
                .generator(new VoidGenerator())
                .generateStructures(false)
                .createWorld();
        if (world == null) {
            leader.sendMessage(Component.text("Не удалось создать мир игры.", NamedTextColor.RED));
            return;
        }
        world.setAutoSave(false);
        world.setDifficulty(Difficulty.NORMAL);
        Location spawn = PlaceholderHouse.build(world);
        world.setSpawnLocation(0, 64, 4);

        GameSession session = new GameSession(world, settings.copy());
        sessions.add(session);

        Title title = Title.title(
                Component.text("Фазмофобия", NamedTextColor.RED),
                Component.text("Найдите улики и выясните тип призрака", NamedTextColor.GRAY));
        for (Player p : party) {
            session.players().add(p.getUniqueId());
            byPlayer.put(p.getUniqueId(), session);
            p.teleport(spawn);
            p.setGameMode(GameMode.ADVENTURE);
            p.showTitle(title);
            p.sendMessage(Component.text("Сложность: " + session.settings().difficulty().title()
                    + ", улики: " + session.settings().evidence()
                    + ". Выйти в лобби: /fasm", NamedTextColor.GRAY));
        }
    }

    /** Убрать игрока из игры (выход, смена мира, отключение). */
    public void removePlayer(Player p) {
        GameSession session = byPlayer.remove(p.getUniqueId());
        if (session == null) {
            return;
        }
        session.players().remove(p.getUniqueId());
        if (session.players().isEmpty()) {
            sessions.remove(session);
            // небольшая задержка, чтобы игрок успел покинуть мир
            Bukkit.getScheduler().runTaskLater(plugin, () -> destroyWorld(session.world()), 20L);
        }
    }

    /** Вызывается при выключении плагина. */
    public void shutdown() {
        Location lobbySpawn = plugin.lobbySpawn();
        for (GameSession session : new ArrayList<>(sessions)) {
            for (Player p : session.world().getPlayers()) {
                p.teleport(lobbySpawn);
            }
            destroyWorld(session.world());
        }
        sessions.clear();
        byPlayer.clear();
    }

    private void destroyWorld(World world) {
        File folder = world.getWorldFolder();
        for (Player p : world.getPlayers()) {
            p.teleport(plugin.lobbySpawn());
        }
        Bukkit.unloadWorld(world, false);
        deleteFolder(folder);
    }

    /** Удаляет оставшиеся с прошлого запуска игровые миры. */
    public static void cleanupLeftovers() {
        File[] files = Bukkit.getWorldContainer().listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory() && f.getName().startsWith(FasmPlugin.GAME_PREFIX)) {
                deleteFolder(f);
            }
        }
    }

    private static void deleteFolder(File folder) {
        Path root = folder.toPath();
        if (!Files.exists(root)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(root)) {
            walk.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                    // файл может быть занят, не критично
                }
            });
        } catch (IOException ignored) {
            // не критично
        }
    }
}
