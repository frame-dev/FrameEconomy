package de.framedev.frameeconomy.vault;

import de.framedev.frameeconomy.main.Main;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
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
        if (Main.getInstance() != null) {
            return true;
        }
        return false;
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
        File file = new File(Main.getInstance().getDataFolder() + "/money", "eco.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        if (Bukkit.getServer().getOnlineMode()) {
            if (Main.getInstance().isMysql() || Main.getInstance().isSQL()) {
                return new MySQLManager().hasAccount(Bukkit.getOfflinePlayer(s));
            }
            if (cfg.getStringList("accounts").contains(Bukkit.getOfflinePlayer(s).getUniqueId().toString())) {
                return true;
            }
        } else {
            if (cfg.getStringList("accounts").contains(Bukkit.getOfflinePlayer(s).getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasAccount(String s, String s1) {
        return hasAccount(s);
    }

    @Override
    public double getBalance(String playerName) {
        File file = new File(Main.getInstance().getDataFolder() + "/money", "eco.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        if (Main.getInstance().isMysql() || Main.getInstance().isSQL()) {
            return new MySQLManager().getMoney(Bukkit.getOfflinePlayer(playerName));
        } else if (Main.getInstance().isMongoDb()) {
            if (Main.getInstance().getBackendManager().exists(Bukkit.getOfflinePlayer(playerName), "money", "eco"))
                return (double) Main.getInstance().getBackendManager().get(Bukkit.getOfflinePlayer(playerName), "money", "eco");
        } else {
            if (Bukkit.getServer().getOnlineMode()) {
                return cfg.getDouble(Bukkit.getOfflinePlayer(playerName).getUniqueId().toString());
            } else {
                return cfg.getDouble(Bukkit.getOfflinePlayer(playerName).getName());
            }
        }
        return 0.0;
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
        if (getBalance(playerName) < amount) {
            return false;
        }
        return true;
    }

    @Override
    public boolean has(String s, String s1, double v) {
        return has(s, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        if (!hasAccount(playerName))
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "The Player does not have an Account!");
        double balance = getBalance(playerName);
        if (getBalance(playerName) > Main.getInstance().getConfig().getDouble("Economy.MinBalance")) {
            balance -= amount;
            if (balance < Main.getInstance().getConfig().getDouble("Economy.MinBalance"))
                return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.FAILURE, " test ");
            if (Main.getInstance().isMysql() || Main.getInstance().isSQL()) {
                new MySQLManager().removeMoney(Bukkit.getOfflinePlayer(playerName), amount);
            } else if (Main.getInstance().isMongoDb()) {
                if (Main.getInstance().getBackendManager().exists(Bukkit.getOfflinePlayer(playerName), "money", "eco"))
                    Main.getInstance().getBackendManager().updateUser(Bukkit.getOfflinePlayer(playerName), "money", amount, "eco");
            } else {
                File file = new File(Main.getInstance().getDataFolder() + "/money", "eco.yml");
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
                if (Bukkit.getServer().getOnlineMode()) {
                    cfg.set(Bukkit.getOfflinePlayer(playerName).getUniqueId().toString(), balance);
                } else {
                    cfg.set(Bukkit.getOfflinePlayer(playerName).getName(), balance);
                }
                try {
                    cfg.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
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
        if (Main.getInstance().isMysql() || Main.getInstance().isSQL()) {
            new MySQLManager().addMoney(Bukkit.getOfflinePlayer(playerName), amount);
        } else if (Main.getInstance().isMongoDb()) {
            if (Main.getInstance().getBackendManager().exists(Bukkit.getOfflinePlayer(playerName), "money", "eco"))
                Main.getInstance().getBackendManager().updateUser(Bukkit.getOfflinePlayer(playerName), "money", amount, "eco");
        } else {
            File file = new File(Main.getInstance().getDataFolder() + "/money", "eco.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            if (balance > Main.getInstance().getConfig().getDouble("Economy.MaxBalance")) {
                return new EconomyResponse(Main.getInstance().getConfig().getDouble("Economy.MaxBalance"), 0.0D, EconomyResponse.ResponseType.FAILURE, "Bigger");
            }
            if (Bukkit.getServer().getOnlineMode()) {
                cfg.set(Bukkit.getOfflinePlayer(playerName).getUniqueId().toString(), balance);
            } else {
                cfg.set(Bukkit.getOfflinePlayer(playerName).getName(), balance);
            }
            try {
                cfg.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new EconomyResponse(amount, 0.0D, EconomyResponse.ResponseType.SUCCESS, "");
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
        if (Main.getInstance().isMysql() || Main.getInstance().isSQL()) {
            new MySQLManager().createBank(Bukkit.getOfflinePlayer(player), name);
        } else if (Main.getInstance().isMongoDb()) {
            Main.getInstance().getBackendManager().updateUser(Bukkit.getOfflinePlayer(player), "bankowner", Bukkit.getOfflinePlayer(player).getUniqueId().toString(), "eco");
            Main.getInstance().getBackendManager().updateUser(Bukkit.getOfflinePlayer(player), "bankname", name, "eco");
        } else {
            File file = new File(Main.getInstance().getDataFolder() + "/money", "eco.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            cfg.set("Banks." + name + ".Owner", player);
            try {
                cfg.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        if(Main.getInstance().isMysql() || Main.getInstance().isSQL()) {
            if(new MySQLManager().removeBank(name))
                return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.SUCCESS, "");
            return new EconomyResponse(0.0,0.0, EconomyResponse.ResponseType.FAILURE,"Error while Deleting Bank!");
        } else {
            File file = new File(Main.getInstance().getDataFolder() + "/money", "eco.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            if (!cfg.contains("Banks." + name))
                return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Bank doesn't exists!");
            cfg.set("Banks." + name, null);
            try {
                cfg.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.SUCCESS, "");
        }
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        if (Main.getInstance().isMysql() || Main.getInstance().isSQL()) {
            return new EconomyResponse(new MySQLManager().getBankMoney(name), new MySQLManager().getBankMoney(name), EconomyResponse.ResponseType.SUCCESS, "");
        } else if (Main.getInstance().isMongoDb()) {
            return new EconomyResponse((double) Main.getInstance().getBackendManager().getObject("bankname", name, "bank", "eco"), (double) Main.getInstance().getBackendManager().getObject("bankname", name, "bank", "eco"), EconomyResponse.ResponseType.SUCCESS, "");
        } else {
            File file = new File(Main.getInstance().getDataFolder() + "/money", "eco.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            if (!cfg.contains("Banks." + name))
                return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Bank doesn't exists!");
            return new EconomyResponse(cfg.getDouble("Banks." + name + ".balance"), cfg.getDouble("Banks." + name + ".balance"), EconomyResponse.ResponseType.SUCCESS, "");
        }
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        if (bankBalance(name).amount < amount) {
            return new EconomyResponse(amount, bankBalance(name).amount, EconomyResponse.ResponseType.FAILURE, "Not enought Money!");
        }
        return new EconomyResponse(amount, bankBalance(name).amount, EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        double balance = bankBalance(name).amount;
        if (Main.getInstance().isMysql() || Main.getInstance().isSQL()) {
            balance -= amount;
            if (!bankHas(name, amount).transactionSuccess())
                return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.FAILURE, "Not enought Money");
            new MySQLManager().setBankMoney(name, balance);
        } else if (Main.getInstance().isMongoDb()) {
            balance -= amount;
            if (!bankHas(name, amount).transactionSuccess())
                return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.FAILURE, "Not enought Money");
            Main.getInstance().getBackendManager().updataData("bankname", name, "bank", balance, "eco");
        } else {
            File file = new File(Main.getInstance().getDataFolder() + "/money", "eco.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            if (!bankHas(name, amount).transactionSuccess())
                return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.FAILURE, "Not enought Money");
            balance -= amount;
            cfg.set("Banks." + name + ".balance", balance);
            try {
                cfg.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        if (Main.getInstance().isMysql() || Main.getInstance().isSQL()) {
            double balance = new MySQLManager().getBankMoney(name);
            balance += amount;
            new MySQLManager().setBankMoney(name, balance);
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
        } else if (Main.getInstance().isMongoDb()) {
            double balance = bankBalance(name).balance;
            balance += amount;
            Main.getInstance().getBackendManager().updataData("bankname", name, "bank", balance, "eco");
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
        } else {
            File file = new File(Main.getInstance().getDataFolder() + "/money", "eco.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            double balance = bankBalance(name).amount;
            balance += amount;
            cfg.set("Banks." + name + ".balance", balance);
            try {
                cfg.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
        }
    }

    @Override
    public EconomyResponse isBankOwner(String name, String player) {
        if (Main.getInstance().isMysql() || Main.getInstance().isSQL()) {
            if (!new MySQLManager().isBankOwner(name, Bukkit.getOfflinePlayer(player)))
                return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Isn't the Owner");
        } else if (Main.getInstance().isMongoDb()) {
            if (!((String) Main.getInstance().getBackendManager().get(Bukkit.getOfflinePlayer(player), "bankowner", "eco")).equalsIgnoreCase(Bukkit.getOfflinePlayer(player).getUniqueId().toString()))
                return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Isn't the Owner");
        } else {
            File file = new File(Main.getInstance().getDataFolder() + "/money", "eco.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            if (!cfg.contains("Banks." + name + ".Owner"))
                return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Doesn't Exsits");
            if (!player.equalsIgnoreCase(cfg.getString("Banks." + name + ".Owner")))
                return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Isn't the Owner");
        }
        return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse isBankMember(String name, String player) {
        if (Main.getInstance().isMysql() || Main.getInstance().isSQL()) {
            if (!new MySQLManager().isBankMember(name, Bukkit.getOfflinePlayer(player)))
                return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Isn't a member");
        } else if (Main.getInstance().isMongoDb()) {
            if (!Main.getInstance().getVaultManager().getBankMembers(name).contains(player))
                return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Isn't a member");
        } else {
            File file = new File(Main.getInstance().getDataFolder() + "/money", "eco.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            if (!cfg.contains("Banks." + name + ".members"))
                return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Doesn't have any Members!");
            List<String> players = cfg.getStringList("Banks." + name + ".members");
            if (!players.contains(player))
                return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Isn't a member");
        }
        return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public List<String> getBanks() {
        if (Main.getInstance().isMysql() || Main.getInstance().isSQL()) {
            return new MySQLManager().getBanks();
        } else if (Main.getInstance().isMongoDb()) {
            List<String> data = new ArrayList<>();
            for (OfflinePlayer offlinePlayer : Bukkit.getOnlinePlayers()) {
                if (((String)Main.getInstance().getBackendManager().get(offlinePlayer, "bankowner", "eco")).equalsIgnoreCase(offlinePlayer.getUniqueId().toString()))
                    data.add((String) Main.getInstance().getBackendManager().get(offlinePlayer, "bankname", "eco"));
            }
            return data;
        } else {
            File file = new File(Main.getInstance().getDataFolder() + "/money", "eco.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            List<String> banks = new ArrayList<>();
            ConfigurationSection cs = cfg.getConfigurationSection("Banks");
            if (cs != null) {
                for (String s : cs.getKeys(false)) {
                    if (s != null) {
                        banks.add(s);
                    }
                }
            }
            return banks;
        }
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        if (!hasAccount(playerName)) {
            File file = new File(Main.getInstance().getDataFolder() + "/money", "eco.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            if (Bukkit.getServer().getOnlineMode()) {
                if (Main.getInstance().isMysql() || Main.getInstance().isSQL()) {
                    new MySQLManager().createAccount(Bukkit.getOfflinePlayer(playerName));
                } else {
                    List<String> accounts = cfg.getStringList("accounts");
                    accounts.add(Bukkit.getOfflinePlayer(playerName).getUniqueId().toString());
                    cfg.set("accounts", accounts);
                }
            } else {
                if (Main.getInstance().isMysql() || Main.getInstance().isSQL()) {
                    new MySQLManager().createAccount(Bukkit.getOfflinePlayer(playerName));
                } else {
                    List<String> accounts = cfg.getStringList("accounts");
                    accounts.add(Bukkit.getOfflinePlayer(playerName).getName());
                    cfg.set("accounts", accounts);
                }
            }
            try {
                cfg.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(playerName);
    }


}
