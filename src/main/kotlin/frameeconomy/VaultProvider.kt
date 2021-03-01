package frameeconomy

import de.framedev.frameeconomy.main.Main
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

/**
 * This Plugin was Created by FrameDev
 * Package :
 * Date: 28.02.21
 * Project: FrameEconomy
 * Copyrighted by FrameDev
 */
class VaultProvider {

    var economy: Economy = Main.getInstance().vaultManager.eco

    fun runnable() {
        var ticksSec = Ticks.secToTicks(Main.getInstance().config.getLong("PayLoad.Time.Sec"))
        var ticksMin = Ticks.minToTicks(Main.getInstance().config.getLong("PayLoad.Time.Min"))
        var ticksHour = Ticks.hourToTicks(Main.getInstance().config.getLong("PayLoad.Time.Hour"))
        var ticksDay = Ticks.dayToTicks(Main.getInstance().config.getLong("PayLoad.Time.Day"))
        var ticks = ticksSec + ticksMin + ticksHour + ticksDay
        if(Main.getInstance().config.getBoolean("PayLoad.Use")) {
            object : BukkitRunnable() {
                override fun run() {
                    payLoad()
                    println("PayLoad")
                }
            }.runTaskTimer(Main.getInstance(), 0, ticks)
        }
        var ticksSecInt = Ticks.secToTicks(Main.getInstance().config.getLong("Interest.Time.Sec"))
        var ticksMinInt = Ticks.secToTicks(Main.getInstance().config.getLong("Interest.Time.Min"))
        var ticksHourInt = Ticks.secToTicks(Main.getInstance().config.getLong("Interest.Time.Hour"))
        var ticksDayInt = Ticks.secToTicks(Main.getInstance().config.getLong("Interest.Time.Day"))
        var ticksInt = ticksSecInt + ticksMinInt + ticksHourInt + ticksDayInt
        if(Main.getInstance().config.getBoolean("Interest.Use")) {
            object : BukkitRunnable() {
                override fun run() {
                    interest()
                    println("Interest")
                }
            }.runTaskTimer(Main.getInstance(), 0, ticksInt)
        }
    }


    /**
     * Get Economy PayLoad
     */
    fun payLoad() {
        for (offlinePlayer in Bukkit.getOfflinePlayers()) {
            economy.depositPlayer(offlinePlayer, 100.toDouble())
        }
    }

    fun interest() {
        for(offlinePlayer in Bukkit.getOfflinePlayers()) {
            economy.withdrawPlayer(offlinePlayer,0.125)
        }
    }
}