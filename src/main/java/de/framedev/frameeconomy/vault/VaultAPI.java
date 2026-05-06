package de.framedev.frameeconomy.vault;

import de.framedev.frameeconomy.mongodb.BackendManager;
import de.framedev.frameeconomy.main.Main;
import de.framedev.frameeconomy.utils.FileManager;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import java.util.ArrayList;
import java.util.List;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.essentialsmin.api
 * Date: 22.11.2020
 * Project: EssentialsMini
 * Copyrighted by FrameDev
 */
public class VaultAPI extends AbstractEconomy {

    @Override
    public boolean isEnabled() {
        return Main.getInstance() != null;
    }

    @Override
    public String getName() {
        return Main.getInstance().getDescription().getName();
    }

    @Override
    public boolean hasBankSupport() {
        return true;
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String format(double amount) {
        return String.format("%.6f", amount);
    }

    @Override
    public String currencyNamePlural() {
        return Main.getInstance().getConfig().getString("Currency.Plural");
    }

    @Override
    public String currencyNameSingular() {
        return Main.getInstance().getConfig().getString("Currency.Singular");
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return hasAccount(player.getName());
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        return depositPlayer(player.getName(), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        return withdrawPlayer(player.getName(), amount);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return createPlayerAccount(player.getName());
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return has(player.getName(), amount);
    }

    @Override
    public boolean hasAccount(String s) {
        OfflinePlayer player = offlinePlayer(s);
        if (Main.getInstance().isSqlStorage()) {
            return new SQLManager().hasAccount(player);
        }
        BackendManager backendManager = mongoBackend();
        if (backendManager != null) {
            return backendManager.exists(player, "uuid", "eco");
        }
        return new FileManager().hasAccount(player);
    }

    @Override
    public boolean hasAccount(String s, String s1) {
        return hasAccount(s);
    }

    @Override
    public double getBalance(String playerName) {
        OfflinePlayer player = offlinePlayer(playerName);
        if (Main.getInstance().isSqlStorage()) {
            return new SQLManager().getMoney(player);
        }
        BackendManager backendManager = mongoBackend();
        if (backendManager != null && backendManager.exists(player, "money", "eco")) {
            Object balance = backendManager.get(player, "money", "eco");
            if (balance instanceof Number) {
                return ((Number) balance).doubleValue();
            }
        }
        return new FileManager().getMoney(player);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return getBalance(player.getName());
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player.getName());
    }

    @Override
    public double getBalance(String s, String s1) {
        return getBalance(s);
    }

    @Override
    public boolean has(String playerName, double amount) {
        if (amount < 0.0D) return false;
        return !(getBalance(playerName) < amount);
    }

    @Override
    public boolean has(String s, String s1, double v) {
        return has(s, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        if (amount < 0.0D)
            return new EconomyResponse(0.0D, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Value is less than zero!");
        if (!hasAccount(playerName))
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "The Player does not have an Account!");
        double balance = getBalance(playerName);
        double minBalance = Main.getInstance().getConfig().getDouble("Economy.MinBalance", 0.0D);
        double newBalance = balance - amount;
        if (newBalance < minBalance) {
            return failure(0.0D, balance, "Not enough money!");
        }
        OfflinePlayer player = offlinePlayer(playerName);
        savePlayerBalance(player, newBalance);
        return success(amount, newBalance);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return withdrawPlayer(s, v);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        if (!hasAccount(playerName))
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "The Player does not have an Account!");
        if (amount < 0.0D)
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Value is less than zero!");
        double balance = getBalance(playerName);
        balance += amount;
        if (balance > Main.getInstance().getConfig().getDouble("Economy.MaxBalance"))
            return failure(Main.getInstance().getConfig().getDouble("Economy.MaxBalance"), 0.0D, "Bigger");

        OfflinePlayer player = offlinePlayer(playerName);
        savePlayerBalance(player, balance);
        return success(amount, balance);
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        return depositPlayer(s, v);
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return createBank(name, player.getName());
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        if (getBanks().contains(name)) {
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Bank already exists!");
        }
        if (Main.getInstance().isSqlStorage()) {
            if (!new SQLManager().createBank(offlinePlayer(player), name))
                return failure(0.0D, 0.0D, "Error while Creating Bank!");
        } else {
            BackendManager backendManager = mongoBackend();
            if (backendManager != null) {
                OfflinePlayer bankPlayer = offlinePlayer(player);
                backendManager.updateUser(bankPlayer, "bankowner", bankPlayer.getUniqueId().toString(), "eco");
                backendManager.updateUser(bankPlayer, "bankname", name, "eco");
                backendManager.updateUser(bankPlayer, "bank", 0.0D, "eco");
            } else {
                new FileManager().createBank(name, player);
            }
        }
        return success(0.0D, 0.0D);
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        if (Main.getInstance().isSqlStorage()) {
            if (new SQLManager().removeBank(name))
                return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.SUCCESS, "");
            return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Error while Deleting Bank!");
        } else {
            FileManager fileManager = new FileManager();
            if (fileManager.hasBank(name)) {
                fileManager.deleteBank(name);
                return success(0.0, 0.0);
            }
            return failure(0.0D, 0.0D, "Bank doesn't exists!");
        }
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        if (Main.getInstance().isSqlStorage()) {
            return new EconomyResponse(new SQLManager().getBankMoney(name), new SQLManager().getBankMoney(name), EconomyResponse.ResponseType.SUCCESS, "");
        }
        BackendManager backendManager = mongoBackend();
        if (backendManager != null) {
            Object bank = backendManager.getObject("bankname", name, "bank", "eco");
            if (!(bank instanceof Number))
                return failure(0.0D, 0.0D, "Bank doesn't exists!");
            double balance = ((Number) bank).doubleValue();
            return success(balance, balance);
        }
        FileManager fileManager = new FileManager();
        if (fileManager.hasBank(name)) {
            double balance = fileManager.getBankBalance(name);
            return success(balance, balance);
        }
        return failure(0.0D, 0.0D, "Bank doesn't exists!");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        EconomyResponse balance = bankBalance(name);
        if (!balance.transactionSuccess()) return balance;
        if (amount < 0.0D) {
            return new EconomyResponse(0.0D, balance.amount, EconomyResponse.ResponseType.FAILURE, "Value is less than zero!");
        }
        if (balance.amount < amount) {
            return new EconomyResponse(amount, balance.amount, EconomyResponse.ResponseType.FAILURE, "Not enought Money!");
        }
        return new EconomyResponse(amount, balance.amount, EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        if (amount < 0.0D)
            return new EconomyResponse(0.0D, bankBalance(name).amount, EconomyResponse.ResponseType.FAILURE, "Value is less than zero!");
        EconomyResponse bankBalance = bankBalance(name);
        if (!bankBalance.transactionSuccess())
            return bankBalance;
        double balance = bankBalance.amount;
        if (balance < amount)
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.FAILURE, "Not enought Money");
        double newBalance = balance - amount;
        if (Main.getInstance().isSqlStorage()) {
            new SQLManager().setBankMoney(name, newBalance);
        } else {
            BackendManager backendManager = mongoBackend();
            if (backendManager != null) {
                backendManager.updataData("bankname", name, "bank", newBalance, "eco");
            } else {
                new FileManager().setBankBalance(name, newBalance);
            }
        }
        return success(amount, newBalance);
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        if (amount < 0.0D)
            return new EconomyResponse(0.0D, bankBalance(name).amount, EconomyResponse.ResponseType.FAILURE, "Value is less than zero!");
        EconomyResponse bankBalance = bankBalance(name);
        if (!bankBalance.transactionSuccess())
            return bankBalance;
        double balance = bankBalance.amount + amount;
        if (Main.getInstance().isSqlStorage()) {
            new SQLManager().setBankMoney(name, balance);
            return success(amount, balance);
        }
        BackendManager backendManager = mongoBackend();
        if (backendManager != null) {
            backendManager.updataData("bankname", name, "bank", balance, "eco");
            return success(amount, balance);
        }
        new FileManager().setBankBalance(name, balance);
        return success(amount, balance);
    }

