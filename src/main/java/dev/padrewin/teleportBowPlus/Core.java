package dev.padrewin.teleportBowPlus;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import dev.padrewin.teleportBowPlus.command.TBPCommand;
import dev.padrewin.teleportBowPlus.listener.ArrowHitListener;
import dev.padrewin.teleportBowPlus.listener.ArrowShootListener;
import dev.padrewin.teleportBowPlus.listener.EntityDespawnListener;
import dev.padrewin.teleportBowPlus.listener.PlayerJoinListener;
import dev.padrewin.teleportBowPlus.listener.ItemDropListener;
import dev.padrewin.teleportBowPlus.listener.InventoryClickListener;

public class Core extends JavaPlugin {

    private static Core core;
    private static TBPManager tbpManager;

    @Override
    public void onEnable() {
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "&8[&dTeleportBowPlus&8] &aEnabling plugin."));

        core = this;
        tbpManager = new TBPManager();

        new Metrics(this, 11058);

        setDefaultMessages();
        saveDefaultConfig();
        tbpManager.loadConfigOptions(this);

        // Înregistrare evenimente
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new ArrowHitListener(), this);
        getServer().getPluginManager().registerEvents(new ArrowShootListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDespawnListener(), this);
        getServer().getPluginManager().registerEvents(new ItemDropListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);

        // Înregistrare comenzi
        Objects.requireNonNull(getCommand("teleportbowplus")).setExecutor(new TBPCommand());
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "&8[&dTeleportBowPlus&8] &cDisabling plugin."));
    }

    public static Core getInstance() {
        return core;
    }

    public static TBPManager getTbpManager() {
        return tbpManager;
    }

    private void setDefaultMessage(String name, String message) {
        File messagesFile = new File(getDataFolder(), "messages.yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(messagesFile);

        // Setăm mesajul dacă nu este deja setat
        if (!yamlConfiguration.isSet(name)) {
            yamlConfiguration.set(name, message);
            try {
                yamlConfiguration.save(messagesFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setDefaultMessages() {
        setDefaultMessage("prefix", "&8[&dTBP&8] ");
        setDefaultMessage("no-permission", "&cAccess denied.");
        setDefaultMessage("receive-bow", "&7You have been given a &eTeleport Bow&7.");
        setDefaultMessage("give-bow", "&7You have given &e%player% &7a &eTeleport Bow&7.");
        setDefaultMessage("reload", "&7All files have been reloaded.");
        setDefaultMessage("invalid-player", "&cCould not find the player '%argument%'.");
    }
}
