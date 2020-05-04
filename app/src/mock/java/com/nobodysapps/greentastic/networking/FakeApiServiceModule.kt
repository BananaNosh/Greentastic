package com.nobodysapps.greentastic.networking

import com.nobodysapps.greentastic.networking.model.Vehicle
import com.nobodysapps.greentastic.networking.model.VehicleAggregate
import dagger.Module
import dagger.Provides
import io.reactivex.Single


@Module
class FakeApiServiceModule {

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
                return Single.create { emitter ->
                    emitter.onSuccess(
                        VehicleAggregate(
                            Vehicle(
                                12749.0,
                                listOf(173, 255, 47),
                                0.86,
                                emptyList(),
                                76499.0,
                                listOf(264, 184, 60),
                                0.43,
                                1.94,
                                listOf(50, 205, 50),
                                0.75,
                                38.87,
                                listOf(255, 120, 71),
                                0.35,
                                0.8518518518518521,
                                listOf(173, 255, 47),
                                0.0,
                                listOf(50, 205, 50),
                                1.0
                            ),
                            fakeVehicle,
                            fakeVehicle,
                            fakeVehicle,
                            fakeVehicle,
                            fakeVehicle
                        )
                    )
                }
            }

        }
    }
}

private val fakeVehicle = Vehicle(
    10.0, listOf(255, 255, 255), 0.86,
    emptyList(),
    76499.0, listOf(100, 184, 60),
    0.2,
    1.94, listOf(50, 205, 50),
    0.75, 38.87, listOf(255, 120, 71), 0.35,
    0.8518518518518521, listOf(173, 255, 47), 0.0, listOf(50, 205, 50), 1.0
)