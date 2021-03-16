package de.framedev.frameeconomy.commands;

import de.framedev.frameeconomy.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.frameeconomy.commands
 * Date: 13.03.21
 * Project: FrameEconomy
 * Copyrighted by FrameDev
 */

public class BankCMD implements CommandExecutor {

    private final Main plugin;

    public BankCMD(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("bank").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("frameeconomy.bank.create")) {
                        if (plugin.getVaultManager().getEco().createBank(args[1], player).transactionSuccess()) {
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
                        if (plugin.getVaultManager().getEco().getBanks().contains(bankName)) {
                            if (plugin.getVaultManager().getEco().isBankOwner(bankName, player).transactionSuccess() || plugin.getVaultManager().getEco().isBankMember(bankName, player).transactionSuccess()) {
                                player.sendMessage(plugin.getPrefix() + "§aThe Balance from the Bank is §6" + plugin.getVaultManager().getEco().bankBalance(bankName).balance);
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
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("deposit")) {
                String bankName = args[1];
                double amount = Double.parseDouble(args[2]);
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("frameeconomy.bank.deposit")) {
                        if (plugin.getVaultManager().getEco().getBanks().contains(bankName)) {
                            if (plugin.getVaultManager().getEco().has(player, amount)) {
                                plugin.getVaultManager().getEco().withdrawPlayer(player, amount);
                                if (plugin.getVaultManager().getEco().bankDeposit(bankName, amount).transactionSuccess()) {
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
                        if (plugin.getVaultManager().getEco().getBanks().contains(bankName)) {
                            if (plugin.getVaultManager().getEco().isBankOwner(bankName, player).transactionSuccess() || plugin.getVaultManager().getEco().isBankMember(bankName, player).transactionSuccess()) {
                                if (plugin.getVaultManager().getEco().bankHas(bankName, amount).transactionSuccess()) {
                                    plugin.getVaultManager().getEco().depositPlayer(player, amount);
                                    plugin.getVaultManager().getEco().bankWithdraw(bankName, amount);
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
                        if (plugin.getVaultManager().getEco().getBanks().contains(bankName)) {
                            if (plugin.getVaultManager().getEco().isBankOwner(bankName, player).transactionSuccess()) {
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
                        if (plugin.getVaultManager().getEco().getBanks().contains(bankName)) {
                            if (plugin.getVaultManager().getEco().isBankOwner(bankName, player).transactionSuccess()) {
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
        }
        return false;
    }
}