package de.framedev.frameeconomy.commands;

import de.framedev.frameeconomy.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        PluginCommand economyCommand = Objects.requireNonNull(plugin.getCommand("economy"), "Command 'economy' is not defined in plugin.yml");
        economyCommand.setExecutor(this);
        economyCommand.setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
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
        if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
            handleBalanceChange(sender, args);
            return true;
        }
        plugin.sendMessage(sender, "economy.usage");
        return true;
    }

    private void handleBalanceChange(CommandSender sender, String[] args) {
        if (args.length != 2 && args.length != 3) {
            plugin.sendMessage(sender, "economy.usage");
            return;
        }

        String action = args[0].toLowerCase();
        OfflinePlayer target;
        boolean selfTarget = args.length == 2;
        if (selfTarget) {
            Player player = CommandHelper.requirePlayerWithPermission(plugin, sender, "frameeconomy.eco." + action);
            if (player == null) return;
            target = player;
        } else {
            if (!CommandHelper.requirePermission(plugin, sender, "frameeconomy.eco." + action + ".others")) return;
            target = plugin.resolveOfflinePlayer(args[2]);
        }

        Double parsedAmount = plugin.parsePositiveAmount(sender, args[1]);
        if (parsedAmount == null) return;
        double amount = parsedAmount;
        String currency = plugin.getVaultManager().getEconomy().currencyNamePlural();
        String targetName = target.getName();

        plugin.runDatabaseAsync(() -> {
            boolean success = applyBalanceChange(action, target, amount);
            plugin.runSync(() -> {
                if (!success) {
                    plugin.sendMessage(sender, "economy.change-failed",
                            "player", targetName,
                            "amount", String.valueOf(amount),
                            "currency", currency);
                    return;
                }
                if (target.isOnline()) {
                    plugin.sendMessage((Player) target, "economy." + action + "-own",
                            "amount", String.valueOf(amount),
                            "currency", currency);
                }
                if (!selfTarget) {
                    plugin.sendMessage(sender, "economy." + action + "-other",
                            "player", targetName,
                            "amount", String.valueOf(amount),
                            "currency", currency);
                }
            });
        });
    }

    private boolean applyBalanceChange(String action, OfflinePlayer player, double amount) {
        if (action.equalsIgnoreCase("set")) {
            return plugin.setPlayerBalance(player, amount);
        }
        if (action.equalsIgnoreCase("add")) {
            return plugin.addPlayerBalance(player, amount);
        }
        if (action.equalsIgnoreCase("remove")) {
            return plugin.removePlayerBalance(player, amount);
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if ("status".startsWith(args[0].toLowerCase())) completions.add("status");
            if ("set".startsWith(args[0].toLowerCase())) completions.add("set");
            if ("add".startsWith(args[0].toLowerCase())) completions.add("add");
            if ("remove".startsWith(args[0].toLowerCase())) completions.add("remove");
            return CommandHelper.matching(completions, args[0]);
        } else if (args.length == 3 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {
            List<String> players = new ArrayList<>();
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                if (offlinePlayer.getName() != null) {
                    players.add(offlinePlayer.getName());
                }
            }
            return CommandHelper.matching(players, args[2]);
        }
        return null;
    }
}
