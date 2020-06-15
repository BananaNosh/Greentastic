package com.nobodysapps.greentastic.ui.transport

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.nobodysapps.greentastic.repository.TransportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class TransportViewModel @Inject constructor(
    private val transportRepository: TransportRepository
) : ViewModel() {

    val vehicles: LiveData<List<Vehicle>> = liveData {
        emit(transportRepository.loadVehicles())
    }

    fun load(source: String, dest: String) = viewModelScope.launch(Dispatchers.IO) {
        transportRepository.loadVehicles(source, dest)
    }


}