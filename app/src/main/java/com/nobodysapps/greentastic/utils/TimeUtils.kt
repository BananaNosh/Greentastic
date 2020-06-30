package com.nobodysapps.greentastic.utils

import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


class TimeFormatter(
    val totalString: String = "$HOURS_KEY:$MINUTES_KEY:$SECONDS_KEY",
    val formats: MutableMap<String, String> = mutableMapOf(
        SECONDS_KEY to "%02d",
        MINUTES_KEY to "%02d",
        HOURS_KEY to "%02d"
    )
) {
    fun millisToTimeString(millis: Long): String {
        var remaining = millis
        val stringValues = HashMap<String, String>()
        val sortedFormats = formats.toSortedMap(Comparator { o1, o2 ->
            -(timeUnits[o1]?.compareTo(timeUnits[o2]) ?: 0)
        })
        sortedFormats.filter {
            it.key in totalString
        }.forEach {
            var valueOfUnit = fromMillis(timeUnits[it.key], remaining)
            val millisPerUnit = timeUnits[it.key]?.toMillis(1) ?: 0
            remaining -= millisPerUnit * valueOfUnit

            if (it.key == sortedFormats.lastKey() && remaining > 0 && it.key in timeUnits) {
                valueOfUnit += (remaining / timeUnits.getValue(it.key).toMillis(1).toFloat()).roundToInt()
            }

            var format = it.value
            val isOptional = format.startsWith("?")
            if (isOptional) format = format.substring(1)
            val isTrailingZero = remaining == millis
            stringValues[it.key] =
                if (!isTrailingZero || !isOptional) format.format(valueOfUnit) else ""
        }
        var string = totalString
        stringValues.forEach {
            string = string.replace(it.key, it.value)
        }
        return string
    }

    companion object {
        const val MILLIS_KEY = "s''"
        const val SECONDS_KEY = "s'"
        const val MINUTES_KEY = "m'"
        const val HOURS_KEY = "h'"
        const val DAYS_KEY = "d'"
        //const val monthsKey = "M'"
        //const val yearsKey = "y'"

        val timeUnits = mapOf(
            DAYS_KEY to TimeUnit.DAYS,
            HOURS_KEY to TimeUnit.HOURS,
            MINUTES_KEY to TimeUnit.MINUTES,
            SECONDS_KEY to TimeUnit.SECONDS,
            MILLIS_KEY to TimeUnit.MILLISECONDS
        )

        fun fromMillis(timeUnit: TimeUnit?, millis: Long): Long = when (timeUnit) {
            TimeUnit.DAYS -> TimeUnit.MILLISECONDS.toDays(millis)
            TimeUnit.HOURS -> TimeUnit.MILLISECONDS.toHours(millis)
            TimeUnit.MINUTES -> TimeUnit.MILLISECONDS.toMinutes(millis)
            TimeUnit.SECONDS -> TimeUnit.MILLISECONDS.toSeconds(millis)
            TimeUnit.MILLISECONDS -> millis
            else -> throw IllegalArgumentException("Argument not supported")
        }
    }

}
