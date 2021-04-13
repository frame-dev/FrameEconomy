package frameeconomy.kotlin

import de.framedev.frameeconomy.main.Main
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.roundToLong

/**
 * This Plugin was Created by FrameDev
 * Package :
 * ClassName frameeconomy.kotlin.VaultProvider
 * Date: 22.03.21
 * Project: FrameEconomy
 * Copyrighted by FrameDev
 */
open class VaultProvider(val plugin: Main) {

    init {
        runnable()
    }

    private fun runnable() {
        var sec = Ticks.secToTicks(plugin.config.getLong("PayLoad.Sec"))
        var min = Ticks.minToTicks(plugin.config.getLong("PayLoad.Min"))
        var hour = Ticks.hourToTicks(plugin.config.getLong("PayLoad.Hour"))
        var day = Ticks.dayToTicks(plugin.config.getLong("PayLoad.Day"))
        var ticks = sec + min + hour + day
        if (plugin.config.getBoolean("PayLoad.Use"))
            object : BukkitRunnable() {
                override fun run() {
                    payLoad()
                }
            }.runTaskTimer(plugin, 0, ticks)
        var secInt = Ticks.secToTicks(plugin.config.getLong("Interest.Sec"))
        var minInt = Ticks.minToTicks(plugin.config.getLong("Interest.Min"))
        var hourInt = Ticks.hourToTicks(plugin.config.getLong("Interest.Hour"))
        var dayInt = Ticks.dayToTicks(plugin.config.getLong("Interest.Day"))
        var ticksInt = secInt + minInt + hourInt + dayInt
        if (plugin.config.getBoolean("Interest.Use"))
            object : BukkitRunnable() {
                override fun run() {
                    interest()
                }
            }.runTaskTimer(plugin, 0, ticksInt)
    }

    open fun payLoad() {
        for (offlinePlayer in Bukkit.getOfflinePlayers()) {
            plugin.vaultManager.economy.depositPlayer(offlinePlayer, plugin.config.getDouble("PayLoad.Amount"))
        }
    }

    open fun payLoad(offlinePlayer: OfflinePlayer) {
        plugin.vaultManager.economy.depositPlayer(offlinePlayer, plugin.config.getDouble("PayLoad.Amount"))
    }

    open fun interest() {
        var percent = plugin.config.getDouble("Interest.Percent")
        var amount = plugin.config.getDouble("Interest.Amount")
        var sum: Double = (amount * percent).roundToLong().toDouble()
        for (offlinePlayer in Bukkit.getOfflinePlayers()) {
            plugin.vaultManager.economy.withdrawPlayer(offlinePlayer, sum)
        }
    }

    open fun interest(offlinePlayer: OfflinePlayer) {
        var percent = plugin.config.getDouble("Interest.Percent")
        var amount = plugin.config.getDouble("Interest.Amount")
        var sum: Double = (amount * percent).roundToLong().toDouble()
        plugin.vaultManager.economy.withdrawPlayer(offlinePlayer, sum)
    }
}