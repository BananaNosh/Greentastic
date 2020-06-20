package com.nobodysapps.greentastic.repository

import android.util.Log
import com.nobodysapps.greentastic.networking.ApiService
import com.nobodysapps.greentastic.storage.Connection
import com.nobodysapps.greentastic.storage.ConnectionDao
import com.nobodysapps.greentastic.ui.transport.vehiclesFromAggregate
import java.util.*
import javax.inject.Inject


class TransportRepository @Inject constructor(private val apiService: ApiService, private val connectionDao: ConnectionDao) {


//    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var vehicles = connectionDao.loadRequestedVehicles()

    suspend fun loadVehicles(source: String, dest: String) {
        Log.d(TAG, "Start loading vehicles")
//        if (source == dest) {
//            // TODO warning
//        }
        // TODO check cached
        connectionDao.resetRequested()
        val updated = connectionDao.setRequestedIfStored(source, dest) > 0
        if (!updated) {
            val downloadedVehicles =
                vehiclesFromAggregate(apiService.getVehicles(source, dest)) // TODO cartype, weights
            downloadedVehicles?.let {
                connectionDao.insert(
                    Connection(
                        source,
                        dest,
                        downloadedVehicles,
                        Calendar.getInstance().timeInMillis,
                        true
                    )
                )
            } // TODO null response from api (frankfurt - tunisia)
        }
    }

    companion object {
        const val TAG = "TransportApiRepository"
    }
}