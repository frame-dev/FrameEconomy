package de.framedev.frameeconomy.main;

import de.framedev.frameeconomy.api.FrameEconomyAPI;
import de.framedev.frameeconomy.mongodb.BackendManager;
import de.framedev.frameeconomy.mongodb.MongoManager;
import de.framedev.frameeconomy.mysql.MySQL;
import de.framedev.frameeconomy.mysql.SQLite;
import de.framedev.frameeconomy.utils.ConfigUtils;
import de.framedev.frameeconomy.utils.MongoDBUtils;
import de.framedev.frameeconomy.utils.RegisterManager;
import de.framedev.frameeconomy.utils.SchedulerManager;
import de.framedev.frameeconomy.vault.VaultManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public final class Main extends JavaPlugin implements Listener {

    // The Instance for this Class
    private static Main instance;

    //VaultManager is used for Connection to Vault
    private VaultManager vaultManager;

    // Register Manager
    private RegisterManager registerManager;

    //MongoDB Utils
    private MongoDBUtils mongoDBUtils;

    private ExecutorService databaseExecutor;

    @Override
    public void onEnable() {
        instance = this;
        databaseExecutor = Executors.newSingleThreadExecutor(task -> {
            Thread thread = new Thread(task, "FrameEconomy-Database");
            thread.setDaemon(true);
            return thread;
        });

        ConfigUtils configUtils = new ConfigUtils();
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

        if (isMysql()) {
            new MySQL();
            getLogger().log(Level.INFO, "MySQL Enabled!");
        } else if (isSQL()) {
            //noinspection InstantiationOfUtilityClass
            new SQLite(getConfig().getString("SQLite.Path"), getConfig().getString("SQLite.FileName"));
            getLogger().log(Level.INFO, "SQLite Enabled!");
        } else if (isMongoDb())
            this.mongoDBUtils = new MongoDBUtils();

        // Register Vault
        this.vaultManager = new VaultManager(this);

        // for(Document document : backendManager.getAllDocuments("eco")) {
        //     Document doc = new Document("bank",120.0);
        //     MongoCollection mongoCollection = mongoManager.getDatabase().getCollection("eco");
        //     Document updateObject = new Document("$set", doc);
        //     mongoCollection.updateOne(Filters.eq("uuid",document.getString("uuid")), updateObject);
        // }
        // Register All
        this.registerManager = new RegisterManager(this);

        getLogger().log(Level.INFO, "Enabled");

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
                runAsync(Main.this::checkUpdate);
            }
        }.runTaskLater(this, 120);

        new SchedulerManager().runTaskTimer(this, 20 * 6, 20 * 60 * 5);
        new FrameEconomyAPI(this);
    }

    @Override
    public void onLoad() {
        getLogger().log(Level.INFO, "Loaded");
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        if (databaseExecutor != null) {
            databaseExecutor.shutdown();
            try {
                if (!databaseExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    databaseExecutor.shutdownNow();
                }
            } catch (InterruptedException exception) {
                databaseExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        MySQL.shutdown();
        SQLite.shutdown();
        if (getMongoManager() != null) {
            getMongoManager().shutdown();
        }
        getLogger().log(Level.INFO, "Disabled!");
    }

    /**
     * This Class is used for Register all classes
     *
     * @return return the RegisterManager that register all Classes that implements CommandExecutor and other stuff
     */
    public RegisterManager getRegisterManager() {
        return registerManager;
    }

    /**
     * Checking for Updates in SpigotMC Forum
     */
    public void checkUpdate() {
        Bukkit.getConsoleSender().sendMessage(getMessage("update.checking"));
        try {
            int resource = 90172;
            URLConnection conn = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resource).openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String oldVersion = getDescription().getVersion();
            String newVersion = br.readLine();
            if (!newVersion.equalsIgnoreCase(oldVersion)) {
                Bukkit.getConsoleSender().sendMessage(getMessage("update.available", "version", newVersion));
            } else {
                Bukkit.getConsoleSender().sendMessage(getMessage("update.current"));
            }
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(getMessage("update.failed"));
        }
    }

    /**
     * This can only be changed in Config.yml
     * Implements an Example Prefix if Prefix in Config.yml was Null
     *
     * @return Prefix of this Plugin
     */
    @NotNull
    public String getPrefix() {
        String prefix = getConfig().getString("Prefix");
        if (prefix == null) return "§6[§aFrame§bEconomy§6] §c» §7";
        return colorize(Objects.requireNonNull(prefix));
    }

    /**
     * Reads a player-facing message from config.yml and applies placeholders.
     *
     * @param path         path below the Messages section
     * @param replacements key/value placeholder pairs
     * @return configured and formatted message
     */
    public String getMessage(String path, String... replacements) {
        return getPrefix() + getMessageWithoutPrefix(path, replacements);
    }

    /**
     * Reads a message without adding the plugin prefix.
     *
     * @param path         path below the Messages section
     * @param replacements key/value placeholder pairs
     * @return configured and formatted message
     */
    public String getMessageWithoutPrefix(String path, String... replacements) {
        String message = getConfig().getString("Messages." + path, "Missing message: " + path);
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            message = message.replace("{" + replacements[i] + "}", String.valueOf(replacements[i + 1]));
        }
        return colorize(message);
    }

    /**
     * Sends a configured message to a command sender.
     *
     * @param sender       command sender receiving the message
     * @param path         path below the Messages section
     * @param replacements key/value placeholder pairs
     */
    public void sendMessage(CommandSender sender, String path, String... replacements) {
        sender.sendMessage(getMessage(path, replacements));
    }

    /**
     * Runs storage-heavy work away from the server thread.
     *
     * @param task work to run asynchronously
     */
    public void runAsync(Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(this, task);
    }

    /**
     * Runs database and storage work on FrameEconomy's serialized database worker.
     *
     * @param task storage work to run asynchronously
     */
    public void runDatabaseAsync(Runnable task) {
        if (databaseExecutor == null || databaseExecutor.isShutdown()) {
            runAsync(task);
            return;
        }
        databaseExecutor.execute(() -> {
            try {
                task.run();
            } catch (Exception exception) {
                getLogger().log(Level.SEVERE, "Async database task failed", exception);
            }
        });
    }

    /**
     * Runs Bukkit-facing work on the server thread.
     *
     * @param task work to run synchronously
     */
    public void runSync(Runnable task) {
        Bukkit.getScheduler().runTask(this, task);
    }

    /**
     * Sends a configured message from async code by hopping back to the server thread.
     *
     * @param sender command sender receiving the message
     * @param path path below the Messages section
     * @param replacements key/value placeholder pairs
     */
    public void sendMessageSync(CommandSender sender, String path, String... replacements) {
        runSync(() -> sendMessage(sender, path, replacements));
    }

    /**
     * Parses a positive amount from command input and reports invalid values.
     *
     * @param sender    command sender to notify on invalid input
     * @param rawAmount amount text from the command
     * @return parsed amount, or null when invalid
     */
    public Double parsePositiveAmount(CommandSender sender, String rawAmount) {
        try {
            double amount = Double.parseDouble(rawAmount);
            if (amount <= 0) {
                sendMessage(sender, "general.invalid-amount", "amount", rawAmount);
                return null;
            }
            return amount;
        } catch (NumberFormatException exception) {
            sendMessage(sender, "general.invalid-amount", "amount", rawAmount);
            return null;
        }
    }

    /**
     * Applies legacy Minecraft color code formatting.
     *
     * @param message message text
     * @return colorized message
     */
    public String colorize(String message) {
        if (message == null) return "";
        return message.replace("&", "§").replace(">>", "»");
    }

    /**
     * Sets a player's balance through the active Vault economy provider.
     *
     * @param player player account to update
     * @param amount new balance
     * @return true if the economy accepted the update
     */
    public boolean setPlayerBalance(OfflinePlayer player, double amount) {
        if (!vaultManager.getEconomy().hasAccount(player)) {
            vaultManager.getEconomy().createPlayerAccount(player);
        }
        double currentBalance = vaultManager.getEconomy().getBalance(player);
        return vaultManager.getEconomy().withdrawPlayer(player, currentBalance).transactionSuccess()
                && vaultManager.getEconomy().depositPlayer(player, amount).transactionSuccess();
    }

    /**
     * This Class contains Creator / Updater / Inserter for your MongoDB Connection
     *
     * @return the Util class for MongoDB
     */
    public BackendManager getBackendManager() {
        if (mongoDBUtils == null) return null;
        return mongoDBUtils.getBackendManager();
    }

    /**
     * Used for Connection to your MongoDB Database
     * in this Class MongoDB will connect to your Database
     *
     * @return the MongoDB util Class
     */
    public MongoManager getMongoManager() {
        if (mongoDBUtils == null) return null;
        return mongoDBUtils.getMongoManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        OfflinePlayer player = event.getPlayer();
        runDatabaseAsync(() -> {
            if (vaultManager != null) {
                if (!vaultManager.getEconomy().hasAccount(player))
                    vaultManager.getEconomy().createPlayerAccount(player);
            }
            if (isMongoDb()) {
                if (getBackendManager() != null)
                    if (!getBackendManager().exists(player, "uuid", "eco"))
                        getBackendManager().createUser(player, "eco");
            }
        });
    }

    /**
     * VaultManager used for VaultAPI
     * Any change will make Errors
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

    /**
     *
     * @return if SQLite is Enabled
     */
    public boolean isSQL() {
        return getConfig().getBoolean("SQLite.Use");
    }

    /**
     *
     * @return if MySQL is Enabled
     */
    public boolean isMysql() {
        return getConfig().getBoolean("MySQL.Use");
    }

    /**
     *
     * @return if MongoDB is Enabled
     */
    public boolean isMongoDb() {
        return getConfig().getBoolean("MongoDB.Use");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("frameeconomy")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("frameeconomy.reload")) {
                        saveConfig();
                        reloadConfig();
                        Bukkit.getPluginManager().disablePlugin(this);
                        Bukkit.getPluginManager().enablePlugin(this);
                        sendMessage(sender, "reload.success");
                    } else {
                        sendMessage(sender, "general.no-permission");
                    }
                }
            }
        }
        return super.onCommand(sender, command, label, args);
    }
}
