package dev.pluginstest.fasm;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class FasmCommand implements CommandExecutor, TabCompleter {

    private final FasmPlugin plugin;

    public FasmCommand(FasmPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Команда только для игроков.");
            return true;
        }
        String sub = args.length == 0 ? "" : args[0].toLowerCase();
        switch (sub) {
            case "", "lobby", "leave" -> {
                plugin.sendToLobby(p);
                p.sendMessage(Component.text(
                        "Ты в лобби. Подойди к NPC и нажми ПКМ, чтобы настроить игру.", NamedTextColor.GRAY));
            }
            case "npc" -> {
                if (!p.hasPermission("fasm.admin")) {
                    p.sendMessage(Component.text("Нет прав.", NamedTextColor.RED));
                    return true;
                }
                plugin.respawnNpc();
                p.sendMessage(Component.text("NPC пересоздан.", NamedTextColor.GREEN));
            }
            default -> p.sendMessage(Component.text("Использование: /fasm [lobby|leave|npc]", NamedTextColor.RED));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        if (args.length == 1) {
            for (String s : List.of("lobby", "leave", "npc")) {
                if (s.startsWith(args[0].toLowerCase())) {
                    out.add(s);
                }
            }
        }
        return out;
    }
}
