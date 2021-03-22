package de.framedev.frameeconomy.utils;

import de.framedev.frameeconomy.main.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.frameeconomy.utils
 * ClassName ConfigUtils
 * Date: 20.03.21
 * Project: FrameEconomy
 * Copyrighted by FrameDev
 */

public class ConfigUtils {
    /**
     * Reload the Config.yml
     * @throws UnsupportedEncodingException error
     */
    public void reloadCustomConfig() throws UnsupportedEncodingException {
        // Look for defaults in the jar
        Reader defConfigStream = new InputStreamReader(Objects.requireNonNull(Main.getInstance().getResource("config.yml")), StandardCharsets.UTF_8);
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        Main.getInstance().getConfig().setDefaults(defConfig);
    }

    private File configFile;
    private FileConfiguration configCfg;

    /**
     * Saves the Default Config Sections from the Config.yml
     */
    public void saveDefaultConfigValues() {
        configFile = new File(Main.getInstance().getDataFolder() + "config.yml");
        configCfg = YamlConfiguration.loadConfiguration(configFile);
        //Defaults in jar
        Reader defConfigStream;
        defConfigStream = new InputStreamReader(Objects.requireNonNull(Main.getInstance().getResource("config.yml")), StandardCharsets.UTF_8);
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        configCfg.setDefaults(defConfig);
        //Copy default values
        configCfg.options().copyDefaults(true);
        Main.getInstance().saveConfig();
        //OR use this to copy default values
        //this.saveDefaultConfig();
    }
}
