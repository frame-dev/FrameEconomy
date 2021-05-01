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

    open fun banks(): List<String> {
        return Main.getInstance().vaultManager.economy.banks;
    }

    open fun accounts(): List<OfflinePlayer> {
        val accounts: ArrayList<OfflinePlayer> = ArrayList<OfflinePlayer>()
        for (offlinePlayer in Bukkit.getOfflinePlayers()) {
            if (offlinePlayer != null)
                if (offlinePlayer.name != null)
                    if (Main.getInstance().vaultManager.economy.hasAccount(offlinePlayer)) {
                        accounts.add(offlinePlayer)
                    }
        }
        return accounts
    }

    private fun runnable() {
        val sec = Ticks.secToTicks(plugin.config.getLong("PayLoad.Sec"))
        val min = Ticks.minToTicks(plugin.config.getLong("PayLoad.Min"))
        val hour = Ticks.hourToTicks(plugin.config.getLong("PayLoad.Hour"))
        val day = Ticks.dayToTicks(plugin.config.getLong("PayLoad.Day"))
        val ticks = sec + min + hour + day
        if (plugin.config.getBoolean("PayLoad.Use"))
            object : BukkitRunnable() {
                override fun run() {
                    payLoad()
                }
            }.runTaskTimer(plugin, 0, ticks)
        val secInt = Ticks.secToTicks(plugin.config.getLong("Interest.Sec"))
        val minInt = Ticks.minToTicks(plugin.config.getLong("Interest.Min"))
        val hourInt = Ticks.hourToTicks(plugin.config.getLong("Interest.Hour"))
        val dayInt = Ticks.dayToTicks(plugin.config.getLong("Interest.Day"))
        val ticksInt = secInt + minInt + hourInt + dayInt
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
        val percent = plugin.config.getDouble("Interest.Percent")
        val amount = plugin.config.getDouble("Interest.Amount")
        val sum: Double = (amount * percent).roundToLong().toDouble()
        for (offlinePlayer in Bukkit.getOfflinePlayers()) {
            plugin.vaultManager.economy.withdrawPlayer(offlinePlayer, sum)
        }
    }

    open fun interest(offlinePlayer: OfflinePlayer) {
        val percent = plugin.config.getDouble("Interest.Percent")
        val amount = plugin.config.getDouble("Interest.Amount")
        val sum: Double = (amount * percent).roundToLong().toDouble()
        plugin.vaultManager.economy.withdrawPlayer(offlinePlayer, sum)
    }
}