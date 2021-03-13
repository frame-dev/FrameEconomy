package de.framedev.frameeconomy.vault;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.framedev.frameeconomy.main.Main;
import de.framedev.frameeconomy.mysql.MySQL;
import de.framedev.frameeconomy.mysql.SQL;
import de.framedev.frameeconomy.mysql.SQLLite;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.essentialsmin.api
 * Date: 23.11.2020
 * Project: EssentialsMini
 * Copyrighted by FrameDev
 */
public class MySQLManager {

    protected String tableName = "frameeconomy_eco";

    protected boolean isOnlineMode() {
        return Bukkit.getServer().getOnlineMode();
    }

    protected void setMoney(OfflinePlayer player, double amount) {
        if (isOnlineMode()) {
            if (SQL.isTableExists(tableName)) {
                if (SQL.exists(tableName, "Player", player.getUniqueId().toString())) {
                    SQL.updateData(tableName, "Money", "'" + amount + "'", "Player = '" + player.getUniqueId().toString() + "'");
                } else {
                    SQL.insertData(tableName, "'" + player.getUniqueId().toString() + "','" + player.getName() + "','" + amount + "'", "Player", "Name", "Money");
                }
            } else {
                SQL.createTable(tableName, "Player TEXT(256)", "Name TEXT(255)", "Money DOUBLE", "BankBalance DOUBLE", "BankName TEXT", "BankOwner TEXT", "BankMembers TEXT");
                SQL.insertData(tableName, "'" + player.getUniqueId().toString() + "','" + player.getName() + "','" + amount + "'", "Player", "Name", "Money");
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

    protected double getMoney(OfflinePlayer player) {
        if (isOnlineMode()) {
            if (SQL.isTableExists(tableName)) {
                if (SQL.exists(tableName, "Player", player.getUniqueId().toString())) {
                    if (SQL.get(tableName, "Money", "Player", player.getUniqueId().toString()) != null) {
                        return (double) SQL.get(tableName, "Money", "Player", player.getUniqueId().toString());
                    }
                }
            } else {
                SQL.createTable(tableName, "Player TEXT(256)", "Name TEXT(255)", "Money DOUBLE", "BankBalance DOUBLE", "BankName TEXT", "BankOwner TEXT", "BankMembers TEXT");
            }
        } else {
            if (SQL.isTableExists(tableName)) {
                if (SQL.exists(tableName, "Player", player.getName())) {
                    if (SQL.get(tableName, "Money", "Player", player.getName()) != null) {
                        return (double) SQL.get(tableName, "Money", "Player", player.getName());
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

    protected boolean createBank(OfflinePlayer player, String bankName) {
        if (SQL.isTableExists(tableName)) {
            if (isOnlineMode()) {
                if (SQL.exists(tableName, "Player", player.getUniqueId().toString())) {
                    if (SQL.get(tableName, "BankName", "Player", player.getUniqueId().toString()) != null) {
                        return false;
                    } else {
                        SQL.updateData(tableName, "BankName", "'" + bankName + "'", "Player = '" + player.getUniqueId().toString() + "'");
                        SQL.updateData(tableName, "BankOwner", "'" + player.getUniqueId().toString() + "'", "Player = '" + player.getUniqueId().toString() + "'");
                        return true;
                    }
                } else {
                    SQL.insertData(tableName, "'" + player.getUniqueId().toString() + "','" + bankName + "','" + player.getUniqueId().toString() + "'", "Player", "BankName", "BankOwner");
                    return true;
                }
            } else {
                if (SQL.exists(tableName, "Player", player.getName())) {
                    if (SQL.get(tableName, "BankName", "Player", player.getName()) == null) {
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
                SQL.insertData(tableName, "'" + player.getUniqueId().toString() + "','" + bankName + "','" + player.getUniqueId().toString() + "'", "Player", "BankName", "BankOwner");
            } else {
                SQL.insertData(tableName, "'" + player.getName() + "','" + bankName + "','" + player.getName() + "'", "Player", "BankName", "BankOwner");
            }
        }
        return false;
    }

    protected void setBankMoney(String name, double amount) {
        int i = 0;
        List<String> players = new ArrayList<>();
        try {
            if (Main.getInstance().isMysql()) {
                Statement statement = MySQL.getConnection().createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE BankName ='" + name + "';");
                while (resultSet.next()) {
                    i++;
                    players.add(resultSet.getString("Player"));
                }
            } else if (Main.getInstance().isSQL()) {
                Statement statement = SQLLite.connect().createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE BankName ='" + name + "';");
                while (resultSet.next()) {
                    i++;
                    players.add(resultSet.getString("Player"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (Main.getInstance().isMysql()) {
                MySQL.close();
            } else if (Main.getInstance().isSQL())
                SQLLite.close();
        }
        if (Main.getInstance().isMysql() || Main.getInstance().isSQL()) {
            for (int x = 0; x <= i; x++) {
                for (String player : players) {
                    SQL.updateData(tableName, "BankBalance", "'" + amount + "'", "Player = '" + player + "'");
                }
            }
        }
    }

    protected double getBankMoney(String name) {
        try {
            if (Main.getInstance().isMysql()) {
                Statement statement = MySQL.getConnection().createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE BankName ='" + name + "';");
                if (resultSet.next())
                    return resultSet.getDouble("BankBalance");
            } else if (Main.getInstance().isSQL()) {
                Statement statement = SQLLite.connect().createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE BankName ='" + name + "';");
                if (resultSet.next())
                    return resultSet.getDouble("BankBalance");
            }
        } catch (Exception ignored) {
        } finally {
            if (Main.getInstance().isMysql()) {
                MySQL.close();
            } else if (Main.getInstance().isSQL())
                SQLLite.close();
        }
        return 0.0;
    }

    protected void addBankMoney(String name, double amount) {
        double money = getBankMoney(name);
        money += amount;
        setBankMoney(name, amount);
    }

    protected void removeBankMoney(String name, double amount) {
        double money = getBankMoney(name);
        money -= amount;
        setBankMoney(name, amount);
    }

    protected boolean isBankOwner(String name, OfflinePlayer player) {
        try {
            if (Main.getInstance().isMysql()) {
                Statement statement = MySQL.getConnection().createStatement();
                ResultSet resultSet = null;
                if (isOnlineMode()) {
                    resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE Player = '" + player.getUniqueId().toString() + "';");
                    if (resultSet.next())
                        if (resultSet.getString("BankName").equalsIgnoreCase(name) && resultSet.getString("BankOwner").equalsIgnoreCase(player.getUniqueId().toString()))
                            return true;
                } else {
                    resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE Player = '" + player.getName() + "';");
                    if (resultSet.next())
                        if (resultSet.getString("BankName").equalsIgnoreCase(name) && resultSet.getString("BankOwner").equalsIgnoreCase(player.getName()))
                            return true;
                }
            } else if (Main.getInstance().isSQL()) {
                Statement statement = SQLLite.connect().createStatement();
                ResultSet resultSet = null;
                if (isOnlineMode()) {
                    resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE Player = '" + player.getUniqueId().toString() + "';");
                    if (resultSet.next()) {
                        if (resultSet.getString("BankName").equalsIgnoreCase(name) && resultSet.getString("BankOwner").equalsIgnoreCase(player.getUniqueId().toString())) {
                            return true;
                        }
                    }
                } else {
                    resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE Player = '" + player.getName() + "';");
                    if (resultSet.next()) {
                        if (resultSet.getString("BankName").equalsIgnoreCase(name) && resultSet.getString("BankOwner").equalsIgnoreCase(player.getName())) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception ex) {
        } finally {
            if (Main.getInstance().isMysql()) {
                MySQL.close();
            } else if (Main.getInstance().isSQL())
                SQLLite.close();
        }
        return false;
    }

    public void addBankMember(String bankName, OfflinePlayer player) {
        if (SQL.isTableExists(tableName)) {
            if (SQL.exists(tableName, "BankName", bankName)) {
                if (SQL.get(tableName, "BankMembers", "BankName", bankName) != null) {
                    Type type = new TypeToken<List<String>>() {
                    }.getType();
                    List<String> players = new Gson().fromJson((String) SQL.get(tableName, "BankMembers", "BankName", bankName), type);
                    if (!players.contains(player.getName()))
                        players.add(player.getName());
                    if (isOnlineMode()) {
                        SQL.updateData(tableName, "BankOwner", "'" + SQL.get(tableName, "BankOwner", "BankName", bankName) + "'", "Player = '" + player.getUniqueId().toString() + "'");
                        SQL.updateData(tableName, "BankName", "'" + bankName + "'", "Player = '" + player.getUniqueId().toString() + "'");
                    } else {
                        SQL.updateData(tableName, "BankOwner", "'" + SQL.get(tableName, "BankOwner", "BankName", bankName) + "'", "Player = '" + player.getName() + "'");
                        SQL.updateData(tableName, "BankName", "'" + bankName + "'", "Player = '" + player.getName() + "'");
                    }
                    SQL.updateData(tableName, "BankMembers", "'" + new Gson().toJson(players) + "'", "BankName = '" + bankName + "'");
                } else {
                    List<String> players = new ArrayList<>();
                    players.add(player.getName());
                    if (isOnlineMode()) {
                        SQL.updateData(tableName, "BankOwner", "'" + SQL.get(tableName, "BankOwner", "BankName", bankName) + "'", "Player = '" + player.getUniqueId().toString() + "'");
                        SQL.updateData(tableName, "BankName", "'" + bankName + "'", "Player = '" + player.getUniqueId().toString() + "'");
                    } else {
                        SQL.updateData(tableName, "BankOwner", "'" + SQL.get(tableName, "BankOwner", "BankName", bankName) + "'", "Player = '" + player.getName() + "'");
                        SQL.updateData(tableName, "BankName", "'" + bankName + "'", "Player = '" + player.getName() + "'");
                    }
                    SQL.updateData(tableName, "BankMembers", "'" + new Gson().toJson(players) + "'", "BankName = '" + bankName + "'");
                }
            }
        }
    }

    public boolean isBankMember(String bankName, OfflinePlayer player) {
        if (SQL.isTableExists(tableName)) {
            if (SQL.exists(tableName, "BankName", bankName)) {
                if (SQL.get(tableName, "BankMembers", "BankName", bankName) != null) {
                    Type type = new TypeToken<List<String>>() {
                    }.getType();
                    List<String> players = new Gson().fromJson((String) SQL.get(tableName, "BankMembers", "BankName", bankName), type);
                    if (players.contains(player.getName()))
                        return true;
                }
            }
        }
        return false;
    }

    public void removeBankMember(String bankName, OfflinePlayer player) {
        List<String> pls = new ArrayList<>();
        List<String> members = new ArrayList<>();
        if (SQL.isTableExists(tableName)) {
            if (SQL.exists(tableName, "BankName", bankName)) {
                if (SQL.get(tableName, "BankMembers", "BankName", bankName) != null) {
                    Type type = new TypeToken<List<String>>() {
                    }.getType();
                    List<String> players = new Gson().fromJson((String) SQL.get(tableName, "BankMembers", "BankName", bankName), type);
                    if (players.contains(player.getName())) {
                        players.remove(player.getName());
                    }
                    System.out.println(players);
                    if (isOnlineMode()) {
                        SQL.updateData(tableName, "BankOwner", "'" + null + "'", "Player = '" + player.getUniqueId().toString() + "'");
                        SQL.updateData(tableName, "BankName", "'" + null + "'", "Player = '" + player.getUniqueId().toString() + "'");
                        SQL.updateData(tableName, "BankBalance", "'" + null + "'", "Player = '" + player.getUniqueId().toString() + "'");
                        SQL.updateData(tableName, "BankMembers", "'" + null + "'", "Player = '" + player.getUniqueId().toString() + "'");
                    } else {
                        SQL.updateData(tableName, "BankOwner", "'" + null + "'", "Player = '" + player.getName() + "'");
                        SQL.updateData(tableName, "BankName", "'" + null + "'", "Player = '" + player.getName() + "'");
                        SQL.updateData(tableName, "BankBalance", "'" + null + "'", "Player = '" + player.getName() + "'");
                        SQL.updateData(tableName, "BankMembers", "'" + null + "'", "Player = '" + player.getName() + "'");
                    }
                    members.addAll(players);
                    if (Main.getInstance().isMysql()) {
                        try {
                            Statement statement = MySQL.getConnection().createStatement();
                            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE BankName ='" + bankName + "';");
                            while (resultSet.next()) {
                                pls.add(resultSet.getString("Player"));
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        } finally {
                            MySQL.close();
                        }
                    } else if (Main.getInstance().isSQL()) {
                        try {
                            Statement statement = SQLLite.connect().createStatement();
                            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE BankName ='" + bankName + "';");
                            while (resultSet.next()) {
                                pls.add(resultSet.getString("Player"));
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        } finally {
                            SQLLite.close();
                        }
                    }
                }
            }
            if (Main.getInstance().isMysql() || Main.getInstance().isSQL()) {
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
    }

    protected void createAccount(OfflinePlayer player) {
        if (!SQL.isTableExists("frameeconomy_accounts")) {
            SQL.createTable("frameeconomy_accounts", "name TEXT(255)", "uuid VARCHAR(2003)");
        }
        if (isOnlineMode()) {
            if (!SQL.exists("frameeconomy_accounts", "uuid", "" + player.getUniqueId())) {
                SQL.insertData("frameeconomy_accounts", "'" + player.getName() + "','" + player.getUniqueId() + "'", "name", "uuid");
            }
        } else {
            if (!SQL.exists("frameeconomy_accounts", "name", "" + player.getName())) {
                SQL.insertData("frameeconomy_accounts", "'" + player.getName() + "'", "name");
            }
        }
    }

    protected boolean hasAccount(OfflinePlayer player) {
        if (SQL.isTableExists("frameeconomy_accounts")) {
            if (isOnlineMode()) {
                if (SQL.exists("frameeconomy_accounts", "uuid", "" + player.getUniqueId())) {
                    return true;
                }
            } else {
                if (SQL.exists("frameeconomy_accounts", "name", "" + player.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<String> getBankMembers(String bankName) {
        if (SQL.isTableExists(tableName)) {
            if (SQL.exists(tableName, "BankName", bankName)) {
                if (SQL.get(tableName, "BankMembers", "BankName", bankName) != null) {
                    Type type = new TypeToken<List<String>>() {
                    }.getType();
                    List<String> players = new Gson().fromJson((String) SQL.get(tableName, "BankMembers", "BankName", bankName), type);
                    return players;
                }
            }
        }
        return null;
    }

    protected List<String> getBanks() {
        List<String> banks = new ArrayList<>();
        if (SQL.isTableExists(tableName)) {
            try {
                if (Main.getInstance().isMysql()) {
                    Statement statement = MySQL.getConnection().createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE BankName IS NOT NULL");
                    while (resultSet.next()) {
                        banks.add(resultSet.getString("BankName"));
                    }
                } else if (Main.getInstance().isSQL()) {
                    Statement statement = SQLLite.connect().createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE BankName IS NOT NULL");
                    while (resultSet.next()) {
                        banks.add(resultSet.getString("BankName"));
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (Main.getInstance().isMysql())
                    MySQL.close();
                if (Main.getInstance().isSQL())
                    SQLLite.close();
            }
        }
        return banks;
    }
}
