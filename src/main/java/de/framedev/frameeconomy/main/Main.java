package de.framedev.frameeconomy.main;

import de.framedev.frameeconomy.mongodb.BackendManager;
import de.framedev.frameeconomy.mongodb.MongoManager;
import de.framedev.frameeconomy.mysql.MySQL;
import de.framedev.frameeconomy.mysql.SQLite;
import de.framedev.frameeconomy.utils.ConfigUtils;
import de.framedev.frameeconomy.utils.RegisterManager;
import de.framedev.frameeconomy.vault.VaultManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.logging.Level;

public final class Main extends JavaPlugin implements Listener {

    // The Instance for this Class
    private static Main instance;

    //VaultManager is used for Connection to Vault
    private VaultManager vaultManager;

    //MongoDB Utils
    private MongoManager mongoManager;
    private BackendManager backendManager;

    private ConfigUtils configUtils;

    private String prefix = null;

    @Override
    public void onEnable() {
        instance = this;

        this.configUtils = new ConfigUtils();
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        configUtils.saveDefaultConfigValues();
        reloadConfig();
        saveConfig();
        try {
            configUtils.reloadCustomConfig();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //init Variable Prefix
        prefix = getPrefix();

        if (isMysql()) {
            new MySQL();
            getLogger().log(Level.INFO, "MySQL Enabled!");
        } else if (isSQL()) {
            //noinspection InstantiationOfUtilityClass
            new SQLite(getConfig().getString("SQLite.Path"), getConfig().getString("SQLite.FileName"));
            getLogger().log(Level.INFO, "SQLite Enabled!");
        }

        this.vaultManager = new VaultManager(this);


        if (isMongoDb()) {
            if (getConfig().getBoolean("MongoDB.Localhost")) {
                this.mongoManager = new MongoManager();
                mongoManager.connectLocalHost();
                getLogger().log(Level.INFO, "MongoDB Enabled!");
                this.backendManager = new BackendManager(this);
            } else if (getConfig().getBoolean("MongoDB.Normal")) {
                this.mongoManager = new MongoManager();
                mongoManager.connect();
                getLogger().log(Level.INFO, "MongoDB Enabled!");
                this.backendManager = new BackendManager(this);
            }
        }
        /*
        for(Document document : backendManager.getAllDocuments("eco")) {
            Document doc = new Document("bank",120.0);
            MongoCollection mongoCollection = mongoManager.getDatabase().getCollection("eco");
            Document updateObject = new Document("$set", doc);
            mongoCollection.updateOne(Filters.eq("uuid",document.getString("uuid")), updateObject);
        }*/
        new RegisterManager(this);
        getLogger().log(Level.INFO, "Enabled!");
        new BukkitRunnable() {
            @Override
            public void run() {
                if (vaultManager.getEconomy() != null)
                    getLogger().log(Level.INFO, "Connect to Vault API!");
            }
        }.runTaskLater(this, 60);
        new BukkitRunnable() {
            @Override
            public void run() {
                checkUpdate();
            }
        }.runTaskLater(this, 120);
    }

    /**
     * Checking for Updates in SpigotMC Forum
     */
    public void checkUpdate() {
        Bukkit.getConsoleSender().sendMessage(prefix + "Checking for updates...");
        try {
            int resource = 90172;
            URLConnection conn = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resource).openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String oldVersion = getDescription().getVersion();
            String newVersion = br.readLine();
            if (!newVersion.equalsIgnoreCase(oldVersion)) {
                Bukkit.getConsoleSender().sendMessage(prefix + "A new update is available: version " + newVersion);
            } else {
                Bukkit.getConsoleSender().sendMessage(prefix + "You're running the newest plugin version!");
            }
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(prefix + "Failed to check for updates on spigotmc.org");
        }
    }

    /**
     * This can only be changed in Config.yml
     *
     * @return Prefix of this Plugin
     */
    @NotNull
    public String getPrefix() {
        String prefix = getConfig().getString("Prefix");
        if (prefix == null) return "§6[§aFrame§bEconomy§6] §c» §7";
        if (prefix.contains("&"))
            prefix = prefix.replace("&", "§");
        if (prefix.contains(">>"))
            prefix = prefix.replace(">>", "»");
        return Objects.requireNonNull(prefix);
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "Disabled!");
    }

    /**
     * This Class contains Creator / Updater / Inserter for your MongoDB Connection
     * @return the Util class for MongoDB
     */
    public BackendManager getBackendManager() {
        return backendManager;
    }

    /**
     * Used for Connection to your MongoDB Database
     *
     * @return the MongoDB util Class
     */
    public MongoManager getMongoManager() {
        return mongoManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (vaultManager != null) {
            if (!vaultManager.getEconomy().hasAccount(event.getPlayer()))
                vaultManager.getEconomy().createPlayerAccount(event.getPlayer());
        }
        if (isMongoDb()) {
            if (backendManager != null)
                if (!backendManager.exists(event.getPlayer(), "uuid", "eco"))
                    backendManager.createUser(event.getPlayer(), "eco");
        }
    }

    /**
     * VaultManager used for VaultAPI
     *
     * @return the VaultManager
     */
    public VaultManager getVaultManager() {
        return vaultManager;
    }

    /**
     * @return this Main Class
     */
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
