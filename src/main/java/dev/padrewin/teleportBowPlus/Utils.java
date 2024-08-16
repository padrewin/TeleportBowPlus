package dev.padrewin.teleportBowPlus;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Utils {

    public static void sendParticle(Location loc, String particleType, float offsetX, float offsetY, float offsetZ, float spread, int amount) {
        try {
            // Convertim string-ul în enum-ul Particle și trimitem particule
            Particle particle = Particle.valueOf(particleType.toUpperCase());
            loc.getWorld().spawnParticle(particle, loc, amount, offsetX, offsetY, offsetZ, spread);
        } catch (IllegalArgumentException e) {
            // Logăm un mesaj dacă tipul de particulă nu este valid
            Core.getInstance().getLogger().warning("Invalid particle type: " + particleType);
        }
    }

    public static void playSound(Player player) {
        try {
            // Convertim string-ul în enum-ul Sound și redăm sunetul
            Sound sound = Sound.valueOf(Core.getTbpManager().soundType.toUpperCase());
            player.playSound(player.getLocation(), sound, 1.5F, 1.0F);
        } catch (IllegalArgumentException e) {
            // Logăm un mesaj dacă tipul de sunet nu este valid
            Core.getInstance().getLogger().warning("Invalid sound type: " + Core.getTbpManager().soundType);
        }
    }
}
