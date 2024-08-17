package dev.padrewin.teleportBowPlus.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import dev.padrewin.teleportBowPlus.Core;
import dev.padrewin.teleportBowPlus.Utils;

public class ArrowShootListener implements Listener {

    @EventHandler
    public void onShoot(EntityShootBowEvent e) {
        if (!(e.getEntity() instanceof Player) || Core.getTbpManager().isWorldBlacklisted(e.getEntity().getWorld())) {
            return;
        }

        Player player = (Player) e.getEntity();
        ItemStack bow = player.getItemInHand();

        // Set the bow to unbreakable and hide its tags and attributes
        if (bow != null && bow.getType() == Material.BOW) {
            ItemMeta meta = bow.getItemMeta();
            if (meta != null) {
                meta.setUnbreakable(true); // Set the bow as unbreakable
                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE); // Hide the unbreakable tag
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES); // Hide attributes like damage
                bow.setItemMeta(meta); // Apply the changes to the bow
            }
        }

        // Handle arrow particle trails
        if (Core.getTbpManager().trailEnabled) {
            int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.getInstance(), () ->
                            Utils.sendParticle(e.getProjectile().getLocation(), Core.getTbpManager().trailParticleType, 0.0F, 0.0F, 0.0F, 0.0F, 1),
                    0L, Core.getTbpManager().trailInterval);

            Core.getTbpManager().arrowTrails.put(e.getProjectile(), task);
        }

        // Handle teleport type
        if (Core.getTbpManager().teleportType.equals("ALL")) {
            Core.getTbpManager().arrowEntityLists.add(e.getProjectile());
        } else {
            Core.getTbpManager().arrowEntity.put(player, e.getProjectile());
        }
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Arrow)) return;

        Arrow arrow = (Arrow) e.getEntity();

        // Cancel the particle trail when the arrow hits something
        Integer taskId = Core.getTbpManager().arrowTrails.get(arrow);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
            Core.getTbpManager().arrowTrails.remove(arrow);
        }

        // Teleport the player to the arrow's location if the arrow is shot by a player
        if (arrow.getShooter() instanceof Player) {
            Player player = (Player) arrow.getShooter();

            if (Core.getTbpManager().teleportType.equals("ALL") || Core.getTbpManager().arrowEntity.get(player) == arrow) {
                player.teleport(arrow.getLocation());
                Core.getTbpManager().arrowEntity.remove(player); // Remove arrow reference after teleportation
            }
        }
    }
}
