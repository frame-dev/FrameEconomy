package frameeconomy.kotlin

import de.framedev.frameeconomy.main.Main
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
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
        // Run the Schedulers
        runnable()
    }

    /**
     * @return all Banks
     */
    open fun banks(): List<String> {
        return Main.getInstance().vaultManager.economy.banks;
    }

    /**
     * @return all Accounts from OfflinePlayers
     */
    open fun accounts(): List<OfflinePlayer> {
        val accounts: ArrayList<OfflinePlayer> = ArrayList()
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
        if (plugin.config.getBoolean("PayLoad.Use") && ticks > 0)
            object : BukkitRunnable() {
                override fun run() {
                    val players = Bukkit.getOfflinePlayers()
                    plugin.runAsync {
                        payLoad(players)
                    }
                }
            }.runTaskTimer(plugin, 0, ticks)
        val secInt = Ticks.secToTicks(plugin.config.getLong("Interest.Sec"))
        val minInt = Ticks.minToTicks(plugin.config.getLong("Interest.Min"))
        val hourInt = Ticks.hourToTicks(plugin.config.getLong("Interest.Hour"))
        val dayInt = Ticks.dayToTicks(plugin.config.getLong("Interest.Day"))
        val ticksInt = secInt + minInt + hourInt + dayInt
        if (plugin.config.getBoolean("Interest.Use") && ticksInt > 0)
            object : BukkitRunnable() {
                override fun run() {
                    val players = Bukkit.getOfflinePlayers()
                    plugin.runAsync {
                        interest(players)
                    }
                }
            }.runTaskTimer(plugin, 0, ticksInt)
    }

    open fun payLoad() {
        payLoad(Bukkit.getOfflinePlayers())
    }

    private fun payLoad(players: Array<OfflinePlayer>) {
        for (offlinePlayer in players) {
            plugin.vaultManager.economy.depositPlayer(offlinePlayer, plugin.config.getDouble("PayLoad.Amount"))
        }
    }

    open fun payLoad(offlinePlayer: OfflinePlayer) {
        plugin.vaultManager.economy.depositPlayer(offlinePlayer, plugin.config.getDouble("PayLoad.Amount"))
    }

    open fun interest() {
        interest(Bukkit.getOfflinePlayers())
    }

    private fun interest(players: Array<OfflinePlayer>) {
        val percent = plugin.config.getDouble("Interest.Percent")
        val amount = plugin.config.getDouble("Interest.Amount")
        val sum: Double = (amount * percent).toDouble()
        for (offlinePlayer in players) {
            plugin.vaultManager.economy.withdrawPlayer(offlinePlayer, sum)
        }
    }

    open fun interest(offlinePlayer: OfflinePlayer) {
        val percent = plugin.config.getDouble("Interest.Percent")
        val amount = plugin.config.getDouble("Interest.Amount")
        val sum: Double = (amount * percent)
        plugin.vaultManager.economy.withdrawPlayer(offlinePlayer, sum)
    }
}
