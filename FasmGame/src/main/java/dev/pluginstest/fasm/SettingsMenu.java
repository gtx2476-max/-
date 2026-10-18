package dev.pluginstest.fasm;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public final class SettingsMenu implements InventoryHolder {

    public static final int SLOT_DIFFICULTY = 10;
    public static final int SLOT_EVIDENCE = 12;
    public static final int SLOT_MAP = 14;
    public static final int SLOT_PLAY = 16;

    private final Inventory inventory;

    public SettingsMenu() {
        this.inventory = Bukkit.createInventory(this, 27,
                Component.text("Настройка контракта", NamedTextColor.DARK_GRAY));
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void render(GameSettings s) {
        ItemStack pane = item(Material.GRAY_STAINED_GLASS_PANE, Component.text(" "), List.of());
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, pane);
        }
        inventory.setItem(SLOT_DIFFICULTY, item(Material.WITHER_SKELETON_SKULL,
                Component.text("Сложность: ", NamedTextColor.WHITE)
                        .append(Component.text(s.difficulty().title(), s.difficulty().color())),
                List.of(Component.text("Клик, чтобы сменить", NamedTextColor.GRAY))));
        inventory.setItem(SLOT_EVIDENCE, item(Material.BOOK,
                Component.text("Улики: ", NamedTextColor.WHITE)
                        .append(Component.text(String.valueOf(s.evidence()), NamedTextColor.AQUA)),
                List.of(Component.text("Сколько улик оставляет призрак", NamedTextColor.GRAY),
                        Component.text("Клик, чтобы сменить", NamedTextColor.GRAY))));
        inventory.setItem(SLOT_MAP, item(Material.COMPASS,
                Component.text("Карта: ", NamedTextColor.WHITE)
                        .append(Component.text(s.mapName(), NamedTextColor.GOLD)),
                List.of(Component.text("Клик, чтобы сменить", NamedTextColor.GRAY))));
        inventory.setItem(SLOT_PLAY, item(Material.LIME_CONCRETE,
                Component.text("ИГРАТЬ", NamedTextColor.GREEN, TextDecoration.BOLD),
                List.of(Component.text("Игроки рядом (до 8 блоков)", NamedTextColor.GRAY),
                        Component.text("отправятся с тобой", NamedTextColor.GRAY))));
    }

    private static ItemStack item(Material material, Component name, List<Component> lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(name.decoration(TextDecoration.ITALIC, false));
        meta.lore(lore.stream().map(c -> c.decoration(TextDecoration.ITALIC, false)).toList());
        stack.setItemMeta(meta);
        return stack;
    }
}
