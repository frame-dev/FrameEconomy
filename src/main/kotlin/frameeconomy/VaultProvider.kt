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
        var ticksSec = ticksToSec(Main.getInstance().config.getLong("PayLoad.Time.Sec"))
        var ticksMin = ticksToMin(Main.getInstance().config.getLong("PayLoad.Time.Min"))
        var ticksHour = ticksToHour(Main.getInstance().config.getLong("PayLoad.Time.Hour"))
        var ticksDay = ticksToDay(Main.getInstance().config.getLong("PayLoad.Time.Day"))
        var ticks = ticksSec + ticksMin + ticksHour + ticksDay
        object : BukkitRunnable() {
            override fun run() {
                payLoad()
                println("PayLoad")
            }
        }.runTaskTimer(Main.getInstance(), 0, ticks)
    }


    fun payLoad() {
        for (offlinePlayer in Bukkit.getOfflinePlayers()) {
            economy.depositPlayer(offlinePlayer, 100.toDouble())
        }
    }

    companion object {
        fun ticksToSec(sec: Long): Long {
            return sec * 20
        }

        fun ticksToMin(min: Long): Long {
            return min * 60 * 20
        }

        fun ticksToHour(hour: Long): Long {
            return hour * 60 * 60 * 20
        }

        fun ticksToDay(day: Long): Long {
            return day * 24 * 60 * 60 * 20
        }
    }
}