    @Override
    public EconomyResponse isBankOwner(String name, String player) {
        if (Main.getInstance().isSqlStorage()) {
            if (!new SQLManager().isBankOwner(name, offlinePlayer(player)))
                return failure(0.0, 0.0, "Isn't the Owner");
        } else {
            BackendManager backendManager = mongoBackend();
            if (backendManager != null) {
                OfflinePlayer bankPlayer = offlinePlayer(player);
                Object bankOwner = backendManager.get(bankPlayer, "bankowner", "eco");
                if (!(bankOwner instanceof String) || !((String) bankOwner).equalsIgnoreCase(bankPlayer.getUniqueId().toString()))
                    return failure(0.0, 0.0, "Isn't the Owner");
            } else {
                FileManager fileManager = new FileManager();
                if (fileManager.hasBank(name)) {
                    if (!fileManager.isBankOwner(name, player))
                        return failure(0.0, 0.0, "Isn't the Owner");
                    return success(0.0, 0.0);
                }
                return failure(0.0, 0.0, "Doesn't Exsits");
            }
        }
        return success(0.0, 0.0);
    }

    @Override
    public EconomyResponse isBankMember(String name, String player) {
        if (Main.getInstance().isSqlStorage()) {
            if (!new SQLManager().isBankMember(name, offlinePlayer(player)))
                return failure(0.0, 0.0, "Isn't a member");
        } else {
            BackendManager backendManager = mongoBackend();
            if (backendManager != null) {
                if (!Main.getInstance().getVaultManager().getBankMembers(name).contains(player))
                    return failure(0.0, 0.0, "Isn't a member");
            } else {
                FileManager fileManager = new FileManager();
                if (fileManager.hasBank(name)) {
                    if (!fileManager.isBankMember(name, player))
                        return failure(0.0, 0.0, "Isn't a member");
                    return success(0.0, 0.0);
                }
                return failure(0.0, 0.0, "Doesn't have any Members!");
            }
        }
        return success(0.0, 0.0);
    }

