package com.nobodysapps.greentastic.networking

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("query_autocomplete")
    fun getAutoComplete(@Query("search_string") searchString: String,
                        @Query("user_location") userLocation: String?=null): Single<List<String>>
}

//https://clean-commuter.appspot.com/query_autocomplete?user_location=\(ownCoords)&search_string=\(frankfurt)