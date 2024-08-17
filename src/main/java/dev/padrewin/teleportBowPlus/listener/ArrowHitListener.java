package dev.padrewin.teleportBowPlus.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import dev.padrewin.teleportBowPlus.Core;
import dev.padrewin.teleportBowPlus.Utils;

public class ArrowHitListener implements Listener {

    @EventHandler
    public void onHit(ProjectileHitEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Projectile projectile = e.getEntity();
        Player player = (Player) e.getEntity().getShooter();

        // Verificăm dacă jucătorul are arcul personalizat în mână
        ItemStack bow = player.getInventory().getItemInMainHand();
        if (!isCustomBow(bow)) {
            return; // Oprirea teleportării dacă arcul nu este cel personalizat
        }

        // Continuăm doar dacă arcul este cel personalizat
        if (Core.getTbpManager().teleportType.equals("ALL")) {
            scheduleTeleport(player, projectile);
        } else if (Core.getTbpManager().arrowEntity.containsKey(player)) {
            Entity latestArrow = Core.getTbpManager().arrowEntity.get(player);
            if (latestArrow.equals(projectile)) {
                scheduleTeleport(player, latestArrow);
            }
        }
    }

    private boolean isCustomBow(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(Core.getInstance(), "custom_bow_identifier");
        PersistentDataContainer container = meta.getPersistentDataContainer();

        // Verificăm dacă arcul are identificatorul nostru
        return container.has(key, PersistentDataType.STRING) &&
                "teleport_bow".equals(container.get(key, PersistentDataType.STRING));
    }

    private void scheduleTeleport(Player player, Entity projectile) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Core.getInstance(), () -> {
            teleportPlayerToSafeLocation(player, projectile);
        }, 2L); // 2 ticks delay
    }

    private void teleportPlayerToSafeLocation(Player player, Entity projectile) {
        Location entityLocation = projectile.getLocation();

        if (entityLocation == null || entityLocation.getWorld() == null) {
            return;
        }

        Location safeLocation = findSafeLocation(entityLocation);
        if (safeLocation != null) {
            player.teleport(safeLocation);
        }

        // Play sound if enabled
        if (Core.getTbpManager().soundEnabled)
            Utils.playSound(player);

        // Send particle effect if enabled
        if (Core.getTbpManager().particleEnabled)
            Utils.sendParticle(player.getLocation(), Core.getTbpManager().particleType, 1.0F, 1.0F, 1.0F, 0.5F, 15);

        // Remove the arrow if arrowDespawn is enabled
        if (Core.getTbpManager().arrowDespawn) {
            projectile.remove();
        }
    }

    private Location findSafeLocation(Location loc) {
        if (isSafe(loc)) {
            return loc;
        }

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location newLoc = loc.clone().add(x, 0, z);
                if (isSafe(newLoc)) {
                    return newLoc;
                }
            }
        }
        return null;
    }

    private boolean isSafe(Location loc) {
        return loc.getBlock().getType() == Material.AIR &&
                loc.clone().add(0, 1, 0).getBlock().getType() == Material.AIR &&
                loc.clone().add(0, -1, 0).getBlock().getType().isSolid();
    }
}
