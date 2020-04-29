package com.nobodysapps.greentastic.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class SearchViewModel @Inject constructor(val repository: SearchApiRepository): ViewModel() {

    var sourceString: LiveData<String> = MutableLiveData()
    var destString: LiveData<String> = MutableLiveData()

//    var sourceIsLoading: LiveData<Boolean> = repository

    var sourceCompletion = repository.sourceCompletion
    var destCompletion = repository.destCompletion

}
