package dev.padrewin.teleportBowPlus.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import dev.padrewin.teleportBowPlus.Core;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class InventoryClickListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        Player player = (Player) e.getWhoClicked();

        // Verificăm dacă metadatele itemului sunt null pentru a evita erori
        if (item == null || item.getItemMeta() == null) return;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey bowKey = new NamespacedKey(Core.getInstance(), "bow_identifier");
        NamespacedKey arrowKey = new NamespacedKey(Core.getInstance(), "arrow_identifier");

        // Verificăm dacă itemul are identificatorul unic pentru arc
        if (meta.getPersistentDataContainer().has(bowKey, PersistentDataType.INTEGER)) {
            int id = meta.getPersistentDataContainer().get(bowKey, PersistentDataType.INTEGER);
            if (id == 1 && !player.hasPermission("tbp.movebow")) {
                e.setCancelled(true);  // Blocăm mutarea arcului
            }
        }

        // Verificăm dacă itemul are identificatorul unic pentru săgeți
        if (meta.getPersistentDataContainer().has(arrowKey, PersistentDataType.INTEGER)) {
            int id = meta.getPersistentDataContainer().get(arrowKey, PersistentDataType.INTEGER);
            if (id == 1 && !player.hasPermission("tbp.movearrow")) {
                e.setCancelled(true);  // Blocăm mutarea săgeților
            }
        }
    }
}