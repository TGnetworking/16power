package com.voidgamerz.custompowers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PowerCommands implements CommandExecutor, TabCompleter {

    private final PowerManager powers;

    public PowerCommands(PowerManager powers) {
        this.powers = powers;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("listpowers")) {
            sender.sendMessage(ChatColor.GOLD + "=== The 16 Powers ===");
            for (PowerType t : PowerType.values()) {
                sender.sendMessage(t.getColor() + " - " + t.getId() + ChatColor.GRAY + "  (" + t.getDisplayName() + ")");
            }
            return true;
        }

        if (command.getName().equalsIgnoreCase("givepower")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /givepower <player> <powerId>");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player '" + args[0] + "' is not online.");
                return true;
            }
            PowerType type = PowerType.fromId(args[1]);
            if (type == null) {
                sender.sendMessage(ChatColor.RED + "Unknown power id '" + args[1] + "'. Run /listpowers.");
                return true;
            }
            ItemStack item = powers.createItem(type);
            target.getInventory().addItem(item);
            sender.sendMessage(ChatColor.GREEN + "Gave " + target.getName() + " the power: " + type.getDisplayName());
            if (!target.equals(sender)) {
                target.sendMessage(ChatColor.GOLD + "You received a power: " + type.getDisplayName());
            }
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equalsIgnoreCase("givepower")) return List.of();

        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(partial))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            String partial = args[1].toLowerCase();
            List<String> ids = new ArrayList<>();
            for (PowerType t : PowerType.values()) {
                if (t.getId().startsWith(partial)) ids.add(t.getId());
            }
            return ids;
        }
        return List.of();
    }
}
