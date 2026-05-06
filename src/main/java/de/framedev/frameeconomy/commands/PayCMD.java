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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
        PluginCommand payCommand = Objects.requireNonNull(plugin.getCommand("pay"), "Command 'pay' is not defined in plugin.yml");
        payCommand.setExecutor(this);
        payCommand.setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            plugin.sendMessage(sender, "general.only-player");
            return true;
        }
        if (!CommandHelper.requirePermission(plugin, sender, "frameeconomy.pay")) return true;
        if (args.length != 2 && args.length != 3) {
            plugin.sendMessage(sender, "pay.usage");
            return true;
        }

        Player player = (Player) sender;
        Double parsedAmount = plugin.parsePositiveAmount(sender, args[0]);
        if (parsedAmount == null) return true;
        Double parsedPercent = args.length == 3 ? plugin.parsePositiveAmount(sender, args[2]) : Double.valueOf(0.0D);
        if (parsedPercent == null) return true;

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            plugin.sendMessage(player, "general.player-not-found", "player", args[1]);
            return true;
        }

        double percentBonus = args.length == 3 ? Math.round(parsedAmount * parsedPercent) / 100.0 : 0.0D;
        double amount = CommandHelper.roundAmount(parsedAmount + percentBonus);
        String targetName = target.getName();
        String playerName = player.getName();

        plugin.runDatabaseAsync(() -> {
            if (!plugin.getVaultManager().getEconomy().has(player, amount)) {
                plugin.sendMessageSync(player, "general.not-enough-money");
                return;
            }
            boolean withdrawn = plugin.getVaultManager().getEconomy().withdrawPlayer(player, amount).transactionSuccess();
            boolean deposited = withdrawn && plugin.getVaultManager().getEconomy().depositPlayer(target, amount).transactionSuccess();
            if (withdrawn && deposited) {
                if (args.length == 3) {
                    plugin.sendMessageSync(player, "pay.percent", "amount", String.valueOf(percentBonus));
                }
                plugin.sendMessageSync(player, "pay.sent",
                        "player", targetName,
                        "amount", String.valueOf(amount),
                        "currency", plugin.getVaultManager().getEconomy().currencyNamePlural());
                plugin.sendMessageSync(target, "pay.received",
                        "player", playerName,
                        "amount", String.valueOf(amount),
                        "currency", plugin.getVaultManager().getEconomy().currencyNamePlural());
            } else {
                if (withdrawn) {
                    plugin.getVaultManager().getEconomy().depositPlayer(player, amount);
                }
                plugin.sendMessageSync(player, "pay.failed");
            }
        });
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if(args.length == 1 && sender instanceof OfflinePlayer) {
            ArrayList<String> empty = new ArrayList<>();
            empty.add(plugin.getVaultManager().getEconomy().getBalance((OfflinePlayer) sender) + "");
            Collections.sort(empty);
            return empty;
        }
        return null;
    }
}
