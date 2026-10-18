package dev.pluginstest.fasm;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

public final class FasmListener implements Listener {

    private final FasmPlugin plugin;

    public FasmListener(FasmPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onNpcInteract(PlayerInteractEntityEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Entity clicked = e.getRightClicked();
        if (clicked instanceof Villager && plugin.isNpc(clicked)) {
            e.setCancelled(true);
            plugin.openMenu(e.getPlayer());
        }
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof SettingsMenu menu)) {
            return;
        }
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player p)) {
            return;
        }
        if (e.getClickedInventory() != e.getInventory()) {
            return;
        }
        GameSettings s = plugin.settingsOf(p);
        switch (e.getRawSlot()) {
            case SettingsMenu.SLOT_DIFFICULTY -> {
                s.cycleDifficulty();
                menu.render(s);
            }
            case SettingsMenu.SLOT_EVIDENCE -> {
                s.cycleEvidence();
                menu.render(s);
            }
            case SettingsMenu.SLOT_MAP -> {
                s.cycleMap();
                menu.render(s);
            }
            case SettingsMenu.SLOT_PLAY -> {
                p.closeInventory();
                plugin.games().start(p, s);
            }
            default -> {
            }
        }
    }

    @EventHandler
    public void onMenuDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof SettingsMenu) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        plugin.games().removePlayer(e.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        if (e.getFrom().getName().startsWith(FasmPlugin.GAME_PREFIX)
                && !e.getPlayer().getWorld().equals(e.getFrom())) {
            plugin.games().removePlayer(e.getPlayer());
        }
    }

    // защита лобби
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (inLobby(e.getPlayer()) && !e.getPlayer().hasPermission("fasm.admin")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (inLobby(e.getPlayer()) && !e.getPlayer().hasPermission("fasm.admin")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p && inLobby(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player p && inLobby(p)) {
            e.setCancelled(true);
        }
    }

    private boolean inLobby(Player p) {
        return p.getWorld().getName().equals(FasmPlugin.LOBBY_NAME);
    }
}
