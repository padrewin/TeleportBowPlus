package dev.padrewin.teleportBowPlus.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import dev.padrewin.teleportBowPlus.Core;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (Core.getTbpManager().bowOnJoin && player.hasPermission("tbp.bowonjoin")) {
            ItemStack bowItem = Core.getTbpManager().bowItem;
            ItemMeta meta = bowItem.getItemMeta();

            // Setăm identificatorul unic pentru arc
            NamespacedKey bowKey = new NamespacedKey(Core.getInstance(), "bow_identifier");
            meta.getPersistentDataContainer().set(bowKey, PersistentDataType.INTEGER, 1);

            bowItem.setItemMeta(meta);
            player.getInventory().setItem(Core.getTbpManager().bowSlot, bowItem);
        }

        if (Core.getTbpManager().arrowOnJoin && player.hasPermission("tbp.arrowonjoin")) {
            ItemStack arrowItem = Core.getTbpManager().arrowItem;
            ItemMeta meta = arrowItem.getItemMeta();

            // Setăm identificatorul unic pentru săgeți
            NamespacedKey arrowKey = new NamespacedKey(Core.getInstance(), "arrow_identifier");
            meta.getPersistentDataContainer().set(arrowKey, PersistentDataType.INTEGER, 1);

            arrowItem.setItemMeta(meta);
            player.getInventory().setItem(Core.getTbpManager().arrowSlot, arrowItem);
        }
    }
}
