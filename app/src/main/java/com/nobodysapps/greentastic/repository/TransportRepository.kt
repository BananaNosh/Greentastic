package com.nobodysapps.greentastic.repository

import androidx.lifecycle.MutableLiveData
import com.nobodysapps.greentastic.networking.ApiService
import com.nobodysapps.greentastic.storage.Connection
import com.nobodysapps.greentastic.storage.ConnectionDao
import com.nobodysapps.greentastic.ui.transport.vehiclesFromAggregate
import java.util.*
import javax.inject.Inject


class TransportRepository @Inject constructor(private val apiService: ApiService, private val connectionDao: ConnectionDao) {


    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
//    var vehicles: MutableLiveData<List<Vehicle>> = connectionDao.loadRequestedVehicles()

    suspend fun loadVehicles(source: String, dest: String) {
        if (source == dest) {
            // TODO warning
        }
        // TODO check cached
//        connectionDao.resetRequested()
        val downloadedVehicles =
            vehiclesFromAggregate(apiService.getVehicles(source, dest)) // TODO cartype, weights
        connectionDao.insert(Connection(source, dest, downloadedVehicles, Calendar.getInstance().timeInMillis, true))
    }

    suspend fun loadVehicles() = connectionDao.loadRequestedVehicles()

    companion object {
        const val TAG = "TransportApiRepository"
    }
}