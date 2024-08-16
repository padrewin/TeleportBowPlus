package dev.padrewin.teleportBowPlus.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import dev.padrewin.teleportBowPlus.Core;
import dev.padrewin.teleportBowPlus.Utils;

public class EntityDespawnListener implements Listener {

    @EventHandler
    public void onDespawn(EntityDeathEvent e) {
        // Verificăm dacă entitatea care a murit este un proiectil
        if (!(e.getEntity() instanceof Projectile)) {
            return;
        }

        Projectile projectile = (Projectile) e.getEntity();
        if (!(projectile.getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) projectile.getShooter();
        player.sendMessage("foekofefkeo"); // Mesaj temporar pentru debug, elimină-l dacă nu mai este necesar

        // Dacă teleportType este "ALL"
        if (Core.getTbpManager().teleportType.equals("ALL")) {
            if (Core.getTbpManager().arrowEntityLists.contains(projectile)) {
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

                // Sunet și particule, dacă sunt activate
                if (Core.getTbpManager().soundEnabled) {
                    Utils.playSound(player);
                }
                if (Core.getTbpManager().particleEnabled) {
                    Utils.sendParticle(player.getLocation(), Core.getTbpManager().particleType, 1.0F, 1.0F, 1.0F, 0.5F, 15);
                }
            }
        }
        // Dacă teleportType este "LATEST"
        else if (Core.getTbpManager().arrowEntity.containsKey(player)) {
            Entity latestArrow = Core.getTbpManager().arrowEntity.get(player);
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

            // Sunet și particule, dacă sunt activate
            if (Core.getTbpManager().soundEnabled) {
                Utils.playSound(player);
            }
            if (Core.getTbpManager().particleEnabled) {
                Utils.sendParticle(player.getLocation(), Core.getTbpManager().particleType, 1.0F, 1.0F, 1.0F, 0.5F, 15);
            }

            // Gestionăm trail-ul și despawn-ul săgeților
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
