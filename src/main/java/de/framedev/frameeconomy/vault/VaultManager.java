package de.framedev.frameeconomy.vault;

import de.framedev.frameeconomy.main.Main;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VaultManager {

    private final Economy eco;

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
        eco = new VaultAPI();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!eco.hasAccount(p.getName()))
                eco.createPlayerAccount(p.getName());
        }
    }

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
        }
    }

    public List<String> getBankMembers(String bankName) {
        if (Main.getInstance().isMysql() || Main.getInstance().isSQL()) {
            return new MySQLManager().getBankMembers(bankName);
        } else if (Main.getInstance().isMongoDb()) {
            return (List<String>) Main.getInstance().getBackendManager().getObject("bankname",bankName,"bankmembers","eco");
        }
        return null;
    }


    public Economy getEco() {
        return eco;
    }
}
