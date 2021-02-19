package de.framedev.frameeconomy.main;

import de.framedev.frameeconomy.commands.BalanceCMD;
import de.framedev.frameeconomy.commands.EcoCMD;
import de.framedev.frameeconomy.commands.PayCMD;
import de.framedev.frameeconomy.mysql.MySQL;
import de.framedev.frameeconomy.vault.VaultManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Main extends JavaPlugin implements Listener {

    private static Main instance;

    private VaultManager vaultManager;

    @Override
    public void onEnable() {
        instance = this;

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        new MySQL();

        this.vaultManager = new VaultManager(this);
        new PayCMD(this);
        new BalanceCMD(this);
        new EcoCMD(this);

        getServer().getPluginManager().registerEvents(this, this);

        getLogger().log(Level.INFO, "Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "Disabled!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (vaultManager != null) {
            if (!vaultManager.getEco().hasAccount(event.getPlayer()))
                vaultManager.getEco().createPlayerAccount(event.getPlayer());
        }
    }

    public VaultManager getVaultManager() {
        return vaultManager;
    }

    public static Main getInstance() {
        return instance;
    }

    public boolean isMysql() {
        return getConfig().getBoolean("MySQL.Use");
    }
}
