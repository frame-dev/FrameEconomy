package de.framedev.frameeconomy.main;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import de.framedev.frameeconomy.commands.BalanceCMD;
import de.framedev.frameeconomy.commands.BankCMD;
import de.framedev.frameeconomy.commands.EcoCMD;
import de.framedev.frameeconomy.commands.PayCMD;
import de.framedev.frameeconomy.mongodb.BackendManager;
import de.framedev.frameeconomy.mongodb.MongoManager;
import de.framedev.frameeconomy.mysql.MySQL;
import de.framedev.frameeconomy.mysql.SQLLite;
import de.framedev.frameeconomy.vault.MySQLManager;
import de.framedev.frameeconomy.vault.VaultManager;
import frameeconomy.VaultProvider;
import org.bson.Document;
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
    private MongoManager mongoManager;
    private BackendManager backendManager;

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
        new BankCMD(this);


        if(isMongoDb()) {
            if(getConfig().getBoolean("MongoDB.Localhost")) {
                this.mongoManager = new MongoManager();
                mongoManager.connectLocalHost();
                this.backendManager = new BackendManager(this);
            } else if(getConfig().getBoolean("MongoDB.Normal")) {
                this.mongoManager = new MongoManager();
                mongoManager.connect();
                this.backendManager = new BackendManager(this);
            }
        }
        /*for(Document document : backendManager.getAllDocuments("eco")) {
            Document doc = new Document("bank",120.0);
            MongoCollection mongoCollection = mongoManager.getDatabase().getCollection("eco");
            Document updateObject = new Document("$set", doc);
            mongoCollection.updateOne(Filters.eq("uuid",document.getString("uuid")), updateObject);
        }*/

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

    public String getPrefix() {
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

    public static VaultProvider getVaultProvider() {
        return vaultProvider;
    }

    public BackendManager getBackendManager() {
        return backendManager;
    }

    public MongoManager getMongoManager() {
        return mongoManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (vaultManager != null) {
            if (!vaultManager.getEco().hasAccount(event.getPlayer()))
                vaultManager.getEco().createPlayerAccount(event.getPlayer());
        }
        if(isMongoDb()) {
            backendManager.createUser(event.getPlayer(), "eco");
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

    public boolean isMongoDb() {
        return getConfig().getBoolean("MongoDB.Use");
    }
}
