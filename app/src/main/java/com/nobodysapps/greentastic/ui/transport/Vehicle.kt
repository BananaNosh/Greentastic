package com.nobodysapps.greentastic.ui.transport

import android.graphics.Color
import android.graphics.ColorSpace
import com.nobodysapps.greentastic.R
import com.nobodysapps.greentastic.networking.model.ApiVehicle

data class Vehicle(
    val type: VehicleType,
    val total: VehicleValue,
    val price: VehicleValue,
    val calories: VehicleValue,
    val emission: VehicleValue,
    val toxicity: VehicleValue,
    val duration: VehicleValue
)


//fun vehicleFromApi(apiVehicle: ApiVehicle) = Vehicle(
//    VehicleValue(0, apiVehicle.totalWeightedScore, Color.valueOf(apiVehicle.totalWeightedScoreCol.toFloatArray(), ColorSpace.Rgb).toArgb())
//)


fun List<Int>.toFloatArray(): FloatArray {
    return map {
        it.toFloat()
    }.toFloatArray()
}


data class VehicleValue(val absoluteValue: Float, val score: Float, val color: Int)

enum class VehicleType(val resourceValue: Int) {
    BIKE(R.drawable.ic_bike) // TODO add rest

}