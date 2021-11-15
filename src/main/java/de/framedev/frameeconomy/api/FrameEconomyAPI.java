package de.framedev.frameeconomy.api;

import de.framedev.frameeconomy.main.Main;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;

import java.util.List;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.frameeconomy.api
 * ClassName FrameEconomyAPI
 * Date: 23.04.21
 * Project: FrameEconomy
 * Copyrighted by FrameDev
 */

public class FrameEconomyAPI {

    private final Main plugin;
    private static FrameEconomyAPI instance;

    public FrameEconomyAPI(Main plugin) {
        this.plugin = plugin;
        instance = this;
    }

    /**
     * Get the Instance of this API
     * @return return the Instance of this API
     */
    public static FrameEconomyAPI getInstance() {
        return instance;
    }

    /**
     * Updates Accounts
     * @return return Registered Accounts
     */
    public List<OfflinePlayer> accounts() {
        return plugin.getVaultManager().getAccounts();
    }

    /**
     * Update Banks
     * @return return Banks
     */
    public List<String> banks() {
        return plugin.getRegisterManager().getVaultProvider().banks();
    }

    /**
     *
     * @param player the Player to create a new Account for Economy
     * @return if it was success or not / return false if Player has already an Account
     */
    public boolean createAccount(OfflinePlayer player) {
        if(!plugin.getVaultManager().getEconomy().hasAccount(player)) {
            plugin.getVaultManager().getEconomy().createPlayerAccount(player);
            return true;
        }
        return false;
    }

    public Economy getEconomy() {
        return plugin.getVaultManager().getEconomy();
    }
}
