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
 */

public class RegisterManager {

    public RegisterManager(Main plugin) {
        //Register Commands
        new PayCMD(plugin);
        new BalanceCMD(plugin);
        new EcoCMD(plugin);
        new BankCMD(plugin);
        new VaultProvider(plugin);
        // Register Join Listener
        plugin.getServer().getPluginManager().registerEvents(plugin, plugin);
    }
}
