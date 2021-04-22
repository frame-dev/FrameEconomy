package de.framedev.frameeconomy.commands;

import de.framedev.frameeconomy.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.frameeconomy.commands
 * Date: 15.02.21
 * Project: FrameEconomy
 * Copyrighted by FrameDev
 */

public class EcoCMD implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public EcoCMD(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("economy").setExecutor(this);
        plugin.getCommand("economy").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args[0].equalsIgnoreCase("set")) {
            if (args.length == 2) {
                if (sender instanceof Player) {
                    if (sender.hasPermission("frameeconomy.eco.set")) {
                        Player player = (Player) sender;
                        double amount = Double.parseDouble(args[1]);
                        plugin.getVaultManager().getEconomy().withdrawPlayer(player, plugin.getVaultManager().getEconomy().getBalance(player));
                        plugin.getVaultManager().getEconomy().depositPlayer(player, amount);
                        player.sendMessage("§aYour Money has been set to §6" + amount + plugin.getVaultManager().getEconomy().currencyNamePlural());
                    } else {
                        sender.sendMessage(plugin.getPrefix() + "§cNo Permissions!");
                    }
                } else {
                    sender.sendMessage(plugin.getPrefix() + "§cOnly Player can use this Command!");
                }
            } else if (args.length == 3) {
                if (sender.hasPermission("frameeconomy.eco.set.others")) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[2]);
                    double amount = Double.parseDouble(args[1]);
                    plugin.getVaultManager().getEconomy().withdrawPlayer(player, plugin.getVaultManager().getEconomy().getBalance(player));
                    plugin.getVaultManager().getEconomy().depositPlayer(player, amount);
                    if (player.isOnline()) {
                        ((Player) player).sendMessage("§aYour Money has been set to §6" + amount + plugin.getVaultManager().getEconomy().currencyNamePlural());
                    }
                    sender.sendMessage("§aMoney from §6" + player.getName() + " §ahas been set to §6" + amount + plugin.getVaultManager().getEconomy().currencyNamePlural());
                } else {
                    sender.sendMessage(plugin.getPrefix() + "§cNo Permissions!");
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
