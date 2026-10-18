package dev.pluginstest.fasm;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.Difficulty;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class FasmPlugin extends JavaPlugin {

    public static final String LOBBY_NAME = "fasm_lobby";
    public static final String GAME_PREFIX = "fasm_game_";

    private World lobby;
    private GameManager games;
    private NamespacedKey npcKey;
    private final Map<UUID, GameSettings> settings = new HashMap<>();

    @Override
    public void onEnable() {
        npcKey = new NamespacedKey(this, "fasm_npc");
        GameManager.cleanupLeftovers();

        lobby = createLobby();
        if (lobby == null) {
            getLogger().severe("Не удалось создать мир лобби. Плагин выключается.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        LobbyBuilder.buildIfNeeded(lobby);
        respawnNpc();

        games = new GameManager(this);

        PluginCommand cmd = getCommand("fasm");
        if (cmd != null) {
            FasmCommand handler = new FasmCommand(this);
            cmd.setExecutor(handler);
            cmd.setTabCompleter(handler);
        }
        getServer().getPluginManager().registerEvents(new FasmListener(this), this);

        // держим ночь: в лобби ясно, в игре дождь
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (World w : Bukkit.getWorlds()) {
                String n = w.getName();
                if (n.equals(LOBBY_NAME)) {
                    w.setTime(18000);
                    w.setStorm(false);
                } else if (n.startsWith(GAME_PREFIX)) {
                    w.setTime(18000);
                    w.setStorm(true);
                    w.setThundering(false);
                }
            }
        }, 20L, 100L);

        getLogger().info("FasmGame включён. Лобби: " + LOBBY_NAME);
    }

    @Override
    public void onDisable() {
        if (games != null) {
            games.shutdown();
        }
    }

    private World createLobby() {
        World existing = Bukkit.getWorld(LOBBY_NAME);
        World w = existing != null ? existing : new WorldCreator(LOBBY_NAME)
                .generator(new VoidGenerator())
                .generateStructures(false)
                .createWorld();
        if (w != null) {
            w.setDifficulty(Difficulty.PEACEFUL);
            w.setSpawnLocation(0, 64, 9);
        }
        return w;
    }

    public World lobby() {
        return lobby;
    }

    public GameManager games() {
        return games;
    }

    public Location lobbySpawn() {
        return new Location(lobby, 0.5, 64, 9.5, 180f, 0f);
    }

    public GameSettings settingsOf(Player p) {
        return settings.computeIfAbsent(p.getUniqueId(), id -> new GameSettings());
    }

    public void openMenu(Player p) {
        SettingsMenu menu = new SettingsMenu();
        menu.render(settingsOf(p));
        p.openInventory(menu.getInventory());
    }

    public void sendToLobby(Player p) {
        games.removePlayer(p);
        p.teleport(lobbySpawn());
        p.setGameMode(GameMode.ADVENTURE);
    }

    public boolean isNpc(Entity e) {
        return e.getPersistentDataContainer().has(npcKey, PersistentDataType.BYTE);
    }

    /** Удаляет старого NPC (если есть) и ставит нового в центре острова. */
    public void respawnNpc() {
        Location loc = new Location(lobby, 0.5, 64, 0.5, 0f, 0f);
        lobby.getChunkAt(loc).load();
        for (Entity e : lobby.getEntities()) {
            if (isNpc(e)) {
                e.remove();
            }
        }
        lobby.spawn(loc, Villager.class, v -> {
            v.customName(Component.text("Настройка контракта", NamedTextColor.GOLD));
            v.setCustomNameVisible(true);
            v.setAI(false);
            v.setInvulnerable(true);
            v.setSilent(true);
            v.setPersistent(true);
            v.setRemoveWhenFarAway(false);
            v.getPersistentDataContainer().set(npcKey, PersistentDataType.BYTE, (byte) 1);
        });
    }
}
