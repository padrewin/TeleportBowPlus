package dev.padrewin.teleportBowPlus.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import dev.padrewin.teleportBowPlus.Core;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ItemDropListener implements Listener {
    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        ItemStack item = e.getItemDrop().getItemStack();
        Player player = e.getPlayer();

        // Verificăm dacă metadatele itemului sunt null pentru a evita erori
        if (item.getItemMeta() == null) return;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey bowKey = new NamespacedKey(Core.getInstance(), "bow_identifier");
        NamespacedKey arrowKey = new NamespacedKey(Core.getInstance(), "arrow_identifier");

        // Verificăm dacă itemul are identificatorul unic pentru arc
        if (meta.getPersistentDataContainer().has(bowKey, PersistentDataType.INTEGER)) {
            int id = meta.getPersistentDataContainer().get(bowKey, PersistentDataType.INTEGER);
            if (id == 1 && !player.hasPermission("tbp.dropbow")) {
                e.setCancelled(true);  // Blocăm drop-ul arcului
            }
        }

        // Verificăm dacă itemul are identificatorul unic pentru săgeată
        if (meta.getPersistentDataContainer().has(arrowKey, PersistentDataType.INTEGER)) {
            int id = meta.getPersistentDataContainer().get(arrowKey, PersistentDataType.INTEGER);
            if (id == 1 && !player.hasPermission("tbp.droparrow")) {
                e.setCancelled(true);  // Blocăm drop-ul săgeții
            }
        }
    }
}