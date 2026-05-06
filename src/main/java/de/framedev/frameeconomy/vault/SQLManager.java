package de.framedev.frameeconomy.vault;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.framedev.frameeconomy.main.Main;
import de.framedev.frameeconomy.mysql.SQL;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.essentialsmin.api
 * Date: 23.11.2020
 * Project: EssentialsMini
 * Copyrighted by FrameDev
 */
@SuppressWarnings({"SqlSourceToSinkFlow", "unused"})
public class SQLManager {
    /**
     * Hilfsmethode, um die Bankmitglieder-Liste sicher zu laden (nie null)
     */
    private List<String> getBankMembersList(String bankName) {
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> players = new Gson().fromJson((String) SQL.get(tableName, "BankMembers", "BankName", bankName), type);
        return players != null ? players : new ArrayList<>();
    }

    protected String tableName = "frameeconomy_eco";

    protected boolean isOnlineMode() {
        return Bukkit.getServer().getOnlineMode();
    }

    /**
     * set the Money in the Database
     *
     * @param player the Player
     * @param amount Money amount
     */
    protected void setMoney(OfflinePlayer player, double amount) {
        if (isOnlineMode()) {
            if (SQL.isTableExists(tableName)) {
                if (SQL.exists(tableName, "Player", player.getUniqueId().toString())) {
                    SQL.updateData(tableName, "Money", "'" + amount + "'", "Player = '" + player.getUniqueId() + "'");
                } else {
                    SQL.insertData(tableName, "'" + player.getUniqueId() + "','" + player.getName() + "','" + amount + "'", "Player", "Name", "Money");
                }
            } else {
                SQL.createTable(tableName, "Player TEXT(256)", "Name TEXT(255)", "Money DOUBLE", "BankBalance DOUBLE", "BankName TEXT", "BankOwner TEXT", "BankMembers TEXT");
                SQL.insertData(tableName, "'" + player.getUniqueId() + "','" + player.getName() + "','" + amount + "'", "Player", "Name", "Money");
            }
        } else {
            if (SQL.isTableExists(tableName)) {
                if (SQL.exists(tableName, "Player", player.getName())) {
                    SQL.updateData(tableName, "Money", "'" + amount + "'", "Player = '" + player.getName() + "'");
                } else {
                    SQL.insertData(tableName, "'" + player.getName() + "','" + amount + "'", "Player", "Money");
                }
            } else {
                SQL.createTable(tableName, "Player TEXT(256)", "Name TEXT(255)", "Money DOUBLE", "BankBalance DOUBLE", "BankName TEXT", "BankOwner TEXT", "BankMembers TEXT");
                SQL.insertData(tableName, "'" + player.getName() + "','" + amount + "'", "Player", "Money");
            }
        }
    }

    /**
     * @param player the Player
     * @return the Money from the selected Player
     */
    protected double getMoney(OfflinePlayer player) {
        if (isOnlineMode()) {
            if (SQL.isTableExists(tableName)) {
                if (SQL.exists(tableName, "Player", player.getUniqueId().toString())) {
                    Object value = SQL.get(tableName, "Money", "Player", player.getUniqueId().toString());
                    if (value != null) {
                        return (double) value;
                    }
                }
            } else {
                SQL.createTable(tableName, "Player TEXT(256)", "Name TEXT(255)", "Money DOUBLE", "BankBalance DOUBLE", "BankName TEXT", "BankOwner TEXT", "BankMembers TEXT");
            }
        } else {
            if (SQL.isTableExists(tableName)) {
                if (SQL.exists(tableName, "Player", player.getName())) {
                    Object value = SQL.get(tableName, "Money", "Player", player.getName());
                    if (value != null) {
                        return (double) value;
                    }
                }
            } else {
                SQL.createTable(tableName, "Player TEXT(256)", "Name TEXT(255)", "Money DOUBLE", "BankBalance DOUBLE", "BankName TEXT", "BankOwner TEXT", "BankMembers TEXT");
            }
        }
        return 0.0D;
    }

    protected void addMoney(OfflinePlayer player, double amount) {
        double money = getMoney(player);
        money += amount;
        setMoney(player, money);
    }

