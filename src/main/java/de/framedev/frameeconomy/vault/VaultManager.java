package de.framedev.frameeconomy.vault;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.framedev.frameeconomy.mongodb.BackendManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.ServicePriority;

import de.framedev.frameeconomy.main.Main;
import de.framedev.frameeconomy.utils.FileManager;
import net.milkbowl.vault.economy.Economy;

public class VaultManager {

    private final Economy economy;
    private List<OfflinePlayer> accounts;

    public VaultManager(Main plugin) {
        this.accounts = new ArrayList<>();
        if (plugin.isYamlFileStorage()) {
            File fileData = getEcoFile();
            FileConfiguration cfgData = YamlConfiguration.loadConfiguration(fileData);
            if (!fileData.exists()) {
                if(!fileData.getParentFile().mkdirs())
                    logger(plugin).log(Level.SEVERE, "Could not create economy directory: " + fileData.getParentFile().getAbsolutePath());
                try {
                    if(!fileData.createNewFile())
                        logger(plugin).log(Level.SEVERE, "Could not create economy file: " + fileData.getAbsolutePath());
                } catch (IOException e) {
                    logger(plugin).log(Level.SEVERE, "Could not create economy file: " + fileData.getAbsolutePath(), e);
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
                        logger(plugin).log(Level.WARNING, "Could not write initial accounts to " + fileData.getAbsolutePath(), e);
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
                        logger(plugin).log(Level.WARNING, "Could not write initial accounts to " + fileData.getAbsolutePath(), e);
                    }
                }
            }
        }
        economy = new VaultAPI();
        plugin.getServer().getServicesManager().register(Economy.class, economy, plugin, ServicePriority.High);
        OfflinePlayer[] onlinePlayers = Bukkit.getOnlinePlayers().toArray(new OfflinePlayer[0]);
        plugin.runDatabaseAsync(() -> {
            for (OfflinePlayer player : onlinePlayers) {
                if (!economy.hasAccount(player))
                    economy.createPlayerAccount(player);
            }
        });
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
        if (Main.getInstance().isSqlStorage()) {
            new SQLManager().addBankMember(bankName, player);
        } else if (Main.getInstance().isMongoDb()) {
            BackendManager backendManager = getBackendManager().orElse(null);
            if (backendManager == null) return;
            List<String> users = getBankMembersFromBackend(bankName);
            if (!users.contains(player.getName()))
                users.add(player.getName());
            backendManager.updateUser(player,"bankname",bankName,"eco");
            backendManager.updateUser(player,"bankmembers", users,"eco");
            backendManager.updataData("bankname", bankName, "bankmembers", users, "eco");
        } else {
            new FileManager().addBankMember(bankName, player.getName());
        }
    }

    /**
     * Removing a User from the Bank
     * @param bankName the BankName
     * @param player the OfflinePlayer
     */
    public void removeBankMember(String bankName, OfflinePlayer player) {
        if (Main.getInstance().isSqlStorage()) {
            new SQLManager().removeBankMember(bankName, player);
        } else if (Main.getInstance().isMongoDb()) {
            BackendManager backendManager = getBackendManager().orElse(null);
            if (backendManager == null) return;
            List<String> users = getBankMembersFromBackend(bankName);
            users.remove(player.getName());
            backendManager.updateUser(player,"bankname","","eco");
            backendManager.updateUser(player,"bankmembers", users,"eco");
            backendManager.updataData("bankname", bankName, "bankmembers", users, "eco");
        } else {
            new FileManager().removeBankMember(bankName, player.getName());
        }
    }

    private Optional<BackendManager> getBackendManager() {
        return Optional.ofNullable(Main.getInstance().getBackendManager());
    }

    /**
     * Return all BankMembers if the Bank exists and have BankMembers!
     * @param bankName the BankName
     * @return all BankMembers from the Bank
     */
    public List<String> getBankMembers(String bankName) {
        if (Main.getInstance().isSqlStorage()) {
            List<String> members = new SQLManager().getBankMembers(bankName);
            return members == null ? Collections.emptyList() : members;
        } else if (Main.getInstance().isMongoDb()) {
            BackendManager backendManager = Main.getInstance().getBackendManager();
            if (backendManager == null) return Collections.emptyList();
            Object obj = backendManager.getObject("bankname",bankName,"bankmembers","eco");
            List<String> members = obj != null ? toStringList(obj) : null;
            return members == null ? Collections.emptyList() : members;
        } else {
            return new FileManager().getBankMembers(bankName);
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

    private static Logger logger(Main plugin) {
        return plugin != null ? plugin.getLogger() : Logger.getLogger(VaultManager.class.getName());
    }

    private static List<String> toStringList(Object raw) {
        if (!(raw instanceof List<?>)) {
            return null;
        }
        List<?> list = (List<?>) raw;
        List<String> values = new ArrayList<>();
        for (Object entry : list) {
            if (entry instanceof String) {
                values.add((String) entry);
            }
        }
        return values;
    }

    private List<String> getBankMembersFromBackend(String bankName) {
        BackendManager backendManager = getBackendManager().orElse(null);
        if (backendManager == null) return new ArrayList<>();
        Object obj = backendManager.getObject("bankname", bankName, "bankmembers", "eco");
        return obj != null ? toStringList(obj) : new ArrayList<>();
    }
}
