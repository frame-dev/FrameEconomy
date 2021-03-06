package frameeconomy

import de.framedev.frameeconomy.main.Main
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.scheduler.BukkitRunnable
import java.util.logging.Level

/**
 * This Plugin was Created by FrameDev
 * Package : frameeconomy
 * Date: 28.02.21
 * Project: FrameEconomy
 * Copyrighted by FrameDev
 */
open class VaultProvider {

    var economy: Economy = Main.getInstance().vaultManager.eco

    constructor(plugin : Main) {
        plugin.logger.log(Level.INFO,"Runnable started!")
        runnable()
    }

    private fun runnable() {
        var ticksSec = Ticks.secToTicks(Main.getInstance().config.getLong("PayLoad.Time.Sec"))
        var ticksMin = Ticks.minToTicks(Main.getInstance().config.getLong("PayLoad.Time.Min"))
        var ticksHour = Ticks.hourToTicks(Main.getInstance().config.getLong("PayLoad.Time.Hour"))
        var ticksDay = Ticks.dayToTicks(Main.getInstance().config.getLong("PayLoad.Time.Day"))
        var ticks = ticksSec + ticksMin + ticksHour + ticksDay
        if(Main.getInstance().config.getBoolean("PayLoad.Use")) {
            object : BukkitRunnable() {
                override fun run() {
                    payLoad()
                }
            }.runTaskTimer(Main.getInstance(), 0, ticks)
        }
        var ticksSecInt = Ticks.secToTicks(Main.getInstance().config.getLong("Interest.Time.Sec"))
        var ticksMinInt = Ticks.minToTicks(Main.getInstance().config.getLong("Interest.Time.Min"))
        var ticksHourInt = Ticks.hourToTicks(Main.getInstance().config.getLong("Interest.Time.Hour"))
        var ticksDayInt = Ticks.dayToTicks(Main.getInstance().config.getLong("Interest.Time.Day"))
        var ticksInt = ticksSecInt + ticksMinInt + ticksHourInt + ticksDayInt
        if(Main.getInstance().config.getBoolean("Interest.Use")) {
            object : BukkitRunnable() {
                override fun run() {
                    interest()
                }
            }.runTaskTimer(Main.getInstance(), 0, ticksInt)
        }
    }


    /**
     * Get Economy PayLoad
     */
    open fun payLoad() {
        for (offlinePlayer in Bukkit.getOfflinePlayers()) {
            economy.depositPlayer(offlinePlayer, 100.toDouble())
        }
    }

    open fun interest() {
        for(offlinePlayer in Bukkit.getOfflinePlayers()) {
            economy.withdrawPlayer(offlinePlayer,0.125)
        }
    }

    /**
     * Get Economy PayLoad
     * @param offlinePlayer the OfflinePlayer
     */
    open fun payLoad(offlinePlayer: OfflinePlayer) {
        economy.depositPlayer(offlinePlayer, 100.toDouble())
    }

    open fun interest(offlinePlayer: OfflinePlayer) {
        economy.withdrawPlayer(offlinePlayer,0.125)
    }
}