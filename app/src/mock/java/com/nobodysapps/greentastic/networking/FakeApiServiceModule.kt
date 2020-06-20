package com.nobodysapps.greentastic.networking

import com.nobodysapps.greentastic.networking.model.ApiVehicle
import com.nobodysapps.greentastic.networking.model.VehicleAggregate
import dagger.Module
import dagger.Provides
import io.reactivex.Single


@Module
object FakeApiServiceModule {

    @JvmStatic
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
                    Thread.sleep(DELAY)
                    it.onSuccess(suggestions)
                }
            }

            override suspend fun getVehicles(
                source: String,
                destination: String,
                carType: String?,
                weights: List<String>?
            ): VehicleAggregate =
                VehicleAggregate(
                    ApiVehicle(
                        12749.0f,
                        listOf(173, 255, 47),
                        0.86f,
                        emptyList(),
                        76499.0f,
                        listOf(264, 184, 60),
                        0.43f,
                        1.94f,
                        listOf(50, 205, 50),
                        0.75f,
                        38.87f,
                        listOf(255, 120, 71),
                        0.35f,
                        0.8518518518518521f,
                        listOf((0..255).random(), (0..255).random(), (0..255).random()),
                        0.0f,
                        listOf(50, 205, 50),
                        1.0f
                    ),
                    fakeVehicle,
                    fakeVehicle,
                    fakeVehicle,
                    fakeVehicle,
                    fakeVehicle
                )

//            override fun getVehicles(
//                source: String,
//                destination: String,
//                carType: String?,
//                weights: List<String>?
//            ): Single<VehicleAggregate> {
//                return Single.create { emitter ->
//                    emitter.onSuccess(
//                        VehicleAggregate(
//                            ApiVehicle(
//                                12749.0f,
//                                listOf(173, 255, 47),
//                                0.86f,
//                                emptyList(),
//                                76499.0f,
//                                listOf(264, 184, 60),
//                                0.43f,
//                                1.94f,
//                                listOf(50, 205, 50),
//                                0.75f,
//                                38.87f,
//                                listOf(255, 120, 71),
//                                0.35f,
//                                0.8518518518518521f,
//                                listOf(173, 255, 47),
//                                0.0f,
//                                listOf(50, 205, 50),
//                                1.0f
//                            ),
//                            fakeVehicle,
//                            fakeVehicle,
//                            fakeVehicle,
//                            fakeVehicle,
//                            fakeVehicle
//                        )
//                    )
//                }
//            }

        }
    }
}

private val fakeVehicle = ApiVehicle(
    10.0f, listOf(255, 255, 255), 0.86f,
    emptyList(),
    76499.0f, listOf(100, 184, 60),
    0.2f,
    1.94f, listOf(50, 205, 50),
    0.75f, 38.87f, listOf(255, 120, 71), 0.35f,
    0.8518518518518521f, listOf(173, 255, 47), 0.0f, listOf(50, 205, 50), 1.0f
)

private const val DELAY: Long = 1000