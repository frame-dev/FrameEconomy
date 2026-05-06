package de.framedev.frameeconomy.vault;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
    private List<OfflinePlayer> accounts;

    public VaultManager(Main plugin) {
        this.accounts = new ArrayList<>();
        // Installation of Vault //
        File fileData = getEcoFile();
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
        economy = new VaultAPI();
        plugin.getServer().getServicesManager().register(Economy.class, economy, plugin, ServicePriority.High);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!economy.hasAccount(p))
                economy.createPlayerAccount(p);
        }
    }

    public void setAccounts(List<OfflinePlayer> accounts) {
        this.accounts = accounts;
    }

    public List<OfflinePlayer> getAccounts() {
        return accounts;
    }

    /**
     * Add a User to the Bank
     * @param bankName the Bank name
     * @param player the OfflinePlayer
     */
    public void addBankMember(String bankName, OfflinePlayer player) {
        if (Main.getInstance().isMysql() || Main.getInstance().isSQL()) {
            new MySQLManager().addBankMember(bankName, player);
        } else if (Main.getInstance().isMongoDb()) {
            List<String> users = (List<String>) Main.getInstance().getBackendManager().getObject("bankname", bankName, "bankmembers", "eco");
            if (users == null) users = new ArrayList<>();
            if (!users.contains(player.getName()))
                users.add(player.getName());
            Main.getInstance().getBackendManager().updateUser(player,"bankname",bankName,"eco");
            Main.getInstance().getBackendManager().updateUser(player,"bankmembers", users,"eco");
            Main.getInstance().getBackendManager().updataData("bankname", bankName, "bankmembers", users, "eco");
        } else {
            File file = getEcoFile();
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
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

    /**
     * Removing a User from the Bank
     * @param bankName the BankName
     * @param player the OfflinePlayer
     */
    public void removeBankMember(String bankName, OfflinePlayer player) {
        if (Main.getInstance().isMysql() || Main.getInstance().isSQL()) {
            new MySQLManager().removeBankMember(bankName, player);
        } else if (Main.getInstance().isMongoDb()) {
            List<String> users = (List<String>) Main.getInstance().getBackendManager().getObject("bankname", bankName, "bankmembers", "eco");
            if (users == null) users = new ArrayList<>();
            if (users.contains(player.getName())) 
                users.remove(player.getName());
            Main.getInstance().getBackendManager().updateUser(player,"bankname","","eco");
            Main.getInstance().getBackendManager().updateUser(player,"bankmembers", users,"eco");
            Main.getInstance().getBackendManager().updataData("bankname", bankName, "bankmembers", users, "eco");
        } else {
            File file = getEcoFile();
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
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

    /**
     * Return all BankMembers if the Bank exists and have BankMembers!
     * @param bankName the BankName
     * @return all BankMembers from the Bank
     */
    public List<String> getBankMembers(String bankName) {
        if (Main.getInstance().isMysql() || Main.getInstance().isSQL()) {
            List<String> members = new MySQLManager().getBankMembers(bankName);
            return members == null ? Collections.emptyList() : members;
        } else if (Main.getInstance().isMongoDb()) {
            List<String> members = (List<String>) Main.getInstance().getBackendManager().getObject("bankname",bankName,"bankmembers","eco");
            return members == null ? Collections.emptyList() : members;
        } else {
            File file = getEcoFile();
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            return cfg.getStringList("Banks." + bankName + ".members");
        }
    }

    /**
     * @return returns the registered Economy class
     */
    public Economy getEconomy() {
        return economy;
    }

    private File getEcoFile() {
        return new File(Main.getInstance().getDataFolder() + "/money", "eco.yml");
    }
}
