package dev.padrewin.teleportBowPlus.listener;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import dev.padrewin.teleportBowPlus.Core;
import dev.padrewin.teleportBowPlus.Utils;

public class ArrowShootListener implements Listener {

    @EventHandler
    public void onShoot(EntityShootBowEvent e) {
        // Verificăm dacă entitatea este un jucător și dacă lumea este blacklistată
        if (!(e.getEntity() instanceof Player) || Core.getTbpManager().isWorldBlacklisted(e.getEntity().getWorld())) {
            return;
        }

        Player player = (Player) e.getEntity();
        ItemStack bow = player.getInventory().getItemInMainHand(); // Utilizăm getItemInMainHand() pentru compatibilitate cu versiunile noi

        // Verificăm dacă arcul are meta date valide
        if (bow == null || bow.getItemMeta() == null) {
            return;
        }

        ItemMeta meta = bow.getItemMeta();
        NamespacedKey bowKey = new NamespacedKey(Core.getInstance(), "bow_identifier");

        // Verificăm dacă arcul are identificatorul unic setat
        if (meta.getPersistentDataContainer().has(bowKey, PersistentDataType.INTEGER)) {
            int id = meta.getPersistentDataContainer().get(bowKey, PersistentDataType.INTEGER);

            if (id == 1) {
                // Verificăm dacă arcul este setat ca fiind indestructibil și resetăm durabilitatea
                if (Core.getTbpManager().bowUnbreakable) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Core.getInstance(), () -> {
                        player.getInventory().getItemInMainHand().setDurability((short) 0); // Resetăm durabilitatea
                        player.updateInventory(); // Actualizăm inventarul pentru a reflecta modificarea
                    });
                }

                // Gestionăm trail-ul de particule pentru săgeată
                if (Core.getTbpManager().trailEnabled) {
                    int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.getInstance(),
                            () -> Utils.sendParticle(e.getProjectile().getLocation(), Core.getTbpManager().trailParticleType, 0.0F, 0.0F, 0.0F, 0.0F, 1),
                            0L, Core.getTbpManager().trailInterval);

                    Core.getTbpManager().arrowTrails.put(e.getProjectile(), task);
                }

                // Gestionăm entitatea săgeții în funcție de tipul de teleportare
                if (Core.getTbpManager().teleportType.equals("ALL")) {
                    Core.getTbpManager().arrowEntityLists.add(e.getProjectile());
                } else {
                    Core.getTbpManager().arrowEntity.put(player, e.getProjectile());
                }
            }
        }
    }
}
