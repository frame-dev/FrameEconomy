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
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                if (sender.hasPermission("frameeconomy.bank.list")) {
                    List<String> banks = plugin.getVaultManager().getEconomy().getBanks();
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < banks.size(); ++i) {
                        stringBuilder.append(banks.get(i));
                        if (i < banks.size() - 1) {
                            stringBuilder.append(", ");
                        }
                    }
                    sender.sendMessage(plugin.getPrefix() + "§6<<<===>>>");
                    sender.sendMessage(plugin.getPrefix() + "§a" + stringBuilder.toString());
                    sender.sendMessage(plugin.getPrefix() + "§6<<<===>>>");
                } else {
                    sender.sendMessage(plugin.getPrefix() + "§cNo Permissions!");
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("frameeconomy.bank.create")) {
                        if (plugin.getVaultManager().getEconomy().createBank(args[1], player).transactionSuccess()) {
                            player.sendMessage(plugin.getPrefix() + "§aBank Successfully Created!");
                        } else {
                            player.sendMessage(plugin.getPrefix() + "§cError while Creating Bank!");
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix() + "§cNo Permissions!");
                    }
                }
            }
            if (args[0].equalsIgnoreCase("balance")) {
                String bankName = args[1];
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("frameeconomy.bank.balance")) {
                        if (plugin.getVaultManager().getEconomy().getBanks().contains(bankName)) {
                            if (plugin.getVaultManager().getEconomy().isBankOwner(bankName, player).transactionSuccess() || plugin.getVaultManager().getEconomy().isBankMember(bankName, player).transactionSuccess()) {
                                player.sendMessage(plugin.getPrefix() + "§aThe Balance from the Bank is §6" + plugin.getVaultManager().getEconomy().bankBalance(bankName).balance);
                            } else {
                                player.sendMessage(plugin.getPrefix() + "§cYou are not a BankMember or the Owner!");
                            }
                        } else {
                            player.sendMessage(plugin.getPrefix() + "§cThis Bank doesn't exist!");
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix() + "§cNo Permissions!");
                    }
                }
            }
            if (args[0].equalsIgnoreCase("remove")) {
                String bankName = args[1];
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("frameeconomy.bank.remove")) {
                        if (plugin.getVaultManager().getEconomy().getBanks().contains(bankName)) {
                            if (plugin.getVaultManager().getEconomy().isBankOwner(bankName, player).transactionSuccess()) {
                                if (plugin.getVaultManager().getEconomy().deleteBank(bankName).transactionSuccess()) {
                                    player.sendMessage(plugin.getPrefix() + "§cBank successfully deleted!");
                                } else {
                                    player.sendMessage(plugin.getPrefix() + "§cError while deleting Bank!");
                                }
                            } else {
                                player.sendMessage(plugin.getPrefix() + "§cYou are not the Owner!");
                            }
                        } else {
                            player.sendMessage(plugin.getPrefix() + "§cThis Bank doesn't exist!");
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix() + "§cNo Permissions!");
                    }
                }
            }
            if (args[0].equalsIgnoreCase("listmembers")) {
                String bankName = args[1];
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("frameeconomy.bank.listmembers")) {
                        if (plugin.getVaultManager().getEconomy().getBanks().contains(bankName)) {
                            if (plugin.getVaultManager().getEconomy().isBankOwner(bankName, player).transactionSuccess() || plugin.getVaultManager().getEconomy().isBankMember(bankName, player).transactionSuccess()) {
                                List<String> bankMembers = new ArrayList<>(plugin.getVaultManager().getBankMembers(bankName));
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < bankMembers.size(); ++i) {
                                    stringBuilder.append(bankMembers.get(i));
                                    if (i < bankMembers.size() - 1) {
                                        stringBuilder.append(", ");
                                    }
                                }
                                player.sendMessage(plugin.getPrefix() + "§6<<<===>>>");
                                player.sendMessage(plugin.getPrefix() + "§a" + stringBuilder.toString());
                                player.sendMessage(plugin.getPrefix() + "§6<<<===>>>");
                            }
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix() + "§cNo Permissions!");
                    }
                }
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("deposit")) {
                String bankName = args[1];
                double amount = Double.parseDouble(args[2]);
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("frameeconomy.bank.deposit")) {
                        if (plugin.getVaultManager().getEconomy().getBanks().contains(bankName)) {
                            if (plugin.getVaultManager().getEconomy().has(player, amount)) {
                                plugin.getVaultManager().getEconomy().withdrawPlayer(player, amount);
                                if (plugin.getVaultManager().getEconomy().bankDeposit(bankName, amount).transactionSuccess()) {
                                    player.sendMessage(plugin.getPrefix() + "§6" + amount + " §awas successfully transferred to the bank!");
                                } else {
                                    player.sendMessage(plugin.getPrefix() + "§cError while Deposit to the Bank!");
                                }
                            } else {
                                player.sendMessage(plugin.getPrefix() + "§cNot enougt Money!");
                            }
                        } else {
                            player.sendMessage(plugin.getPrefix() + "§cThis Bank doesn't exist!");
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix() + "§cNo Permissions!");
                    }
                }
            } else if (args[0].equalsIgnoreCase("withdraw")) {
                String bankName = args[1];
                double amount = Double.parseDouble(args[2]);
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("frameeconomy.bank.withdraw")) {
                        if (plugin.getVaultManager().getEconomy().getBanks().contains(bankName)) {
                            if (plugin.getVaultManager().getEconomy().isBankOwner(bankName, player).transactionSuccess() || plugin.getVaultManager().getEconomy().isBankMember(bankName, player).transactionSuccess()) {
                                if (plugin.getVaultManager().getEconomy().bankHas(bankName, amount).transactionSuccess()) {
                                    plugin.getVaultManager().getEconomy().depositPlayer(player, amount);
                                    plugin.getVaultManager().getEconomy().bankWithdraw(bankName, amount);
                                    player.sendMessage(plugin.getPrefix() + "§aYou successfully withdrew §6" + amount + " §afrom the bank!");
                                } else {
                                    player.sendMessage(plugin.getPrefix() + "§cThe Bank has not enought Money!");
                                }
                            } else {
                                player.sendMessage(plugin.getPrefix() + "§cYou are not a BankMember or the Owner!");
                            }
                        } else {
                            player.sendMessage(plugin.getPrefix() + "§cThis Bank doesn't exist!");
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix() + "§cNo Permissions!");
                    }
                }
            } else if (args[0].equalsIgnoreCase("addmember")) {
                String bankName = args[1];
                OfflinePlayer offline = Bukkit.getOfflinePlayer(args[2]);
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("frameeconomy.bank.addmember")) {
                        if (plugin.getVaultManager().getEconomy().getBanks().contains(bankName)) {
                            if (plugin.getVaultManager().getEconomy().isBankOwner(bankName, player).transactionSuccess()) {
                                plugin.getVaultManager().addBankMember(bankName, offline);
                                player.sendMessage(plugin.getPrefix() + "§6" + offline.getName() + " §ais now Successfully a Member of your Bank!");
                            } else {
                                player.sendMessage(plugin.getPrefix() + "§cYou are not the Bank Owner!");
                            }
                        } else {
                            player.sendMessage(plugin.getPrefix() + "§cThis Bank doesn't exist!");
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix() + "§cNo Permissions!");
                    }
                }
            } else if (args[0].equalsIgnoreCase("removemember")) {
                String bankName = args[1];
                OfflinePlayer offline = Bukkit.getOfflinePlayer(args[2]);
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("frameeconomy.bank.removemember")) {
                        if (plugin.getVaultManager().getEconomy().getBanks().contains(bankName)) {
                            if (plugin.getVaultManager().getEconomy().isBankOwner(bankName, player).transactionSuccess()) {
                                plugin.getVaultManager().removeBankMember(bankName, offline);
                                player.sendMessage(plugin.getPrefix() + "§6" + offline.getName() + " §ais no longer a member of your Bank!");
                            } else {
                                player.sendMessage(plugin.getPrefix() + "§cYou are not the Bank Owner!");
                            }
                        } else {
                            player.sendMessage(plugin.getPrefix() + "§cThis Bank doesn't exist!");
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix() + "§cNo Permissions!");
                    }
                }
            }
        } else {
            sender.sendMessage(plugin.getPrefix() + "§cPlease use §6/bank create <Name> §cor §6/bank deposit <Name> §cor " +
                    "§6/bank withdraw <Name> §cor §6/bank addmember <Name> <PlayerName> §cor §6/bank removemember <Name> <PlayerName> §cor" +
                    " §6/bank balance <Name>§4§l!");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> cmds = new ArrayList<String>(Arrays.asList("remove", "create", "balance", "withdraw", "deposit", "addmember", "removemember", "listmembers", "list"));
            List<String> empty = new ArrayList<>();
            for (String s : cmds) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                    empty.add(s);
                }
            }
            Collections.sort(empty);
            return empty;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("list")) return new ArrayList<>();
            if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("remove"))
                return new ArrayList<>(Collections.singletonList("<BANKNAME>"));
            List<String> banksList = new ArrayList<>();
            List<String> empty = new ArrayList<>();
            for (String banks : plugin.getVaultManager().getEconomy().getBanks()) {
                if (plugin.getVaultManager().getEconomy().isBankMember(banks, (OfflinePlayer) sender).transactionSuccess() || plugin.getVaultManager().getEconomy().isBankOwner(banks, (OfflinePlayer) sender).transactionSuccess()) {
                    banksList.add(banks);
                }
            }
            for (String s : banksList) {
                if (s.toLowerCase().startsWith(args[1].toLowerCase()))
                    empty.add(s);
            }
            Collections.sort(empty);
            return empty;
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("listmembers") || args[0].equalsIgnoreCase("info"))
                return new ArrayList<>();
            if (args[0].equalsIgnoreCase("balance")) return new ArrayList<>();
            if (args[0].equalsIgnoreCase("addmember") || args[0].equalsIgnoreCase("removemember")) {
                List<String> players = new ArrayList<>();
                List<String> empty = new ArrayList<>();
                for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                    players.add(offlinePlayer.getName());
                }
                for (String s : players) {
                    if (s.toLowerCase().startsWith(args[2].toLowerCase()))
                        empty.add(s);
                }
                Collections.sort(empty);
                return empty;
            }
            List<String> empty = new ArrayList<>();
            empty.add(plugin.getVaultManager().getEconomy().getBalance((OfflinePlayer) sender) + "");
            return empty;
        }
        return null;
    }
}
