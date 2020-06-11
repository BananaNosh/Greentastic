package com.nobodysapps.greentastic.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.nobodysapps.greentastic.networking.ApiService
import com.nobodysapps.greentastic.networking.model.VehicleAggregate
import com.nobodysapps.greentastic.storage.ConnectionDao
import com.nobodysapps.greentastic.ui.transport.Vehicle
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class TransportRepository @Inject constructor(private val apiService: ApiService, private val connectionDao: ConnectionDao) {


    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var vehicles: MutableLiveData<List<Vehicle>> = MutableLiveData()

    fun loadVehicles(source: String, dest: String) {
        if (source == dest) {
            // TODO warning
        }
        val vehiclesSingle = apiService.getVehicles(source, dest) // TODO cartype, weights
        vehiclesSingle
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                isLoading.value = true
            }
            .doFinally {
                isLoading.value = false
            }
            .subscribe(object : SingleObserver<VehicleAggregate> {
                override fun onSuccess(vehicles: VehicleAggregate) {
                    Log.d(TAG, "vehicles: $vehicles")

                }

                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "onSubscribe called")
//                    compositeDisposable.add(d)
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "Some error $e")
                    // TODO
                }

            })
    }

    companion object {
        const val TAG = "TransportApiRepository"
    }
}