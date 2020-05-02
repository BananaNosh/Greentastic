package com.nobodysapps.greentastic.networking

import com.nobodysapps.greentastic.networking.model.VehicleAggregate
import dagger.Module
import dagger.Provides
import io.reactivex.Single


@Module
class TestApiServiceModule {

    @Provides
    fun provideApiService(): ApiService {
        return object : ApiService {
            override fun getAutoComplete(
                searchString: String,
                userLocation: String?
            ): Single<List<String>> {
                return Single.create {
                    val suggestions: List<String> = when (searchString) {
                        "frankfurt" -> {
                            listOf(
                                "Frankfurt, Germany",
                                "Frankfurt-Flughafen, Frankfurt, Germany",
                                "Frankfurt (Oder), Germany",
                                "Frankfurt, KY, USA",
                                "Frankfurt, IL, USA"
                            )
                        }
                        else -> {
                            listOf(
                                "Zürich, Switzerland",
                                " Zurich, KS, USA",
                                " Zurich, ON, Canada",
                                " Zurich, Netherlands, Zurich American Insurance Company, Schaumburg, IL, USA"
                            )
                        }
                    }
                    it.onSuccess(suggestions)
                }
            }

            override fun getDirections(
                source: String,
                destination: String,
                carType: String?,
                weights: List<String>?
            ): Single<VehicleAggregate> {
                TODO("Not yet implemented")
            }

        }
    }
}