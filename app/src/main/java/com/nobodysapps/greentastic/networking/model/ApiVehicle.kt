package com.nobodysapps.greentastic.networking.model

import com.google.gson.annotations.SerializedName


data class ApiVehicle(
    @SerializedName("calories")
    val calories: Float,
    @SerializedName("calories_col")
    val caloriesCol: List<Int>,
    @SerializedName("calories_score")
    val caloriesScore: Float,
    @SerializedName("coordinates")
    val coordinates: List<List<Float>>,
    @SerializedName("duration")
    val duration: Float,
    @SerializedName("duration_col")
    val durationCol: List<Int>,
    @SerializedName("duration_score")
    val durationScore: Float,
    @SerializedName("emission")
    val emission: Float,
    @SerializedName("emission_col")
    val emissionCol: List<Int>,
    @SerializedName("emission_score")
    val emissionScore: Float,
    @SerializedName("price")
    val price: Float,
    @SerializedName("price_col")
    val priceCol: List<Int>,
    @SerializedName("price_score")
    val priceScore: Float,
    @SerializedName("total_weighted_score")
    val totalWeightedScore: Float,
    @SerializedName("total_weighted_score_col")
    val totalWeightedScoreCol: List<Int>,
    @SerializedName("toxicity")
    val toxicity: Float,
    @SerializedName("toxicity_col")
    val toxicityCol: List<Int>,
    @SerializedName("toxicity_score")
    val toxicityScore: Float
)

data class VehicleAggregate(
    @SerializedName("bicycling")
    val bike: ApiVehicle,
    @SerializedName("driving")
    val car: ApiVehicle,
    @SerializedName("ebike")
    val eBike: ApiVehicle,
    @SerializedName("escooter")
    val eScooter: ApiVehicle,
    @SerializedName("transit")
    val publicTransport: ApiVehicle,
    @SerializedName("walking")
    val walking: ApiVehicle
)