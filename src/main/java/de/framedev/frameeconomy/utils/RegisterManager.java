package de.framedev.frameeconomy.utils;

import de.framedev.frameeconomy.commands.BalanceCMD;
import de.framedev.frameeconomy.commands.BankCMD;
import de.framedev.frameeconomy.commands.EcoCMD;
import de.framedev.frameeconomy.commands.PayCMD;
import de.framedev.frameeconomy.main.Main;
import frameeconomy.kotlin.VaultProvider;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.frameeconomy.utils
 * ClassName RegisterManager
 * Date: 31.03.21
 * Project: FrameEconomy
 * Copyrighted by FrameDev
 * <p>
 * Register All Commands and Listeners
 */

public class RegisterManager {

    private final VaultProvider vaultProvider;

    /**
     * Initial the RegisterManager for Register all Listeners and Commands
     *
     * @param plugin the Main Java Plugin
     */
    public RegisterManager(Main plugin) {
        //Register Commands
        new PayCMD(plugin);
        new BalanceCMD(plugin);
        new EcoCMD(plugin);
        new BankCMD(plugin);

        // Create VaultProvider
        this.vaultProvider = new VaultProvider(plugin);
        // Register Listeners
        plugin.getServer().getPluginManager().registerEvents(plugin, plugin);
    }

    /**
     * This Returns the Koltin Util File
     *
     * @return returns the Registerd VaultProvider
     */
    public VaultProvider getVaultProvider() {
        return vaultProvider;
    }
}
