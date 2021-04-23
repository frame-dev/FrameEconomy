package de.framedev.frameeconomy.utils;

import de.framedev.frameeconomy.main.Main;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.frameeconomy.utils
 * ClassName SchedulerManager
 * Date: 23.04.21
 * Project: FrameEconomy
 * Copyrighted by FrameDev
 */

public class SchedulerManager extends BukkitRunnable {

    public SchedulerManager() {
        Main.getInstance().getLogger().log(Level.INFO, "Scheduler Started");
    }

    private void updateAccounts() {
        Main.getInstance().getVaultManager().setAccounts(Main.getInstance().getRegisterManager().getVaultProvider().accounts());
    }

    @Override
    public void run() {
        updateAccounts();
    }
}
