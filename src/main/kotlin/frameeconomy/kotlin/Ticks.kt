package frameeconomy.kotlin

/**
 * This Plugin was Created by FrameDev
 * Package :
 * ClassName frameeconomy.kotlin.Ticks
 * Date: 22.03.21
 * Project: FrameEconomy
 * Copyrighted by FrameDev
 */
enum class Ticks(val i : Long) {

    SECOND(20),
    MINUTE(1200),
    HOUR(72000),
    DAY(1728000),
    WEEK(12096000);

    companion object {
        fun secToTicks(sec : Long): Long {
            if(sec == 0L) return 0L
            return sec * SECOND.i
        }

        fun minToTicks(min : Long): Long {
            if(min == 0L) return 0L
            return min * MINUTE.i
        }

        fun hourToTicks(hour : Long): Long {
            if(hour == 0L) return 0L
            return hour * HOUR.i
        }

        fun dayToTicks(day : Long): Long {
            if(day == 0L) return 0L
            return day * DAY.i
        }

        fun weekToTicks(week : Long): Long {
            if(week == 0L) return 0L
            return week * WEEK.i
        }
    }
}