    @Override
    public List<String> getBanks() {
        if (Main.getInstance().isSqlStorage()) {
            return new SQLManager().getBanks();
        }
        BackendManager backendManager = mongoBackend();
        if (backendManager != null) {
            List<String> data = new ArrayList<>();
            for (org.bson.Document document : backendManager.getAllDocuments("eco")) {
                Object bankName = document.get("bankname");
                Object bankOwner = document.get("bankowner");
                if (bankName instanceof String && bankOwner instanceof String
                        && !((String) bankName).isEmpty()
                        && !data.contains((String) bankName)) {
                    data.add((String) bankName);
                }
            }
            return data;
        }
        return new FileManager().getBanks();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        if (!hasAccount(playerName)) {
            if (Main.getInstance().isSqlStorage()) {
                new SQLManager().createAccount(offlinePlayer(playerName));
            } else if (Main.getInstance().isMongoDb()) {
                if (Main.getInstance().getBackendManager() != null) {
                    Main.getInstance().getBackendManager().createUser(offlinePlayer(playerName), "eco");
                }
            } else {
                new FileManager().createAccount(offlinePlayer(playerName));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(playerName);
    }

    private OfflinePlayer offlinePlayer(String value) {
        return Main.getInstance().resolveOfflinePlayer(value);
    }

    private void savePlayerBalance(OfflinePlayer player, double balance) {
        if (Main.getInstance().isSqlStorage()) {
            new SQLManager().setMoney(player, balance);
            return;
        }
        BackendManager backendManager = mongoBackend();
        if (backendManager != null && backendManager.exists(player, "money", "eco")) {
            backendManager.updateUser(player, "money", balance, "eco");
            return;
        }
        new FileManager().setMoney(player, balance);
    }

    private BackendManager mongoBackend() {
        if (!Main.getInstance().isMongoDb()) {
            return null;
        }
        return Main.getInstance().getBackendManager();
    }

    private EconomyResponse success(double amount, double balance) {
        return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
    }

    private EconomyResponse failure(double amount, double balance, String message) {
        return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.FAILURE, message);
    }
}
