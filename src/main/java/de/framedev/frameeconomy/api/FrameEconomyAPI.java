package de.framedev.frameeconomy.api;

import de.framedev.frameeconomy.main.Main;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;

import java.util.List;

/**
 * This Class is the API for this Plugin [Created by FrameDev]
 * Package : de.framedev.frameeconomy.api
 * ClassName FrameEconomyAPI
 * Date: 23.04.21
 * Project: FrameEconomy
 * Copyrighted by FrameDev
 */

public class FrameEconomyAPI {

    private final Main plugin;
    private static FrameEconomyAPI instance;

    /**
     * Constructor for the API
     *
     * @param plugin Main Plugin
     */
    public FrameEconomyAPI(Main plugin) {
        this.plugin = plugin;
        instance = this;
    }

    /**
     * Get the Instance of this API
     *
     * @return return the Instance of this API
     */
    public static FrameEconomyAPI getInstance() {
        return instance;
    }

    /**
     * Updates Accounts
     *
     * @return return Registered Accounts
     */
    public List<OfflinePlayer> getAccounts() {
        return plugin.getVaultManager().getAccounts();
    }

    /**
     * Update Banks
     *
     * @return return Banks
     */
    public List<String> getBanks() {
        return plugin.getRegisterManager().getVaultProvider().banks();
    }

    /**
     * @param player the Player to create a new Account for Economy
     * @return if it was success or not / return false if Player has already an Account
     */
    public boolean createAccount(OfflinePlayer player) {
        if (!plugin.getVaultManager().getEconomy().hasAccount(player)) {
            plugin.getVaultManager().getEconomy().createPlayerAccount(player);
            return true;
        }
        return false;
    }

    public Economy getEconomy() {
        return plugin.getVaultManager().getEconomy();
    }

    public boolean depositPlayer(OfflinePlayer player, double amount) {
        return getEconomy().depositPlayer(player, amount).transactionSuccess();
    }

    public boolean withdrawPlayer(OfflinePlayer player, double amount) {
        return getEconomy().withdrawPlayer(player, amount).transactionSuccess();
    }

    public double getBalance(OfflinePlayer player) {
        return getEconomy().getBalance(player);
    }

    public boolean hasPlayerAccount(OfflinePlayer player) {
        return getEconomy().hasAccount(player);
    }

    public boolean isMySQL() {
        return plugin.isMysql();
    }

    public boolean isSQL() {
        return plugin.isSQL();
    }

    public boolean isMongoDB() {
        return plugin.isMongoDb();
    }
}
