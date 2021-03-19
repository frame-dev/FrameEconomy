package de.framedev.frameeconomy.commands;

import de.framedev.frameeconomy.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.frameeconomy.commands
 * Date: 15.02.21
 * Project: FrameEconomy
 * Copyrighted by FrameDev
 */

public class BalanceCMD implements CommandExecutor {

    private final Main plugin;

    public BalanceCMD(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("balance").setExecutor(this::onCommand);
        plugin.getCommand("balancetop").setExecutor(this::onCommand);
        plugin.getLogger().log(Level.INFO,"Loaded");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("balance")) {
            if (args.length == 0) {
                if (sender instanceof Player) {
                    if (sender.hasPermission("frameeconomy.balance")) {
                        Player player = (Player) sender;
                        player.sendMessage("§aYour Money §6" + plugin.getVaultManager().getEconomy().getBalance(player) + plugin.getVaultManager().getEconomy().currencyNamePlural());
                    } else {
                        sender.sendMessage("§cNo Permissions!");
                    }
                } else {
                    sender.sendMessage("§cOnly Player can use this Command!");
                }
            } else if (args.length == 1) {
                if (sender.hasPermission("frameeconomy.balance.others")) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                    if (player != null) {
                        sender.sendMessage("§aMoney from §6" + player.getName() + " §ais §6" + plugin.getVaultManager().getEconomy().getBalance(player) + plugin.getVaultManager().getEconomy().currencyNamePlural());
                    } else {
                        sender.sendMessage("§cThis Player isn't Online! §6" + args[0]);
                    }
                } else {
                    sender.sendMessage("§cNo Permissions!");
                }
            }
        }
        if (command.getName().equalsIgnoreCase("balancetop")) {
            if (sender.hasPermission("frameeconomy.balancetop")) {
                HashMap<String, Double> mostplayers = new HashMap<>();
                ValueComparator bvc = new ValueComparator(mostplayers);
                TreeMap<String, Double> sorted_map = new TreeMap<String, Double>(bvc);
                for (Player all : Bukkit.getOnlinePlayers()) {
                    mostplayers.put(all.getName(), plugin.getVaultManager().getEconomy().getBalance(all));
                }
                for (OfflinePlayer alloffline : Bukkit.getOfflinePlayers()) {
                    mostplayers.put(alloffline.getName(), plugin.getVaultManager().getEconomy().getBalance(alloffline));
                }
                sorted_map.putAll(mostplayers);
                int i = 0;
                for (Map.Entry<String, Double> e : sorted_map.entrySet()) {
                    i++;
                    sender.sendMessage("§a" + i + "st [§6" + e.getKey() + " §b: " + e.getValue() + "§a]");
                    if (i == 3) {
                        break;
                    }
                }
                return true;
            }
        }
        return false;
    }
    static class ValueComparator implements Comparator<String> {


        Map<String, Double> base;

        public ValueComparator(Map<String, Double> base) {
            this.base = base;
        }


        public int compare(String a, String b) {
            if (base.get(a) >= base.get(b)) {
                return -1;
            } else {
                return 1;
            } // returning 0 would merge keys
        }
    }
}
