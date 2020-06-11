package com.nobodysapps.greentastic.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nobodysapps.greentastic.repository.SearchApiRepository
import javax.inject.Inject

class SearchViewModel @Inject constructor(repository: SearchApiRepository): ViewModel() {

//    var sourceString: LiveData<String> = MutableLiveData()
//    var destString: LiveData<String> = MutableLiveData()
//
////    var sourceIsLoading: LiveData<Boolean> = repository
//
//    var sourceCompletion = repository.sourceCompletion
//    var destCompletion = repository.destCompletion
    var sourceData: SearchViewData = SearchViewData(repository.sourceCompletion, repository.sourceIsLoading)
    var destData: SearchViewData = SearchViewData(repository.destCompletion, repository.destIsLoading)


    class SearchViewData(val completionList: MutableLiveData<List<String>>, val isLoading: MutableLiveData<Boolean>) {
        val searchString = MutableLiveData<String>()
    }

}
