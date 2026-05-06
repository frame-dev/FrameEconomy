package de.framedev.frameeconomy.commands;

import de.framedev.frameeconomy.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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
        plugin.getLogger().log(Level.INFO, "Loaded");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("balance")) {
            if (args.length == 0) {
                Player player = CommandHelper.requirePlayerWithPermission(plugin, sender, "frameeconomy.balance");
                if (player == null) return true;
                String currency = plugin.getVaultManager().getEconomy().currencyNamePlural();
                plugin.runAsync(() -> plugin.sendMessageSync(player, "balance.own",
                        "amount", String.valueOf(plugin.getVaultManager().getEconomy().getBalance(player)),
                        "currency", currency));
            } else if (args.length == 1) {
                if (!CommandHelper.requirePermission(plugin, sender, "frameeconomy.balance.others")) return true;
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                if (player != null) {
                    String currency = plugin.getVaultManager().getEconomy().currencyNamePlural();
                    String playerName = player.getName();
                    plugin.runAsync(() -> plugin.sendMessageSync(sender, "balance.other",
                            "player", playerName,
                            "amount", String.valueOf(plugin.getVaultManager().getEconomy().getBalance(player)),
                            "currency", currency));
                } else {
                    plugin.sendMessage(sender, "general.player-not-found", "player", args[0]);
                }
            }
        }
        if (command.getName().equalsIgnoreCase("balancetop")) {
            if (!CommandHelper.requirePermission(plugin, sender, "frameeconomy.balancetop")) return true;
            OfflinePlayer[] players = Bukkit.getOfflinePlayers();
            List<String> banks = new ArrayList<>(plugin.getVaultManager().getEconomy().getBanks());
            plugin.runAsync(() -> {
                HashMap<String, Double> mostplayers = new HashMap<>();
                ValueComparator bvc = new ValueComparator(mostplayers);
                TreeMap<String, Double> sorted_map = new TreeMap<>(bvc);

                for (OfflinePlayer offlinePlayer : players) {
                    if (offlinePlayer.getName() == null) continue;
                    double balance = plugin.getVaultManager().getEconomy().getBalance(offlinePlayer);
                    if (!banks.isEmpty()) {
                        for (String bank : banks) {
                            if (plugin.getVaultManager().getEconomy().isBankMember(bank, offlinePlayer).transactionSuccess()
                                    || plugin.getVaultManager().getEconomy().isBankOwner(bank, offlinePlayer).transactionSuccess()) {
                                balance += plugin.getVaultManager().getEconomy().bankBalance(bank).balance;
                            }
                        }
                    }
                    mostplayers.put(offlinePlayer.getName(), balance);
                }
                sorted_map.putAll(mostplayers);
                int i = 0;
                for (Map.Entry<String, Double> e : sorted_map.entrySet()) {
                    i++;
                    plugin.sendMessageSync(sender, "balance.top-entry",
                            "position", String.valueOf(i),
                            "player", e.getKey(),
                            "amount", String.valueOf(e.getValue()));
                    if (i == 3) {
                        break;
                    }
                }
            });
            return true;
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
