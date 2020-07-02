package com.nobodysapps.greentastic

import com.nobodysapps.greentastic.utils.ResultObject
import com.nobodysapps.greentastic.utils.ResultState
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.AssertionError

class ResultObjectTest {
    @Test
    fun testTypes() {
        var result: ResultObject<Int> = ResultObject()
        assertEquals(ResultState.LOADING, result.state)
        assertEquals(null, result.value)
        result = ResultObject(10)
        assertEquals(10, result.value)
        assertEquals(ResultState.LOADED, result.state)
        val error = AssertionError("Failed assertion")
        result = ResultObject(error)
        assertEquals(ResultState.ERROR, result.state)
        assertEquals(error, result.error)
    }
}