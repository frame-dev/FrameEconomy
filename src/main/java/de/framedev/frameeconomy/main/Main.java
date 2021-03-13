package de.framedev.frameeconomy.main;

import de.framedev.frameeconomy.commands.BalanceCMD;
import de.framedev.frameeconomy.commands.EcoCMD;
import de.framedev.frameeconomy.commands.PayCMD;
import de.framedev.frameeconomy.mysql.MySQL;
import de.framedev.frameeconomy.mysql.SQLLite;
import de.framedev.frameeconomy.vault.VaultManager;
import frameeconomy.VaultProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public final class Main extends JavaPlugin implements Listener {

    private static Main instance;

    private VaultManager vaultManager;
    private static VaultProvider vaultProvider;

    private String prefix = null;

    @Override
    public void onEnable() {
        instance = this;
        
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        reloadConfig();
        saveConfig();

        prefix = getPrefix();

        if(isMysql()) {
            new MySQL();
            getLogger().log(Level.INFO, "MySQL Enabled!");
        } else if(isSQL()) {
            new SQLLite(getConfig().getString("SQLite.Path"), getConfig().getString("SQLite.FileName"));
            getLogger().log(Level.INFO, "SQLite Enabled!");
        }

        this.vaultManager = new VaultManager(this);
        vaultProvider = new VaultProvider(this);
        new PayCMD(this);
        new BalanceCMD(this);
        new EcoCMD(this);

        getServer().getPluginManager().registerEvents(this, this);

        getLogger().log(Level.INFO, "Enabled!");
        new BukkitRunnable() {
            @Override
            public void run() {
                if (vaultManager.getEco() != null)
                    getLogger().log(Level.INFO, "Connect to Vault API!");
            }
        }.runTaskLater(this, 60);
    }

    private String getPrefix() {
        String prefix = getConfig().getString("Prefix");
        if(prefix.contains("&"))
            prefix = prefix.replace('&','§');
        if(prefix.contains(">>"))
            prefix = prefix.replace(">>", "»");
        return prefix;
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "Disabled!");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(command.getName().equalsIgnoreCase("bank")) {
            vaultManager.getEco().createBank("data", Bukkit.getOfflinePlayer("FramePlays5771"));
            vaultManager.getEco().bankDeposit("data",100d);
            sender.sendMessage("Successfully");
            vaultManager.addBankMember("data", Bukkit.getOfflinePlayer("FramePlays"));
            sender.sendMessage(String.valueOf(vaultManager.getEco().isBankMember("data", Bukkit.getOfflinePlayer("FramePlays")).transactionSuccess()));
            sender.sendMessage(String.valueOf(vaultManager.getEco().bankBalance("data").balance));
        }
        return super.onCommand(sender, command, label, args);
    }

    public static VaultProvider getVaultProvider() {
        return vaultProvider;
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

    public boolean isSQL() {
        return getConfig().getBoolean("SQLite.Use");
    }

    public boolean isMysql() {
        return getConfig().getBoolean("MySQL.Use");
    }
}
