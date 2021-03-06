package de.framedev.frameeconomy.vault;

import de.framedev.frameeconomy.main.Main;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VaultManager {

    private final Economy eco;
    public VaultManager(Main plugin) {
        File fileData = new File(Main.getInstance().getDataFolder() + "/money","eco.yml");
        FileConfiguration cfgData = YamlConfiguration.loadConfiguration(fileData);
        if(!fileData.exists()) {
            fileData.getParentFile().mkdirs();
            try {
                fileData.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(Bukkit.getServer().getOnlineMode()) {
            if(!cfgData.contains("accounts")) {
                ArrayList<String> accounts = new ArrayList<>();
                accounts.add("2f8f4d80-277a-4ee0-9224-3257e88ba0dc");
                cfgData.set("accounts",accounts);
                try {
                    cfgData.save(fileData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if(!cfgData.contains("accounts")) {
                ArrayList<String> accounts = new ArrayList<>();
                accounts.add("FramePlays");
                cfgData.set("accounts",accounts);
                try {
                    cfgData.save(fileData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        plugin.getServer().getServicesManager().register(Economy.class,new VaultAPI(),plugin, ServicePriority.High);
        eco = new VaultAPI();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!eco.hasAccount(p.getName()))
                eco.createPlayerAccount(p.getName());
        }
    }

    public Economy getEco() {
        return eco;
    }
}
