package com.nobodysapps.greentastic.networking.model

import com.google.gson.annotations.SerializedName


data class Vehicle(
    @SerializedName("calories")
    val calories: Double,
    @SerializedName("calories_col")
    val caloriesCol: List<Int>,
    @SerializedName("calories_score")
    val caloriesScore: Double,
    @SerializedName("coordinates")
    val coordinates: List<List<Double>>,
    @SerializedName("duration")
    val duration: Double,
    @SerializedName("duration_col")
    val durationCol: List<Int>,
    @SerializedName("duration_score")
    val durationScore: Double,
    @SerializedName("emission")
    val emission: Double,
    @SerializedName("emission_col")
    val emissionCol: List<Int>,
    @SerializedName("emission_score")
    val emissionScore: Double,
    @SerializedName("price")
    val price: Double,
    @SerializedName("price_col")
    val priceCol: List<Int>,
    @SerializedName("price_score")
    val priceScore: Double,
    @SerializedName("total_weighted_score")
    val totalWeightedScore: Double,
    @SerializedName("total_weighted_score_col")
    val totalWeightedScoreCol: List<Int>,
    @SerializedName("toxicity")
    val toxicity: Double,
    @SerializedName("toxicity_col")
    val toxicityCol: List<Int>,
    @SerializedName("toxicity_score")
    val toxicityScore: Double
)

data class VehicleAggregate(
    @SerializedName("bicycling")
    val bicycling: Vehicle,
    @SerializedName("driving")
    val driving: Vehicle,
    @SerializedName("ebike")
    val eBike: Vehicle,
    @SerializedName("escooter")
    val eScooter: Vehicle,
    @SerializedName("transit")
    val transit: Vehicle,
    @SerializedName("walking")
    val walking: Vehicle
)