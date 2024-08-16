package dev.padrewin.teleportBowPlus;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class LegacyParticle {

    public static void send(Location loc, String particleType, float offsetX, float offsetY, float offsetZ, float spread, int amount) {
        // Convertim tipul de particulă din string în enum-ul Particle
        Particle particle;
        try {
            particle = Particle.valueOf(particleType.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Dacă tipul de particulă nu este valid, logăm eroarea și ieșim din metodă
            Bukkit.getLogger().warning("Invalid particle type: " + particleType);
            return;
        }

        // Trimitem particule la toți jucătorii online
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.spawnParticle(particle, loc, amount, offsetX, offsetY, offsetZ, spread);
        }
    }
}
