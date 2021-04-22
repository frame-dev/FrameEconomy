package de.framedev.frameeconomy.utils;

import de.framedev.frameeconomy.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.frameeconomy.utils
 * ClassName FileManager
 * Date: 20.03.21
 * Project: FrameEconomy
 * Copyrighted by FrameDev
 */

public class FileManager {

    private final File file;
    private final FileConfiguration cfg;

    public FileManager() {
        this.file = new File(Main.getInstance().getDataFolder() + "/money", "eco.yml");
        this.cfg = YamlConfiguration.loadConfiguration(file);
    }

    private void save() {
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMoney(OfflinePlayer player, double amount) {
        if (Bukkit.getServer().getOnlineMode()) {
            cfg.set(player.getUniqueId().toString(), amount);
        } else {
            cfg.set(player.getName(), amount);
        }
        save();
    }

    public double getMoney(OfflinePlayer player) {
        if (Bukkit.getServer().getOnlineMode()) {
            return cfg.getDouble(player.getUniqueId().toString());
        } else {
            return cfg.getDouble(player.getName());
        }
    }

    public void addMoney(OfflinePlayer player, double amount) {
        double money = getMoney(player);
        money += amount;
        setMoney(player, money);
    }

    public void removeMoney(OfflinePlayer player, double amount) {
        double money = getMoney(player);
        money -= amount;
        setMoney(player, money);
    }

    public boolean has(OfflinePlayer player, double amount) {
        return getMoney(player) >= amount;
    }
}
