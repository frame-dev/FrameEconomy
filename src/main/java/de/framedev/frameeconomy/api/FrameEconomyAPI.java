package de.framedev.frameeconomy.api;

import de.framedev.frameeconomy.main.Main;
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

    public static FrameEconomyAPI getInstance() {
        return instance;
    }

    public List<OfflinePlayer> accounts() {
        return plugin.getVaultManager().getAccounts();
    }
}
