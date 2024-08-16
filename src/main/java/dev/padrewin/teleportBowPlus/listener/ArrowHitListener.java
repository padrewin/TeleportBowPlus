package dev.padrewin.teleportBowPlus.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import dev.padrewin.teleportBowPlus.Core;
import dev.padrewin.teleportBowPlus.Utils;

public class ArrowHitListener implements Listener {

    @EventHandler
    public void onHit(ProjectileHitEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player))
            return;

        Projectile projectile = e.getEntity();
        Player player = (Player) e.getEntity().getShooter();

        NamespacedKey arrowKey = new NamespacedKey(Core.getInstance(), "arrow_identifier");

        if (Core.getTbpManager().teleportType.equals("ALL")) {
            if (projectile.getPersistentDataContainer().has(arrowKey, PersistentDataType.INTEGER)) {
                player.sendMessage("Teleporting...");
                Location playerLocation = player.getLocation();
                Location entityLocation = projectile.getLocation();
                Location teleportLocation = new Location(
                        entityLocation.getWorld(),
                        entityLocation.getX(),
                        entityLocation.getY(),
                        entityLocation.getZ(),
                        playerLocation.getYaw(),
                        playerLocation.getPitch()
                );
                player.teleport(teleportLocation);

                if (Core.getTbpManager().soundEnabled)
                    Utils.playSound(player);

                if (Core.getTbpManager().particleEnabled)
                    Utils.sendParticle(player.getLocation(), Core.getTbpManager().particleType, 1.0F, 1.0F, 1.0F, 0.5F, 15);
            }

        } else if (Core.getTbpManager().arrowEntity.containsKey(player)) {
            Entity latestArrow = Core.getTbpManager().arrowEntity.get(player);

            if (latestArrow.getPersistentDataContainer().has(arrowKey, PersistentDataType.INTEGER)) {
                Location playerLocation = player.getLocation();
                Location arrowLocation = latestArrow.getLocation();
                Location teleportLocation = new Location(
                        arrowLocation.getWorld(),
                        arrowLocation.getX(),
                        arrowLocation.getY(),
                        arrowLocation.getZ(),
                        playerLocation.getYaw(),
                        playerLocation.getPitch()
                );
                player.teleport(teleportLocation);

                if (Core.getTbpManager().soundEnabled)
                    Utils.playSound(player);

                if (Core.getTbpManager().particleEnabled)
                    Utils.sendParticle(player.getLocation(), Core.getTbpManager().particleType, 1.0F, 1.0F, 1.0F, 0.5F, 15);

                if (Core.getTbpManager().trailEnabled && Core.getTbpManager().arrowTrails.containsKey(projectile)) {
                    int taskId = Core.getTbpManager().arrowTrails.get(projectile);
                    Bukkit.getScheduler().cancelTask(taskId);
                    Core.getTbpManager().arrowTrails.remove(projectile);
                }

                if (Core.getTbpManager().arrowDespawn) {
                    projectile.remove();
                }
            }
        }
    }
}
