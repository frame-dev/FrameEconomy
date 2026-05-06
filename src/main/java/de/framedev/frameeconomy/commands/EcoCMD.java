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
        if (args.length == 0) {
            plugin.sendMessage(sender, "economy.usage");
            return true;
        }
        if (args[0].equalsIgnoreCase("status")) {
            if (!CommandHelper.requirePermission(plugin, sender, "frameeconomy.eco.status")) return true;
            plugin.sendMessage(sender, "economy.status",
                    "enabled", String.valueOf(plugin.getServer().getPluginManager().isPluginEnabled("Vault")));
            return true;
        }
        if (args[0].equalsIgnoreCase("set")) {
            if (args.length == 2) {
                Player player = CommandHelper.requirePlayerWithPermission(plugin, sender, "frameeconomy.eco.set");
                if (player == null) return true;
                Double parsedAmount = plugin.parsePositiveAmount(sender, args[1]);
                if (parsedAmount == null) return true;
                double amount = parsedAmount;
                String currency = plugin.getVaultManager().getEconomy().currencyNamePlural();
                plugin.runDatabaseAsync(() -> {
                    plugin.setPlayerBalance(player, amount);
                    plugin.sendMessageSync(player, "economy.set-own",
                            "amount", String.valueOf(amount),
                            "currency", currency);
                });
            } else if (args.length == 3) {
                if (!CommandHelper.requirePermission(plugin, sender, "frameeconomy.eco.set.others")) return true;
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[2]);
                Double parsedAmount = plugin.parsePositiveAmount(sender, args[1]);
                if (parsedAmount == null) return true;
                double amount = parsedAmount;
                String currency = plugin.getVaultManager().getEconomy().currencyNamePlural();
                String playerName = player.getName();
                plugin.runDatabaseAsync(() -> {
                    plugin.setPlayerBalance(player, amount);
                    plugin.runSync(() -> {
                        if (player.isOnline()) {
                            plugin.sendMessage((Player) player, "economy.set-own",
                                    "amount", String.valueOf(amount),
                                    "currency", currency);
                        }
                        plugin.sendMessage(sender, "economy.set-other",
                                "player", playerName,
                                "amount", String.valueOf(amount),
                                "currency", currency);
                    });
                });
            } else {
                plugin.sendMessage(sender, "economy.usage");
            }
        } else {
            plugin.sendMessage(sender, "economy.usage");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if ("status".startsWith(args[0].toLowerCase())) completions.add("status");
            if ("set".startsWith(args[0].toLowerCase())) completions.add("set");
            return CommandHelper.matching(completions, args[0]);
        }
        return null;
    }
}
