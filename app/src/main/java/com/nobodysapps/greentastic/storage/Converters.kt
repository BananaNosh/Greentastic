package com.nobodysapps.greentastic.storage

import androidx.room.ColumnInfo
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nobodysapps.greentastic.ui.transport.Vehicle

class Converters {
    @TypeConverter
    fun vehiclesToString(vehicles: List<Vehicle>) = Gson().toJson(vehicles)

    @TypeConverter
    fun vehiclesFromString(string: String): List<Vehicle> {
        val type = object : TypeToken<List<Vehicle>>() {}.type
        return Gson().fromJson(string, type)
    }

    data class VehiclesWrapper(@ColumnInfo(name="vehicles") val vehicles: List<Vehicle>)
}