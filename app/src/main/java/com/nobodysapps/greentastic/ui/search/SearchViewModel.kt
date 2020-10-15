package com.nobodysapps.greentastic.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nobodysapps.greentastic.repository.SearchApiRepository
import com.nobodysapps.greentastic.utils.ResultObject
import javax.inject.Inject

class SearchViewModel @Inject constructor(repository: SearchApiRepository): ViewModel() {

    val sourceData: SearchViewData = SearchViewData(repository.sourceCompletion)
    val destData: SearchViewData = SearchViewData(repository.destCompletion)


    class SearchViewData(val completionResult: MutableLiveData<ResultObject<List<String>>>) {
        val searchString = MutableLiveData<String>()
    }

}
