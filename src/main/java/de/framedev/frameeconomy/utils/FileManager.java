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

    /**
     * Save the eco.yml File
     */
    private void save() {
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param player the Player to set the Money
     * @param amount the Amount of the Money
     */
    public void setMoney(OfflinePlayer player, double amount) {
        if (Bukkit.getServer().getOnlineMode()) {
            cfg.set(player.getUniqueId().toString(), amount);
        } else {
            cfg.set(player.getName(), amount);
        }
        save();
    }

    /**
     *
     * @param player the Player to get the Money
     * @return returns the Money from the Player
     */
    public double getMoney(OfflinePlayer player) {
        if (Bukkit.getServer().getOnlineMode()) {
            return cfg.getDouble(player.getUniqueId().toString());
        } else {
            return cfg.getDouble(player.getName());
        }
    }

    /**
     *
     * @param player the Player to add Money
     * @param amount the Amount to add
     */
    public void addMoney(OfflinePlayer player, double amount) {
        double money = getMoney(player);
        money += amount;
        setMoney(player, money);
    }

    /**
     *
     * @param player the Player to remove Money
     * @param amount the Amount to remove
     */
    public void removeMoney(OfflinePlayer player, double amount) {
        double money = getMoney(player);
        money -= amount;
        setMoney(player, money);
    }

    /**
     *
     * @param player the Player
     * @param amount the Amount that requires the Player
     * @return return if the Player has the Money
     */
    public boolean has(OfflinePlayer player, double amount) {
        return getMoney(player) >= amount;
    }
}
