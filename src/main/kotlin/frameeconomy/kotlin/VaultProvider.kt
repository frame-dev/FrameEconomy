package frameeconomy.kotlin

import de.framedev.frameeconomy.main.Main
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

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
    }

    open fun payLoad() {
        for (offlinePlayer in Bukkit.getOfflinePlayers()) {
            plugin.vaultManager.economy.depositPlayer(offlinePlayer, plugin.config.getDouble("PayLoad.Amount"))
        }
    }
}