package frameeconomy

/**
 * This Plugin was Created by FrameDev
 * Package : frameeconomy
 * Date: 28.02.21
 * Project: FrameEconomy
 * Copyrighted by FrameDev
 */
enum class Ticks(val i: Long) {

    SEC(20*1),
    MIN(20*60),
    HOUR(20*60*60),
    DAY(20*60*60*24);

    companion object {
        fun secToTicks(sec: Long): Long {
            return sec * 20
        }

        fun minToTicks(min: Long): Long {
            return min * 60 * 20
        }

        fun hourToTicks(hour: Long): Long {
            return hour * 60 * 60 * 20
        }

        fun dayToTicks(day: Long): Long {
            return day * 24 * 60 * 60 * 20
        }
    }
}