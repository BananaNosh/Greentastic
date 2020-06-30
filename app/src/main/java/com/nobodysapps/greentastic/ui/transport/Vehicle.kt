package com.nobodysapps.greentastic.ui.transport

import com.nobodysapps.greentastic.R
import com.nobodysapps.greentastic.networking.model.ApiVehicle
import com.nobodysapps.greentastic.networking.model.VehicleAggregate
import com.nobodysapps.greentastic.utils.toRGB
import com.nobodysapps.greentastic.utils.TimeFormatter
import com.nobodysapps.greentastic.utils.TimeFormatter.Companion.DAYS_KEY
import com.nobodysapps.greentastic.utils.TimeFormatter.Companion.HOURS_KEY
import com.nobodysapps.greentastic.utils.TimeFormatter.Companion.MINUTES_KEY
import java.math.RoundingMode
import java.text.DecimalFormat

data class Vehicle(
    val type: VehicleType,
    val total: VehicleValue,
    val price: VehicleValue,
    val calories: VehicleValue,
    val emission: VehicleValue,
    val toxicity: VehicleValue,
    val duration: VehicleValue
)


fun vehiclesFromAggregate(vehicleAggregate: VehicleAggregate?): List<Vehicle>? {
    vehicleAggregate?.let { _ ->
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
    return null
}


fun vehicleFromApi(type: VehicleType, apiVehicle: ApiVehicle) = Vehicle(
    type,
    VehicleValue(
        0f,
        apiVehicle.totalWeightedScore,
        apiVehicle.totalWeightedScoreCol.toRGB(),
        ScoreUnit.EMPTY
    ),
    VehicleValue(
        apiVehicle.price,
        apiVehicle.priceScore,
        apiVehicle.priceCol.toRGB(),
        ScoreUnit.Dollar
    ),
    VehicleValue(
        apiVehicle.calories,
        apiVehicle.caloriesScore,
        apiVehicle.caloriesCol.toRGB(),
        ScoreUnit.KiloCalories
    ),
    VehicleValue(
        apiVehicle.emission,
        apiVehicle.emissionScore,
        apiVehicle.emissionCol.toRGB(),
        ScoreUnit.Kilogramm
    ),
    VehicleValue(
        apiVehicle.toxicity,
        apiVehicle.toxicityScore,
        apiVehicle.toxicityCol.toRGB(),
        ScoreUnit.Gramm
    ),
    VehicleValue(
        apiVehicle.duration,
        apiVehicle.durationScore,
        apiVehicle.durationCol.toRGB(),
        ScoreUnit.Minutes
    )
)


data class VehicleValue(
    val absoluteValue: Float,
    val score: Float,
    val color: Int,
    val unit: ScoreUnit
)

enum class VehicleType(val resourceValue: Int) {
    BIKE(R.drawable.ic_bike),
    CAR(R.drawable.ic_car),
    E_BIKE(R.drawable.ic_ebike),
    E_SCOOTER(R.drawable.ic_scooter),
    PUBLIC_TRANSPORT(R.drawable.ic_train),
    WALKING(R.drawable.ic_walk)
}

enum class ScoreUnit(
    val precision: Int,
    val symbol: String = "",
    val formatter: ((Float) -> String)? = null
) {
    EMPTY(0),
    Dollar(2, "$"),
    KiloCalories(0, "kcal"),
    Kilogramm(2, "kg"),
    Gramm(2, "g"),
    Minutes(0, formatter = { seconds ->
        TimeFormatter(
            "${DAYS_KEY}${HOURS_KEY}${MINUTES_KEY}",  // TODO should use format from strings
            formats = mutableMapOf(
                DAYS_KEY to "?%dd ",
                HOURS_KEY to "?%dh ",
                MINUTES_KEY to "%d'"
            )
        ).millisToTimeString(seconds.toLong() * 1000)
    });

    fun evaluate(score: Float): String {
        return formatter?.let {
            it(score)
        } ?: defaultFormatter(score)
    }

    private fun defaultFormatter(score: Float): String {
        val precisionPattern = if (precision > 0) "#." + (1..precision).map { "#" }.reduce { acc, it -> acc + it } else "#"
        val df = DecimalFormat(precisionPattern)
        df.roundingMode = RoundingMode.HALF_UP
        return "${df.format(score)}$symbol"
    }
}
