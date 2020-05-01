package com.nobodysapps.greentastic.networking

import dagger.Module
import dagger.Provides
import io.reactivex.Single
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock


@Module
class TestApiServiceModule {

    @Provides
    fun provideApiService(): ApiService {
        val apiService = mock(ApiService::class.java)
        `when`(apiService.getAutoComplete(anyString(), anyString())).thenReturn(Single.create {
            val suggestions = listOf(
                "Bla",
                " Zurich, KS, USA",
                " Zurich, ON, Canada",
                " Zurich, Netherlands, Zurich American Insurance Company, Schaumburg, IL, USA"
            )
            it.onSuccess(suggestions)
        })
//        `when`(apiService.getDirections(anyString(), anyString())).thenReturn(Single.create {
//            it.onError(AssertionError("Not Implemented"))
//        })
        return apiService
    }
}