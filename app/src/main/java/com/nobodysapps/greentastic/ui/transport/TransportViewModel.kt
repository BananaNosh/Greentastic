package com.nobodysapps.greentastic.ui.transport

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.nobodysapps.greentastic.repository.TransportRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class TransportViewModel @Inject constructor(
    private val transportRepository: TransportRepository,
    application: Application
) : AndroidViewModel(application) {

    init {
        Log.d(TAG, application.toString())  //TODO remove
    }

    val vehicles: LiveData<List<Vehicle>> = transportRepository.vehicles
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    fun load(source: String, dest: String, scope: CoroutineScope=viewModelScope) = scope.launch(Dispatchers.Main) {
        isLoading.value = true
        transportRepository.loadVehicles(source, dest)
        isLoading.value = false
    }

    companion object {
        const val TAG = "TransportViewModel"
    }

}