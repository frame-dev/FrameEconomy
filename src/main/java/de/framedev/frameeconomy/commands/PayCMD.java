package de.framedev.frameeconomy.commands;

import de.framedev.frameeconomy.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.frameeconomy.commands
 * Date: 15.02.21
 * Project: FrameEconomy
 * Copyrighted by FrameDev
 */

public class PayCMD implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public PayCMD(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("pay").setExecutor(this);
        plugin.getCommand("pay").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 2) {
            if(sender instanceof Player) {
                if(sender.hasPermission("frameeconomy.pay")) {
                    Player player = (Player) sender;
                    double amount = Double.parseDouble(args[0]);
                    amount = Double.parseDouble(String.format("%.4f",amount));
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        if (plugin.getVaultManager().getEconomy().has(player, amount)) {
                            boolean[] success = {false,false};
                            success[0] = plugin.getVaultManager().getEconomy().depositPlayer(target, amount).transactionSuccess();
                            success[1] = plugin.getVaultManager().getEconomy().withdrawPlayer(player, amount).transactionSuccess();
                            if(success[0] && success[1]) {
                                player.sendMessage("§aYou give §6" + target.getName() + " " + amount + plugin.getVaultManager().getEconomy().currencyNamePlural());
                                target.sendMessage("§aYou got from §6" + player.getName() + " " + amount + plugin.getVaultManager().getEconomy().currencyNamePlural());
                            }
                        } else {
                            player.sendMessage("§cNot enought Money!");
                        }
                    } else {
                        player.sendMessage("§cThis Player isn't Online!");
                    }
                } else {
                    sender.sendMessage("§cNo Permissions!");
                }
            } else {
                sender.sendMessage("§cOnly Player can use this Command!");
            }

        } else if(args.length == 3) {
            double betrag;
            if(sender instanceof Player) {
                if (sender.hasPermission("frameeconomy.pay")) {
                    Player player = (Player) sender;
                    double amount = Double.parseDouble(args[0]);
                    double percent = Double.parseDouble(args[2]);
                    betrag = amount;
                    Player target = Bukkit.getPlayer(args[1]);
                    double sum = Math.round(betrag * percent);
                    sum /= 100.0;
                    if (target != null) {
                        if (plugin.getVaultManager().getEconomy().has(player, amount)) {
                            boolean[] success = {false,false};
                            amount = Double.parseDouble(String.format("%.4f",amount + sum));
                            success[0] = plugin.getVaultManager().getEconomy().depositPlayer(target, amount).transactionSuccess();
                            success[1] = plugin.getVaultManager().getEconomy().withdrawPlayer(player, amount).transactionSuccess();
                            if(success[0] && success[1]) {
                                player.sendMessage("§aPercent : §6" + sum);
                                player.sendMessage("§aYou give §6" + target.getName() + " " + amount + plugin.getVaultManager().getEconomy().currencyNamePlural());
                                target.sendMessage("§aYou got from §6" + player.getName() + " " + amount + plugin.getVaultManager().getEconomy().currencyNamePlural());
                            }
                        } else {
                            player.sendMessage("§cNot enought Money!");
                        }
                    } else {
                        player.sendMessage("§cThis Player isn't Online!");
                    }
                } else {
                    sender.sendMessage("§cNo Permissions!");
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 1) {
            ArrayList<String> empty = new ArrayList<>();
            empty.add(plugin.getVaultManager().getEconomy().getBalance((OfflinePlayer) sender) + "");
            Collections.sort(empty);
            return empty;
        }
        return null;
    }
}
