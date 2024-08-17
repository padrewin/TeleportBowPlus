package dev.padrewin.teleportBowPlus;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TBPManager {
    public List<Entity> arrowEntityLists;
    public HashMap<Player, Entity> arrowEntity;
    public HashMap<Entity, Integer> arrowTrails;
    public String teleportType;
    public boolean trailEnabled;
    public boolean soundEnabled;
    public boolean particleEnabled;
    public String soundType;
    public String particleType;
    public String trailParticleType;
    public boolean bowOnJoin;
    public boolean arrowOnJoin;
    public int bowSlot;
    public int arrowSlot;
    public int arrowAmount;
    public int trailInterval;
    public String bowDisplayName;
    public String arrowDisplayName;
    public List<String> arrowLore;
    public List<String> bowLore;
    public List<String> blacklistedWorlds;
    public boolean arrowDespawn;
    public boolean arrowGlow;
    public boolean bowGlow;
    public boolean bowInfinity;
    public boolean bowUnbreakable;
    public boolean denyBowDrop = false;
    public boolean denyArrowDrop = false;
    public boolean denyBowMove = false;
    public boolean denyArrowMove = false;
    public ItemStack bowItem;
    public ItemStack arrowItem;
    public HashMap<String, String> messages = new HashMap<>();

    public void loadConfigOptions(Core core) {
        this.teleportType = core.getConfig().getString("teleport-to");
        if ("ALL".equals(this.teleportType)) {
            this.arrowEntityLists = new ArrayList<>();
        } else if ("LATEST".equals(this.teleportType)) {
            this.arrowEntity = new HashMap<>();
        }
        this.soundEnabled = core.getConfig().getBoolean("teleport-sound.enabled");
        this.particleEnabled = core.getConfig().getBoolean("teleport-particle.enabled");
        if (this.soundEnabled) {
            this.soundType = core.getConfig().getString("teleport-sound.sound");
        }
        if (this.particleEnabled) {
            this.particleType = core.getConfig().getString("teleport-particle.particle");
        }
        this.bowOnJoin = core.getConfig().getBoolean("bow.on-join.give");
        this.arrowOnJoin = core.getConfig().getBoolean("arrow.on-join.give");
        if (this.bowOnJoin) {
            this.bowSlot = core.getConfig().getInt("bow.on-join.inventory-slot");
            this.denyBowDrop = core.getConfig().getBoolean("bow.on-join.deny-drop");
            this.denyBowMove = core.getConfig().getBoolean("bow.on-join.deny-move");
        }
        if (this.arrowOnJoin) {
            this.arrowSlot = core.getConfig().getInt("arrow.on-join.inventory-slot");
            this.arrowAmount = core.getConfig().getInt("arrow.on-join.amount");
            this.denyArrowDrop = core.getConfig().getBoolean("arrow.on-join.deny-drop");
            this.denyArrowMove = core.getConfig().getBoolean("arrow.on-join.deny-move");
        }
        this.bowDisplayName = Objects.requireNonNull(core.getConfig().getString("bow.display-name")).replace('&', '§');
        this.arrowDisplayName = Objects.requireNonNull(core.getConfig().getString("arrow.display-name")).replace('&', '§');
        this.bowLore = replaceColorCodes(core.getConfig().getStringList("bow.lore"));
        this.arrowLore = replaceColorCodes(core.getConfig().getStringList("arrow.lore"));
        this.bowGlow = core.getConfig().getBoolean("bow.glowing");
        this.arrowGlow = core.getConfig().getBoolean("arrow.glowing");
        this.bowInfinity = core.getConfig().getBoolean("bow.infinity");
        this.bowUnbreakable = core.getConfig().getBoolean("bow.unbreakable");
        this.blacklistedWorlds = core.getConfig().getStringList("blacklisted-worlds");
        this.trailEnabled = core.getConfig().getBoolean("arrow.particle-trail.enabled");
        this.arrowDespawn = core.getConfig().getBoolean("arrow.despawn");
        if (this.trailEnabled) {
            this.arrowTrails = new HashMap<>();
            this.trailParticleType = core.getConfig().getString("arrow.particle-trail.particle");
            this.trailInterval = core.getConfig().getInt("arrow.particle-trail.interval");
        }
        File messagesFile = new File(core.getDataFolder(), "messages.yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(messagesFile);
        for (String message : Objects.requireNonNull(yamlConfiguration.getConfigurationSection("")).getKeys(false)) {
            this.messages.put(message, Objects.requireNonNull(yamlConfiguration.getString(message)).replace('&', '§'));
        }
        setBowItem();
        setArrowItem();
    }

    private List<String> replaceColorCodes(List<String> list) {
        List<String> result = new ArrayList<>();
        for (String line : list) {
            result.add(line.replace('&', '§'));
        }
        return result;
    }

    private void setBowItem() {
        ItemStack item = new ItemStack(Material.BOW);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(this.bowDisplayName);
            meta.setLore(this.bowLore);
            if (this.bowGlow) {
                meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
            }
            if (this.bowInfinity) {
                meta.addEnchant(Enchantment.INFINITY, 1, true);
            }
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
            if (this.bowUnbreakable) {
                meta.setUnbreakable(true);
            }

            // Setăm identificatorul unic pentru arc
            NamespacedKey bowKey = new NamespacedKey(Core.getInstance(), "custom_bow_identifier");
            meta.getPersistentDataContainer().set(bowKey, PersistentDataType.STRING, "teleport_bow");

            item.setItemMeta(meta);  // Aplicăm modificările
        }
        this.bowItem = item;
    }

    private void setArrowItem() {
        ItemStack item = new ItemStack(Material.ARROW);
        item.setAmount(this.arrowAmount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(this.arrowDisplayName);
            meta.setLore(this.arrowLore);
            if (this.arrowGlow) {
                meta.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
            }
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        this.arrowItem = item;
    }

    public boolean isWorldBlacklisted(World world) {
        return this.blacklistedWorlds.contains(world.getName());
    }

    public String getMessage(String identifier) {
        return this.messages.getOrDefault(identifier, "");
    }

    public void sendMessage(Player p, String identifier) {
        String message = getMessage(identifier);
        if (!message.isEmpty()) {
            p.sendMessage(getMessage("prefix") + message);
        }
    }

    public void sendMessage(CommandSender sender, String identifier) {
        String message = getMessage(identifier);
        if (!message.isEmpty()) {
            sender.sendMessage(getMessage("prefix") + message);
        }
    }

    public void sendMessage(CommandSender sender, String identifier, String placeholder, String replacement) {
        String message = getMessage(identifier);
        if (!message.isEmpty()) {
            message = message.replace(placeholder, replacement);
            sender.sendMessage(getMessage("prefix") + message);
        }
    }
}
