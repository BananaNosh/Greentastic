package com.nobodysapps.greentastic

import com.nobodysapps.greentastic.utils.TimeFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TimeUtilsTest {
    @Test
    fun testMillisToTimeString() {
        val timeFormatter = TimeFormatter()
        assertEquals("01:00:00", timeFormatter.millisToTimeString(3600000))
        assertEquals("00:22:30", timeFormatter.millisToTimeString(1350000))
        assertEquals("00:00:10", timeFormatter.millisToTimeString(10000))
        assertEquals("01:00:10", timeFormatter.millisToTimeString(3610000))

        val timeFormatter2 = TimeFormatter("m'mins and s'secs")
        assertEquals("60mins and 00secs", timeFormatter2.millisToTimeString(3600000))
        assertEquals("22mins and 30secs", timeFormatter2.millisToTimeString(1350000))
        assertEquals("00mins and 10secs", timeFormatter2.millisToTimeString(10000))
        assertEquals("60mins and 10secs", timeFormatter2.millisToTimeString(3610000))

        timeFormatter2.formats[TimeFormatter.MINUTES_KEY] = "%d"
        assertEquals("60mins and 00secs", timeFormatter2.millisToTimeString(3600000))
        assertEquals("22mins and 30secs", timeFormatter2.millisToTimeString(1350000))
        assertEquals("0mins and 10secs", timeFormatter2.millisToTimeString(10000))
        assertEquals("6mins and 10secs", timeFormatter2.millisToTimeString(370000))

        val timeFormatter3 =
            TimeFormatter(
                "${TimeFormatter.DAYS_KEY}${TimeFormatter.HOURS_KEY}${TimeFormatter.MINUTES_KEY}",
                formats = mutableMapOf(
                    TimeFormatter.DAYS_KEY to "?%dd ",
                    TimeFormatter.HOURS_KEY to "?%dh ",
                    TimeFormatter.MINUTES_KEY to "%d'"
                )
            )
        assertEquals("2'", timeFormatter3.millisToTimeString(120000))
        assertEquals("1h 2'", timeFormatter3.millisToTimeString(3720000))

    }
}
