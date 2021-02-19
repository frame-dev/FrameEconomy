package de.framedev.frameeconomy.vault;

import de.framedev.frameeconomy.mysql.MySQL;
import de.framedev.frameeconomy.mysql.SQL;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
                    SQL.insertData(tableName, "'" + player.getUniqueId().toString() + "','" + amount + "'", "Player", "Money");
                }
            } else {
                SQL.createTable(tableName, "Player TEXT(256)", "Money DOUBLE", "BankBalance DOUBLE", "BankName TEXT", "BankOwner TEXT", "BankMembers TEXT");
                SQL.insertData(tableName, "'" + player.getUniqueId().toString() + "','" + amount + "'", "Player", "Money");
            }
        } else {
            if (SQL.isTableExists(tableName)) {
                if (SQL.exists(tableName, "Player", player.getName())) {
                    SQL.updateData(tableName, "Money", "'" + amount + "'", "Player = '" + player.getName() + "'");
                } else {
                    SQL.insertData(tableName, "'" + player.getName() + "','" + amount + "'", "Player", "Money");
                }
            } else {
                SQL.createTable(tableName, "Player TEXT(256)", "Money DOUBLE", "BankBalance DOUBLE", "BankName TEXT", "BankOwner TEXT", "BankMembers TEXT");
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
            }
        } else {
            if (SQL.isTableExists(tableName)) {
                if (SQL.exists(tableName, "Player", player.getName())) {
                    if (SQL.get(tableName, "Money", "Player", player.getName()) != null) {
                        return (double) SQL.get(tableName, "Money", "Player", player.getName());
                    }
                }
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
            SQL.createTable(tableName, "Player TEXT(256)", "Money DOUBLE", "BankBalance DOUBLE", "BankName TEXT", "BankOwner TEXT", "BankMembers TEXT");
            if (isOnlineMode()) {
                SQL.insertData(tableName, "'" + player.getUniqueId().toString() + "','" + bankName + "','" + player.getUniqueId().toString() + "'", "Player", "BankName", "BankOwner");
            } else {
                SQL.insertData(tableName, "'" + player.getName() + "','" + bankName + "','" + player.getName() + "'", "Player", "BankName", "BankOwner");
            }
        }
        return false;
    }

    protected void setBankMoney(String name, double amount) {
        try {
            Statement statement = MySQL.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE BankName ='" + name + "';");
            while (resultSet.next()) {
                resultSet.updateDouble("BankBalance", amount);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    protected double getBankMoney(String name) {
        try {
            Statement statement = MySQL.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE BankName ='" + name + "';");
            return resultSet.getDouble("BankBalance");
        } catch (Exception ignored) {
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
            Statement statement = MySQL.getConnection().createStatement();
            ResultSet resultSet = null;
            if (isOnlineMode()) {
                resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE Player = '" + player.getUniqueId().toString() + "';");
                if(resultSet.getString("BankName").equalsIgnoreCase(name) && resultSet.getString("BankOwner").equalsIgnoreCase(player.getUniqueId().toString())) {
                    return true;
                }
            } else {
                resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE Player = '" + player.getName() + "';");
                if(resultSet.getString("BankName").equalsIgnoreCase(name) && resultSet.getString("BankOwner").equalsIgnoreCase(player.getName())) {
                    return true;
                }
            }
        } catch (Exception ex) {
        }
        return false;
    }

}
