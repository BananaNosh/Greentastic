package com.nobodysapps.greentastic.ui.transport

import androidx.annotation.ColorInt
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


fun vehicleFromApi(type: VehicleType, apiVehicle: ApiVehicle) = Vehicle(
    type,
    VehicleValue(0f, apiVehicle.totalWeightedScore, apiVehicle.totalWeightedScoreCol.toARGB()),
    VehicleValue(apiVehicle.price, apiVehicle.priceScore, apiVehicle.priceCol.toARGB()),
    VehicleValue(apiVehicle.calories, apiVehicle.caloriesScore, apiVehicle.caloriesCol.toARGB()),
    VehicleValue(apiVehicle.emission, apiVehicle.emissionScore, apiVehicle.emissionCol.toARGB()),
    VehicleValue(apiVehicle.toxicity, apiVehicle.toxicityScore, apiVehicle.toxicityCol.toARGB()),
    VehicleValue(apiVehicle.duration, apiVehicle.durationScore, apiVehicle.durationCol.toARGB())
)


@ColorInt
fun List<Int>.toARGB(): Int {
    val aValue = when (this.size) {
        4 -> this[3]
        3 -> 1
        else -> throw IllegalArgumentException("Must be length 3 or 4")
    }
    return (aValue * 255.0f + 0.5f).toInt() shl 24 or
            ((this[0] * 255.0f + 0.5f).toInt() shl 16) or
            ((this[1] * 255.0f + 0.5f).toInt() shl 8) or
            (this[2] * 255.0f + 0.5f).toInt()
}





data class VehicleValue(val absoluteValue: Float, val score: Float, val color: Int)

enum class VehicleType(val resourceValue: Int) {
    BIKE(R.drawable.ic_bike),
    CAR(R.drawable.ic_car),
    E_BIKE(R.drawable.ic_ebike),
    E_SCOOTER(R.drawable.ic_scooter),
    PUBLIC_TRANSPORT(R.drawable.ic_train),
    WALKING(R.drawable.ic_walk)
}