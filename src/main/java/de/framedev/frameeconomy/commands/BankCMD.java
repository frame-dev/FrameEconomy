package de.framedev.frameeconomy.commands;

import de.framedev.frameeconomy.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.frameeconomy.commands
 * Date: 13.03.21
 * Project: FrameEconomy
 * Copyrighted by FrameDev
 */

public class BankCMD implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public BankCMD(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("bank").setExecutor(this);
        plugin.getCommand("bank").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            if (!CommandHelper.requirePermission(plugin, sender, "frameeconomy.bank.list")) return true;
            plugin.runAsync(() -> {
                List<String> banks = plugin.getVaultManager().getEconomy().getBanks();
                plugin.runSync(() -> sendList(sender, banks, "bank.list-empty"));
            });
            return true;
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                Player player = CommandHelper.requirePlayerWithPermission(plugin, sender, "frameeconomy.bank.create");
                if (player == null) return true;
                String bankName = args[1];
                plugin.runAsync(() -> {
                    if (plugin.getVaultManager().getEconomy().createBank(bankName, player).transactionSuccess()) {
                        plugin.sendMessageSync(player, "bank.create-success", "bank", bankName);
                    } else {
                        plugin.sendMessageSync(player, "bank.create-error", "bank", bankName);
                    }
                });
                return true;
            }

            if (args[0].equalsIgnoreCase("balance")) {
                Player player = CommandHelper.requirePlayerWithPermission(plugin, sender, "frameeconomy.bank.balance");
                if (player == null) return true;
                String bankName = args[1];
                String currency = plugin.getVaultManager().getEconomy().currencyNamePlural();
                plugin.runAsync(() -> {
                    if (!ensureBankExists(player, bankName)) return;
                    if (!ensureBankMember(player, bankName)) return;
                    plugin.sendMessageSync(player, "bank.balance",
                            "bank", bankName,
                            "amount", String.valueOf(plugin.getVaultManager().getEconomy().bankBalance(bankName).balance),
                            "currency", currency);
                });
                return true;
            }

            if (args[0].equalsIgnoreCase("remove")) {
                Player player = CommandHelper.requirePlayerWithPermission(plugin, sender, "frameeconomy.bank.remove");
                if (player == null) return true;
                String bankName = args[1];
                plugin.runAsync(() -> {
                    if (!ensureBankExists(player, bankName)) return;
                    if (!ensureBankOwner(player, bankName)) return;
                    if (plugin.getVaultManager().getEconomy().deleteBank(bankName).transactionSuccess()) {
                        plugin.sendMessageSync(player, "bank.remove-success", "bank", bankName);
                    } else {
                        plugin.sendMessageSync(player, "bank.remove-error", "bank", bankName);
                    }
                });
                return true;
            }

            if (args[0].equalsIgnoreCase("listmembers")) {
                Player player = CommandHelper.requirePlayerWithPermission(plugin, sender, "frameeconomy.bank.listmembers");
                if (player == null) return true;
                String bankName = args[1];
                plugin.runAsync(() -> {
                    if (!ensureBankExists(player, bankName)) return;
                    if (!ensureBankMember(player, bankName)) return;
                    List<String> bankMembers = new ArrayList<>(plugin.getVaultManager().getBankMembers(bankName));
                    plugin.runSync(() -> sendList(player, bankMembers, "bank.members-empty"));
                });
                return true;
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("deposit")) {
                Player player = CommandHelper.requirePlayerWithPermission(plugin, sender, "frameeconomy.bank.deposit");
                if (player == null) return true;
                String bankName = args[1];
                Double parsedAmount = plugin.parsePositiveAmount(player, args[2]);
                if (parsedAmount == null) return true;
                double amount = parsedAmount;
                String currency = plugin.getVaultManager().getEconomy().currencyNamePlural();
                plugin.runAsync(() -> {
                    if (!ensureBankExists(player, bankName)) return;
                    if (!plugin.getVaultManager().getEconomy().has(player, amount)) {
                        plugin.sendMessageSync(player, "general.not-enough-money");
                        return;
                    }
                    if (!plugin.getVaultManager().getEconomy().withdrawPlayer(player, amount).transactionSuccess()) {
                        plugin.sendMessageSync(player, "bank.deposit-error", "bank", bankName);
                        return;
                    }
                    if (plugin.getVaultManager().getEconomy().bankDeposit(bankName, amount).transactionSuccess()) {
                        plugin.sendMessageSync(player, "bank.deposit-success",
                                "bank", bankName,
                                "amount", String.valueOf(amount),
                                "currency", currency);
                    } else {
                        plugin.getVaultManager().getEconomy().depositPlayer(player, amount);
                        plugin.sendMessageSync(player, "bank.deposit-error", "bank", bankName);
                    }
                });
                return true;
            }

            if (args[0].equalsIgnoreCase("withdraw")) {
                Player player = CommandHelper.requirePlayerWithPermission(plugin, sender, "frameeconomy.bank.withdraw");
                if (player == null) return true;
                String bankName = args[1];
                Double parsedAmount = plugin.parsePositiveAmount(player, args[2]);
                if (parsedAmount == null) return true;
                double amount = parsedAmount;
                String currency = plugin.getVaultManager().getEconomy().currencyNamePlural();
                plugin.runAsync(() -> {
                    if (!ensureBankExists(player, bankName)) return;
                    if (!ensureBankMember(player, bankName)) return;
                    if (!plugin.getVaultManager().getEconomy().bankHas(bankName, amount).transactionSuccess()) {
                        plugin.sendMessageSync(player, "bank.not-enough-money");
                        return;
                    }
                    if (plugin.getVaultManager().getEconomy().bankWithdraw(bankName, amount).transactionSuccess()) {
                        plugin.getVaultManager().getEconomy().depositPlayer(player, amount);
                        plugin.sendMessageSync(player, "bank.withdraw-success",
                                "bank", bankName,
                                "amount", String.valueOf(amount),
                                "currency", currency);
                    } else {
                        plugin.sendMessageSync(player, "bank.withdraw-error", "bank", bankName);
                    }
                });
                return true;
            }

            if (args[0].equalsIgnoreCase("addmember") || args[0].equalsIgnoreCase("removemember")) {
                boolean addMember = args[0].equalsIgnoreCase("addmember");
                String permission = addMember ? "frameeconomy.bank.addmember" : "frameeconomy.bank.removemember";
                Player player = CommandHelper.requirePlayerWithPermission(plugin, sender, permission);
                if (player == null) return true;
                String bankName = args[1];
                OfflinePlayer offline = Bukkit.getOfflinePlayer(args[2]);
                String offlineName = offline.getName();
                plugin.runAsync(() -> {
                    if (!ensureBankExists(player, bankName)) return;
                    if (!ensureBankOwner(player, bankName)) return;
                    if (addMember) {
                        plugin.getVaultManager().addBankMember(bankName, offline);
                        plugin.sendMessageSync(player, "bank.add-member-success",
                                "bank", bankName,
                                "player", offlineName);
                    } else {
                        plugin.getVaultManager().removeBankMember(bankName, offline);
                        plugin.sendMessageSync(player, "bank.remove-member-success",
                                "bank", bankName,
                                "player", offlineName);
                    }
                });
                return true;
            }
        }

        plugin.sendMessage(sender, "bank.usage");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> cmds = new ArrayList<String>(Arrays.asList("remove", "create", "balance", "withdraw", "deposit", "addmember", "removemember", "listmembers", "list"));
            List<String> completions = new ArrayList<>();
            for (String s : cmds) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(s);
                }
            }
            Collections.sort(completions);
            return completions;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("list")) return new ArrayList<>();
            if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("remove"))
                return new ArrayList<>(Collections.singletonList("<BANKNAME>"));

            List<String> banksList = new ArrayList<>();
            OfflinePlayer offlineSender = sender instanceof OfflinePlayer ? (OfflinePlayer) sender : null;
            for (String bank : plugin.getVaultManager().getEconomy().getBanks()) {
                if (offlineSender == null || isBankOwnerOrMember(bank, offlineSender)) {
                    banksList.add(bank);
                }
            }
            return CommandHelper.matching(banksList, args[1]);
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("listmembers") || args[0].equalsIgnoreCase("info"))
                return new ArrayList<>();
            if (args[0].equalsIgnoreCase("balance")) return new ArrayList<>();
            if (args[0].equalsIgnoreCase("addmember") || args[0].equalsIgnoreCase("removemember")) {
                List<String> players = new ArrayList<>();
                for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                    if (offlinePlayer.getName() != null) {
                        players.add(offlinePlayer.getName());
                    }
                }
                return CommandHelper.matching(players, args[2]);
            }
            if (sender instanceof OfflinePlayer) {
                return new ArrayList<>(Collections.singletonList(plugin.getVaultManager().getEconomy().getBalance((OfflinePlayer) sender) + ""));
            }
        }
        return null;
    }

    private boolean bankExists(String bankName) {
        return plugin.getVaultManager().getEconomy().getBanks().contains(bankName);
    }

    private boolean ensureBankExists(Player player, String bankName) {
        if (bankExists(bankName)) return true;
        plugin.sendMessageSync(player, "bank.not-found", "bank", bankName);
        return false;
    }

    private boolean isBankOwnerOrMember(String bankName, OfflinePlayer player) {
        return plugin.getVaultManager().getEconomy().isBankOwner(bankName, player).transactionSuccess()
                || plugin.getVaultManager().getEconomy().isBankMember(bankName, player).transactionSuccess();
    }

    private boolean ensureBankMember(Player player, String bankName) {
        if (isBankOwnerOrMember(bankName, player)) return true;
        plugin.sendMessageSync(player, "bank.not-member");
        return false;
    }

    private boolean ensureBankOwner(Player player, String bankName) {
        if (plugin.getVaultManager().getEconomy().isBankOwner(bankName, player).transactionSuccess()) return true;
        plugin.sendMessageSync(player, "bank.not-owner");
        return false;
    }

    private void sendList(CommandSender sender, List<String> values, String emptyMessage) {
        plugin.sendMessage(sender, "bank.list-border");
        if (values == null || values.isEmpty()) {
            plugin.sendMessage(sender, emptyMessage);
        } else {
            plugin.sendMessage(sender, "bank.list-values", "values", CommandHelper.join(values));
        }
        plugin.sendMessage(sender, "bank.list-border");
    }
}
