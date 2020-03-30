package com.nobodysapps.greentastic.networking

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("query_autocomplete")
    fun getAutoComplete(@Query("search_string") searchString: String,
                        @Query("user_location") userLocation: String?=null): Single<List<String>>

    @GET("query_directions")
    fun getDirections(@Query("source") source: String,
                      @Query("destination") destination: String,
                      @Query("car_type") carType: String?=null,
                          @Query("car_type") weights: List<String>?=null)
}

//"/query_directions?source=\(source_)&destination=\(destination)&car_type=\( car_type)&weights=\(time),\(emis),\(cost),\(cals),\(toxc)
// weights: 0.03,0.1,0,0,0
//https://clean-commuter.appspot.com/query_autocomplete?user_location=\(ownCoords)&search_string=\(frankfur
// t)