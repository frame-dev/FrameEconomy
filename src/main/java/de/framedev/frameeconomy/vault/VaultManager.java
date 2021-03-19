package de.framedev.frameeconomy.vault;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;

import de.framedev.frameeconomy.main.Main;
import net.milkbowl.vault.economy.Economy;

public class VaultManager {

    private final Economy economy;

    public VaultManager(Main plugin) {
        File fileData = new File(Main.getInstance().getDataFolder() + "/money", "eco.yml");
        FileConfiguration cfgData = YamlConfiguration.loadConfiguration(fileData);
        if (!fileData.exists()) {
            fileData.getParentFile().mkdirs();
            try {
                fileData.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (Bukkit.getServer().getOnlineMode()) {
            if (!cfgData.contains("accounts")) {
                ArrayList<String> accounts = new ArrayList<>();
                accounts.add("2f8f4d80-277a-4ee0-9224-3257e88ba0dc");
                cfgData.set("accounts", accounts);
                try {
                    cfgData.save(fileData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (!cfgData.contains("accounts")) {
                ArrayList<String> accounts = new ArrayList<>();
                accounts.add("FramePlays");
                cfgData.set("accounts", accounts);
                try {
                    cfgData.save(fileData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        plugin.getServer().getServicesManager().register(Economy.class, new VaultAPI(), plugin, ServicePriority.High);
        economy = new VaultAPI();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!economy.hasAccount(p))
                economy.createPlayerAccount(p);
        }
    }

    File file = new File(Main.getInstance().getDataFolder() + "/money", "eco.yml");
    FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
    public void addBankMember(String bankName, OfflinePlayer player) {
        if (Main.getInstance().isMysql() || Main.getInstance().isSQL()) {
            new MySQLManager().addBankMember(bankName, player);
        } else if (Main.getInstance().isMongoDb()) {
            List<String> users = (List<String>) Main.getInstance().getBackendManager().getObject("bankname", bankName, "bankmembers", "eco");
            if (!users.contains(player.getName()))
                users.add(player.getName());
            Main.getInstance().getBackendManager().updateUser(player,"bankname",bankName,"eco");
            Main.getInstance().getBackendManager().updateUser(player,"bankmembers", users,"eco");
            Main.getInstance().getBackendManager().updataData("bankname", bankName, "bankmembers", users, "eco");
        } else {
            try {
                cfg.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
            if (!cfg.contains("Banks." + bankName + ".members")) {
                List<String> players = new ArrayList<>();
                if (!players.contains(player.getName()))
                    players.add(player.getName());
                cfg.set("Banks." + bankName + ".members", players);
            } else {
                List<String> players = cfg.getStringList("Banks." + bankName + ".members");
                if (!players.contains(player.getName()))
                    players.add(player.getName());
                cfg.set("Banks." + bankName + ".members", players);
            }
            try {
                cfg.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeBankMember(String bankName, OfflinePlayer player) {
        if (Main.getInstance().isMysql() || Main.getInstance().isSQL()) {
            new MySQLManager().removeBankMember(bankName, player);
        } else if (Main.getInstance().isMongoDb()) {
            List<String> users = (List<String>) Main.getInstance().getBackendManager().getObject("bankname", bankName, "bankmembers", "eco");
            if (users.contains(player.getName())) 
                users.remove(player.getName());
            Main.getInstance().getBackendManager().updateUser(player,"bankname","","eco");
            Main.getInstance().getBackendManager().updateUser(player,"bankmembers", users,"eco");
            Main.getInstance().getBackendManager().updataData("bankname", bankName, "bankmembers", users, "eco");
        } else {
            try {
                cfg.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
            if (!cfg.contains("Banks." + bankName + ".members")) {
            } else {
                List<String> players = cfg.getStringList("Banks." + bankName + ".members");
                if (players.contains(player.getName()))
                    players.remove(player.getName());
                cfg.set("Banks." + bankName + ".members", players);
            }
            try {
                cfg.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> getBankMembers(String bankName) {
        if (Main.getInstance().isMysql() || Main.getInstance().isSQL()) {
            return new MySQLManager().getBankMembers(bankName);
        } else if (Main.getInstance().isMongoDb()) {
            return (List<String>) Main.getInstance().getBackendManager().getObject("bankname",bankName,"bankmembers","eco");
        } else {
            return cfg.getStringList("Banks." + bankName + ".members");
        }
    }


    public Economy getEconomy() {
        return economy;
    }
}