    protected void removeMoney(OfflinePlayer player, double amount) {
        double money = getMoney(player);
        money -= amount;
        setMoney(player, money);
    }

    /**
     * @param player   the BankOwner
     * @param bankName the BankName
     * @return Successfully creating the Bank or not
     */
    protected boolean createBank(OfflinePlayer player, String bankName) {
        if (SQL.isTableExists(tableName)) {
            if (isOnlineMode()) {
                if (SQL.exists(tableName, "Player", player.getUniqueId().toString())) {
                    if (SQL.get(tableName, "BankName", "Player", player.getUniqueId().toString()) != null) {
                        return false;
                    } else {
                        SQL.updateData(tableName, "BankName", "'" + bankName + "'", "Player = '" + player.getUniqueId() + "'");
                        SQL.updateData(tableName, "BankOwner", "'" + player.getUniqueId() + "'", "Player = '" + player.getUniqueId() + "'");
                        return true;
                    }
                } else {
                    SQL.insertData(tableName, "'" + player.getUniqueId() + "','" + bankName + "','" + player.getUniqueId() + "'", "Player", "BankName", "BankOwner");
                    return true;
                }
            } else {
                if (SQL.exists(tableName, "Player", player.getName())) {
                    if (SQL.get(tableName, "BankName", "Player", player.getName()) != null) {
                        return false;
                    } else {
                        SQL.updateData(tableName, "BankName", "'" + bankName + "'", "Player = '" + player.getName() + "'");
                        SQL.updateData(tableName, "BankOwner", "'" + player.getName() + "'", "Player = '" + player.getName() + "'");
                        return true;
                    }
                } else {
                    SQL.insertData(tableName, "'" + player.getName() + "','" + bankName + "','" + player.getName() + "'", "Player", "BankName", "BankOwner");
                    return true;
                }
            }
        } else {
            SQL.createTable(tableName, "Player TEXT(256)", "Name TEXT(255)", "Money DOUBLE", "BankBalance DOUBLE", "BankName TEXT", "BankOwner TEXT", "BankMembers TEXT");
            if (isOnlineMode()) {
                SQL.insertData(tableName, "'" + player.getUniqueId() + "','" + bankName + "','" + player.getUniqueId() + "'", "Player", "BankName", "BankOwner");
            } else {
                SQL.insertData(tableName, "'" + player.getName() + "','" + bankName + "','" + player.getName() + "'", "Player", "BankName", "BankOwner");
            }
            return true;
        }
    }

