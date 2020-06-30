package com.nobodysapps.greentastic

import com.nobodysapps.greentastic.ui.transport.ScoreUnit
import org.junit.Assert.assertEquals
import org.junit.Test


class VehicleTest {
    @Test
    fun testScoreUnit() {
        assertEquals("3,23kg", ScoreUnit.Kilogramm.evaluate(3.232f))
        assertEquals("32,32kg", ScoreUnit.Kilogramm.evaluate(32.32f))
        assertEquals("323,2kg", ScoreUnit.Kilogramm.evaluate(323.2f))
        assertEquals("3231kg", ScoreUnit.Kilogramm.evaluate(3231f))

        assertEquals("3231g", ScoreUnit.Gramm.evaluate(3231f))

        assertEquals("54'", ScoreUnit.Minutes.evaluate(3231f))
        assertEquals("1h 1'", ScoreUnit.Minutes.evaluate(3661f))
        assertEquals("28d 22h 29'", ScoreUnit.Minutes.evaluate(2500120f))
    }
}