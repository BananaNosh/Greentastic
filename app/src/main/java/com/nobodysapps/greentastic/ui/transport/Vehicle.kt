package com.nobodysapps.greentastic.ui.transport

import androidx.annotation.ColorInt
import com.nobodysapps.greentastic.R
import com.nobodysapps.greentastic.networking.model.ApiVehicle
import com.nobodysapps.greentastic.networking.model.VehicleAggregate
import com.nobodysapps.greentastic.utils.toRGB

data class Vehicle(
    val type: VehicleType,
    val total: VehicleValue,
    val price: VehicleValue,
    val calories: VehicleValue,
    val emission: VehicleValue,
    val toxicity: VehicleValue,
    val duration: VehicleValue
)


fun vehiclesFromAggregate(vehicleAggregate: VehicleAggregate): List<Vehicle> {
    val types = listOf(
        VehicleType.BIKE,
        VehicleType.CAR,
        VehicleType.E_BIKE,
        VehicleType.E_SCOOTER,
        VehicleType.PUBLIC_TRANSPORT,
        VehicleType.WALKING
    )
    val (bike, car, eBike, eScooter, publicTransport, walking) = vehicleAggregate
    val vehicles = listOf(bike, car, eBike, eScooter, publicTransport, walking)
    return types.zip(vehicles).map {
        val (type, apiVehicle) = it
        vehicleFromApi(type, apiVehicle)
    }
}


fun vehicleFromApi(type: VehicleType, apiVehicle: ApiVehicle) = Vehicle(
    type,
    VehicleValue(0f, apiVehicle.totalWeightedScore, apiVehicle.totalWeightedScoreCol.toRGB()),
    VehicleValue(apiVehicle.price, apiVehicle.priceScore, apiVehicle.priceCol.toRGB()),
    VehicleValue(apiVehicle.calories, apiVehicle.caloriesScore, apiVehicle.caloriesCol.toRGB()),
    VehicleValue(apiVehicle.emission, apiVehicle.emissionScore, apiVehicle.emissionCol.toRGB()),
    VehicleValue(apiVehicle.toxicity, apiVehicle.toxicityScore, apiVehicle.toxicityCol.toRGB()),
    VehicleValue(apiVehicle.duration, apiVehicle.durationScore, apiVehicle.durationCol.toRGB())
)


data class VehicleValue(val absoluteValue: Float, val score: Float, val color: Int)

enum class VehicleType(val resourceValue: Int) {
    BIKE(R.drawable.ic_bike),
    CAR(R.drawable.ic_car),
    E_BIKE(R.drawable.ic_ebike),
    E_SCOOTER(R.drawable.ic_scooter),
    PUBLIC_TRANSPORT(R.drawable.ic_train),
    WALKING(R.drawable.ic_walk)
}