    /**
     * set the Bank Money
     *
     * @param name   the BankName
     * @param amount amount to adding to the Bank
     */
    protected void setBankMoney(String name, double amount) {
        List<String> players = new ArrayList<>();
        try (PreparedStatement statement = SQL.connection().prepareStatement("SELECT Player FROM " + tableName + " WHERE BankName = ?")) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    players.add(resultSet.getString("Player"));
                }
            }
        } catch (Exception ex) {
            logger().log(Level.SEVERE, "Could not load bank players for bank '" + name + "'", ex);
        } finally {
            SQL.close();
        }
        for (String player : players) {
            SQL.updateData(tableName, "BankBalance", "'" + amount + "'", "Player = '" + player + "'");
        }
    }

    /**
     * @param name the BankName
     * @return Amount of the Bank
     */
    protected double getBankMoney(String name) {
        try (PreparedStatement statement = SQL.connection().prepareStatement("SELECT BankBalance FROM " + tableName + " WHERE BankName = ? LIMIT 1")) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("BankBalance");
                }
            }
        } catch (Exception ex) {
            logger().log(Level.SEVERE, "Could not read bank balance for bank '" + name + "'", ex);
        } finally {
            SQL.close();
        }
        return 0.0;
    }

    protected void addBankMoney(String name, double amount) {
        double money = getBankMoney(name);
        money += amount;
        setBankMoney(name, money);
    }

    protected void removeBankMoney(String name, double amount) {
        double money = getBankMoney(name);
        money -= amount;
        setBankMoney(name, money);
    }

    /**
     * @param name   the BankName
     * @param player the Player
     * @return if the user is the BankOwner
     */
    protected boolean isBankOwner(String name, OfflinePlayer player) {
        String playerName = isOnlineMode() ? player.getUniqueId().toString() : player.getName();
        try (PreparedStatement statement = SQL.connection().prepareStatement("SELECT BankName, BankOwner FROM " + tableName + " WHERE Player = ? LIMIT 1")) {
            statement.setString(1, playerName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String bankName = resultSet.getString("BankName");
                    String bankOwner = resultSet.getString("BankOwner");
                    return bankName != null && bankOwner != null
                            && bankName.equalsIgnoreCase(name)
                            && bankOwner.equalsIgnoreCase(playerName);
                }
            }
        } catch (Exception ex) {
            logger().log(Level.SEVERE, "Could not verify bank owner for bank '" + name + "'", ex);
        } finally {
            SQL.close();
        }
        return false;
    }

    /**
     * Adding user to the Bank as Member
     *
     * @param bankName the BankName
     * @param player   the Player
     */
    public void addBankMember(String bankName, OfflinePlayer player) {
        if (SQL.isTableExists(tableName)) {
            if (SQL.exists(tableName, "BankName", bankName)) {
                List<String> players = getBankMembersList(bankName);
                if (!players.contains(player.getName()))
                    players.add(player.getName());
                executeOnlineMode(bankName, player, players);
            }
        }
    }

    private void executeOnlineMode(String bankName, OfflinePlayer player, List<String> players) {
        if (isOnlineMode()) {
            SQL.updateData(tableName, "BankOwner", "'" + SQL.get(tableName, "BankOwner", "BankName", bankName) + "'", "Player = '" + player.getUniqueId() + "'");
            SQL.updateData(tableName, "BankName", "'" + bankName + "'", "Player = '" + player.getUniqueId() + "'");
        } else {
            SQL.updateData(tableName, "BankOwner", "'" + SQL.get(tableName, "BankOwner", "BankName", bankName) + "'", "Player = '" + player.getName() + "'");
            SQL.updateData(tableName, "BankName", "'" + bankName + "'", "Player = '" + player.getName() + "'");
        }
        SQL.updateData(tableName, "BankMembers", "'" + new Gson().toJson(players) + "'", "BankName = '" + bankName + "'");
    }

    /**
     * @param bankName the BankName
     * @param player   the Player
     * @return if the Player is a BankMember
     */
    public boolean isBankMember(String bankName, OfflinePlayer player) {
        if (SQL.isTableExists(tableName)) {
            if (SQL.exists(tableName, "BankName", bankName)) {
                List<String> players = getBankMembersList(bankName);
                return players.contains(player.getName());
            }
        }
        return false;
    }

    /**
     * @param bankName the Bank Name
     * @param player   the Player
     */
    public void removeBankMember(String bankName, OfflinePlayer player) {
        List<String> pls = new ArrayList<>();
        List<String> members = new ArrayList<>();
        if (SQL.isTableExists(tableName)) {
            if (SQL.exists(tableName, "BankName", bankName)) {
                List<String> players = getBankMembersList(bankName);
                players.remove(player.getName());
                if (isOnlineMode()) {
                    SQL.updateData(tableName, "BankOwner", "null", "Player = '" + player.getUniqueId() + "'");
                    SQL.updateData(tableName, "BankName", "null", "Player = '" + player.getUniqueId() + "'");
                    SQL.updateData(tableName, "BankBalance", "null", "Player = '" + player.getUniqueId() + "'");
                    SQL.updateData(tableName, "BankMembers", "null", "Player = '" + player.getUniqueId() + "'");
                } else {
                    SQL.updateData(tableName, "BankOwner", "null", "Player = '" + player.getName() + "'");
                    SQL.updateData(tableName, "BankName", "null", "Player = '" + player.getName() + "'");
                    SQL.updateData(tableName, "BankBalance", "null", "Player = '" + player.getName() + "'");
                    SQL.updateData(tableName, "BankMembers", "null", "Player = '" + player.getName() + "'");
                }
                members.addAll(players);
                try (PreparedStatement statement = SQL.connection().prepareStatement("SELECT Player FROM " + tableName + " WHERE BankName = ?")) {
                    statement.setString(1, bankName);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            pls.add(resultSet.getString("Player"));
                        }
                    }
                } catch (Exception ex) {
                    logger().log(Level.SEVERE, "Could not load member list rows for bank '" + bankName + "'", ex);
                } finally {
                    SQL.close();
                }
            }
            if (SQL.isTableExists(tableName)) {
                if (SQL.exists(tableName, "BankName", bankName)) {
                    if (SQL.get(tableName, "BankMembers", "BankName", bankName) != null) {
                        for (String players : pls) {
                            SQL.updateData(tableName, "BankMembers", "'" + new Gson().toJson(members) + "'", "Player = '" + players + "'");
                        }
                    }
                }
            }
        }
    }

    /**
     * Create an Account for Vault
     *
     * @param player the Player
     */
    protected void createAccount(OfflinePlayer player) {
        if (!SQL.isTableExists("frameeconomy_accounts")) {
            SQL.createTable("frameeconomy_accounts", "name TEXT(255)", "uuid VARCHAR(2003)");
        }
        if (isOnlineMode()) {
            if (!SQL.exists("frameeconomy_accounts", "uuid", String.valueOf(player.getUniqueId()))) {
                SQL.insertData("frameeconomy_accounts", "'" + player.getName() + "','" + player.getUniqueId() + "'", "name", "uuid");
            }
        } else {
            if (!SQL.exists("frameeconomy_accounts", "name", player.getName())) {
                SQL.insertData("frameeconomy_accounts", "'" + player.getName() + "'", "name");
            }
        }
    }

    /**
     * @param player the Player
     * @return if the Player has an Account or not
     */
    protected boolean hasAccount(OfflinePlayer player) {
        if (SQL.isTableExists("frameeconomy_accounts")) {
            if (isOnlineMode()) {
                return SQL.exists("frameeconomy_accounts", "uuid", String.valueOf(player.getUniqueId()));
            } else {
                return SQL.exists("frameeconomy_accounts", "name", player.getName());
            }
        }
        return false;
    }

    /**
     * @param bankName the Bank
     * @return all BankMembers
     */
    public List<String> getBankMembers(String bankName) {
        if (SQL.isTableExists(tableName)) {
            if (SQL.exists(tableName, "BankName", bankName)) {
                if (SQL.get(tableName, "BankMembers", "BankName", bankName) != null) {
                    Type type = new TypeToken<List<String>>() {
                    }.getType();
                    return new Gson().fromJson((String) SQL.get(tableName, "BankMembers", "BankName", bankName), type);
                }
            }
        }
        return null;
    }

    /**
     * @return all Banks
     */
    protected List<String> getBanks() {
        List<String> banks = new ArrayList<>();
        if (SQL.isTableExists(tableName)) {
            try (Statement statement = SQL.connection().createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE BankName IS NOT NULL")) {
                while (resultSet.next()) {
                    banks.add(resultSet.getString("BankName"));
                }
            } catch (Exception ex) {
                logger().log(Level.SEVERE, "Could not list banks from SQL storage", ex);
            } finally {
                SQL.close();
            }
        }
        return banks;
    }

    public boolean removeBank(String bankName) {
        if (SQL.isTableExists(tableName)) {
            if (getBanks().contains(bankName)) {
                try {
                    SQL.updateData(tableName, "BankBalance", "0.0", "BankName = '" + bankName + "'");
                    SQL.updateData(tableName, "BankOwner", "null", "BankName = '" + bankName + "'");
                    SQL.updateData(tableName, "BankMembers", "null", "BankName = '" + bankName + "'");
                    SQL.updateData(tableName, "BankName", "null", "BankName = '" + bankName + "'");
                    return true;
                } catch (Exception ex) {
                    logger().log(Level.SEVERE, "Could not remove bank '" + bankName + "'", ex);
                }
            }
        }
        return false;
    }

    private Logger logger() {
        Main plugin = Main.getInstance();
        return plugin != null ? plugin.getLogger() : Logger.getLogger(SQLManager.class.getName());
    }
}
