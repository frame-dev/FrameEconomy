package de.framedev.frameeconomy.commands;

import de.framedev.frameeconomy.main.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class CommandHelper {

    private CommandHelper() {
    }

    static Player requirePlayer(Main plugin, CommandSender sender) {
        if (sender instanceof Player) return (Player) sender;
        plugin.sendMessage(sender, "general.only-player");
        return null;
    }

    static boolean requirePermission(Main plugin, CommandSender sender, String permission) {
        if (sender.hasPermission(permission)) return true;
        plugin.sendMessage(sender, "general.no-permission");
        return false;
    }

    static Player requirePlayerWithPermission(Main plugin, CommandSender sender, String permission) {
        Player player = requirePlayer(plugin, sender);
        if (player == null) return null;
        return requirePermission(plugin, player, permission) ? player : null;
    }

    static double roundAmount(double amount) {
        return Math.round(amount * 10000.0) / 10000.0;
    }

    static String join(List<String> values) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < values.size(); ++i) {
            stringBuilder.append(values.get(i));
            if (i < values.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    static List<String> matching(List<String> values, String prefix) {
        List<String> completions = new ArrayList<>();
        for (String value : values) {
            if (value.toLowerCase().startsWith(prefix.toLowerCase())) {
                completions.add(value);
            }
        }
        Collections.sort(completions);
        return completions;
    }
}
