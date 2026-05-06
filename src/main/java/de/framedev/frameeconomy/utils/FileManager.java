package de.framedev.frameeconomy.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.framedev.frameeconomy.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles local economy storage in either YAML or JSON format.
 */
@SuppressWarnings("unused")
public class FileManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final File file;
    private final boolean jsonStorage;
    private final FileConfiguration cfg;
    private JsonObject json;

    /**
     * Constructor for this class / Creates the File and FileConfiguration
     */
    public FileManager() {
        this.jsonStorage = Main.getInstance().isJsonFileStorage();
        String path = Main.getInstance().getConfig().getString("FileStorage.Path", "plugins/FrameEconomy/money");
        String fileName = Main.getInstance().getConfig().getString("FileStorage.FileName", "eco");
        this.file = new File(path, fileName + (jsonStorage ? ".json" : ".yml"));
        ensureFileExists();
        if (jsonStorage) {
            this.cfg = null;
            loadJson();
        } else {
            this.cfg = YamlConfiguration.loadConfiguration(file);
            this.json = null;
        }
    }

    /**
     * Save the local storage file.
     */
    private void save() {
        try {
            if (jsonStorage) {
                try (FileWriter writer = new FileWriter(file)) {
                    GSON.toJson(json, writer);
                }
            } else {
                cfg.save(file);
            }
        } catch (IOException e) {
            logger().log(Level.SEVERE, "Could not save economy storage file: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * @param player the Player to set the Money
     * @param amount the Amount of the Money
     */
    public void setMoney(OfflinePlayer player, double amount) {
        if (jsonStorage) {
            json.addProperty(playerKey(player), amount);
        } else {
            cfg.set(playerKey(player), amount);
        }
        save();
    }

    /**
     * @param player the Player to get the Money
     * @return returns the Money from the Player
     */
    public double getMoney(OfflinePlayer player) {
        String key = playerKey(player);
        if (jsonStorage) {
            JsonElement element = json.get(key);
            return element != null && element.isJsonPrimitive() ? element.getAsDouble() : 0.0D;
        }
        return cfg.getDouble(key);
    }

    /**
     * @param player the Player to add Money
     * @param amount the Amount to add
     */
    public void addMoney(OfflinePlayer player, double amount) {
        setMoney(player, getMoney(player) + amount);
    }

    /**
     * @param player the Player to remove Money
     * @param amount the Amount to remove
     */
    public void removeMoney(OfflinePlayer player, double amount) {
        setMoney(player, getMoney(player) - amount);
    }

    /**
     * @param player the Player
     * @param amount the Amount that requires the Player
     * @return return if the Player has the Money
     */
    public boolean has(OfflinePlayer player, double amount) {
        return getMoney(player) >= amount;
    }

    public boolean hasAccount(OfflinePlayer player) {
        return getAccounts().contains(playerKey(player));
    }

    public void createAccount(OfflinePlayer player) {
        List<String> accounts = getAccounts();
        String key = playerKey(player);
        if (!accounts.contains(key)) {
            accounts.add(key);
            setAccounts(accounts);
        }
    }

    public boolean hasBank(String bankName) {
        if (jsonStorage) {
            return banksJson().has(bankName);
        }
        return cfg.contains("Banks." + bankName);
    }

    public void createBank(String bankName, String owner) {
        if (jsonStorage) {
            JsonObject bank = bankJson(bankName);
            bank.addProperty("Owner", owner);
            bank.addProperty("balance", 0.0D);
        } else {
            cfg.set("Banks." + bankName + ".Owner", owner);
            cfg.set("Banks." + bankName + ".balance", 0.0D);
        }
        save();
    }

    public void deleteBank(String bankName) {
        if (jsonStorage) {
            banksJson().remove(bankName);
        } else {
            cfg.set("Banks." + bankName, null);
        }
        save();
    }

    public double getBankBalance(String bankName) {
        if (jsonStorage) {
            JsonElement balance = bankJson(bankName).get("balance");
            return balance != null && balance.isJsonPrimitive() ? balance.getAsDouble() : 0.0D;
        }
        return cfg.getDouble("Banks." + bankName + ".balance");
    }

    public void setBankBalance(String bankName, double amount) {
        if (jsonStorage) {
            bankJson(bankName).addProperty("balance", amount);
        } else {
            cfg.set("Banks." + bankName + ".balance", amount);
        }
        save();
    }

    public String getBankOwner(String bankName) {
        if (jsonStorage) {
            JsonElement owner = bankJson(bankName).get("Owner");
            return owner != null && owner.isJsonPrimitive() ? owner.getAsString() : null;
        }
        return cfg.getString("Banks." + bankName + ".Owner");
    }

    public boolean isBankOwner(String bankName, String playerName) {
        String owner = getBankOwner(bankName);
        return owner != null && owner.equalsIgnoreCase(playerName);
    }

    public boolean isBankMember(String bankName, String playerName) {
        return getBankMembers(bankName).contains(playerName);
    }

    public void addBankMember(String bankName, String playerName) {
        List<String> members = getBankMembers(bankName);
        if (!members.contains(playerName)) {
            members.add(playerName);
            setBankMembers(bankName, members);
        }
    }

    public void removeBankMember(String bankName, String playerName) {
        List<String> members = getBankMembers(bankName);
        if (members.remove(playerName)) {
            setBankMembers(bankName, members);
        }
    }

    public List<String> getBankMembers(String bankName) {
        if (jsonStorage) {
            JsonElement members = bankJson(bankName).get("members");
            if (members == null || !members.isJsonArray()) {
                return new ArrayList<>();
            }
            List<String> values = new ArrayList<>();
            for (JsonElement element : members.getAsJsonArray()) {
                values.add(element.getAsString());
            }
            return values;
        }
        return new ArrayList<>(cfg.getStringList("Banks." + bankName + ".members"));
    }

    public List<String> getBanks() {
        if (jsonStorage) {
            List<String> values = new ArrayList<>();
            for (java.util.Map.Entry<String, JsonElement> entry : banksJson().entrySet()) {
                values.add(entry.getKey());
            }
            return values;
        }
        ConfigurationSection banksSection = cfg.getConfigurationSection("Banks");
        if (banksSection == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(banksSection.getKeys(false));
    }

    private void setBankMembers(String bankName, List<String> members) {
        if (jsonStorage) {
            JsonArray jsonMembers = new JsonArray();
            for (String member : members) {
                jsonMembers.add(member);
            }
            bankJson(bankName).add("members", jsonMembers);
        } else {
            cfg.set("Banks." + bankName + ".members", members);
        }
        save();
    }

    private List<String> getAccounts() {
        if (jsonStorage) {
            JsonArray accounts = accountsJson();
            List<String> values = new ArrayList<>();
            for (JsonElement element : accounts) {
                values.add(element.getAsString());
            }
            return values;
        }
        return new ArrayList<>(cfg.getStringList("accounts"));
    }

    private void setAccounts(List<String> accounts) {
        if (jsonStorage) {
            JsonArray jsonAccounts = new JsonArray();
            for (String account : accounts) {
                jsonAccounts.add(account);
            }
            json.add("accounts", jsonAccounts);
        } else {
            cfg.set("accounts", accounts);
        }
        save();
    }

    private String playerKey(OfflinePlayer player) {
        if (Bukkit.getServer().getOnlineMode()) {
            return player.getUniqueId().toString();
        }
        return player.getName();
    }

    private void ensureFileExists() {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new IOException("Could not create storage directory: " + parent.getAbsolutePath());
            }
            if (!file.exists() && !file.createNewFile()) {
                throw new IOException("Could not create storage file: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            logger().log(Level.SEVERE, "Could not initialize economy storage file: " + file.getAbsolutePath(), e);
        }
    }

    private void loadJson() {
        try (FileReader reader = new FileReader(file)) {
            JsonElement element = new JsonParser().parse(reader);
            if (element != null && element.isJsonObject()) {
                this.json = element.getAsJsonObject();
                return;
            }
        } catch (Exception exception) {
            logger().log(Level.FINE, "Could not parse JSON storage file, recreating default structure", exception);
        }
        this.json = new JsonObject();
        save();
    }

    private Logger logger() {
        Main plugin = Main.getInstance();
        return plugin != null ? plugin.getLogger() : Logger.getLogger(FileManager.class.getName());
    }

    private JsonArray accountsJson() {
        JsonElement accounts = json.get("accounts");
        if (accounts != null && accounts.isJsonArray()) {
            return accounts.getAsJsonArray();
        }
        JsonArray created = new JsonArray();
        json.add("accounts", created);
        return created;
    }

    private JsonObject banksJson() {
        JsonElement banks = json.get("Banks");
        if (banks != null && banks.isJsonObject()) {
            return banks.getAsJsonObject();
        }
        JsonObject created = new JsonObject();
        json.add("Banks", created);
        return created;
    }

    private JsonObject bankJson(String bankName) {
        JsonObject banks = banksJson();
        JsonElement bank = banks.get(bankName);
        if (bank != null && bank.isJsonObject()) {
            return bank.getAsJsonObject();
        }
        JsonObject created = new JsonObject();
        banks.add(bankName, created);
        return created;
    }
}
