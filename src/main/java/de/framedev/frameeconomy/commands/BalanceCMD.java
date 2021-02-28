package de.framedev.frameeconomy.commands;

import de.framedev.frameeconomy.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        plugin.getLogger().log(Level.INFO,"Loaded");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {
            if(sender instanceof Player) {
                if(sender.hasPermission("frameeconomy.balance")) {
                    Player player = (Player) sender;
                    player.sendMessage("§aYour Money §6" + plugin.getVaultManager().getEco().getBalance(player) + plugin.getVaultManager().getEco().currencyNamePlural());
                } else {
                    sender.sendMessage("§cNo Permissions!");
                }
            } else {
                sender.sendMessage("§cOnly Player can use this Command!");
            }
        } else if(args.length == 1) {
            if(sender.hasPermission("frameeconomy.balance.others")) {
                Player player = Bukkit.getPlayer(args[0]);
                if(player != null) {
                    sender.sendMessage("§aMoney from §6" + player.getName() + " §ais §6" + plugin.getVaultManager().getEco().getBalance(player) + plugin.getVaultManager().getEco().currencyNamePlural());
                } else {
                    sender.sendMessage("§cThis Player isn't Online! §6" + args[0]);
                }
            } else {
                sender.sendMessage("§cNo Permissions!");
            }
        }
        return false;
    }
}
