package dev.padrewin.teleportBowPlus.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import dev.padrewin.teleportBowPlus.Core;

public class TBPCommand implements CommandExecutor {

    private final String[] helpMessage = new String[] {
            "/tbp give <player> - Give a player a Teleport Bow",
            "/tbp reload - Reload config.yml and messages.yml"
    };

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("tbp.command")) {
            Core.getTbpManager().sendMessage(sender, "no-permission");
            return true;
        }

        if (args.length < 1) {
            sendHelpMessage(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (!sender.hasPermission("tbp.command.give")) {
                Core.getTbpManager().sendMessage(sender, "no-permission");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(Core.getTbpManager().getMessage("prefix") + " Usage: /tbp give <player>");
                return true;
            }

            // Obținem jucătorul țintă
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                ItemStack bowItem = Core.getTbpManager().bowItem;
                target.getInventory().addItem(bowItem);
                Core.getTbpManager().sendMessage(sender, "give-bow", "%player%", target.getName());
                Core.getTbpManager().sendMessage(target, "receive-bow");
            } else {
                Core.getTbpManager().sendMessage(sender, "invalid-player", "%argument%", args[1]);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("tbp.command.reload")) {
                Core.getTbpManager().sendMessage(sender, "no-permission");
                return true;
            }

            Core.getInstance().reloadConfig();
            Core.getTbpManager().loadConfigOptions(Core.getInstance());
            Core.getTbpManager().sendMessage(sender, "reload");
            return true;
        }

        sendHelpMessage(sender);
        return true;
    }

    private void sendHelpMessage(CommandSender sender) {
        for (String line : helpMessage) {
            sender.sendMessage(line);
        }
    }
}
