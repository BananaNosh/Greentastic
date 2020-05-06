package com.nobodysapps.greentastic.ui.search

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.nobodysapps.greentastic.networking.ApiService
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SearchApiRepository @Inject constructor(val apiService: ApiService){


    var sourceCompletion: MutableLiveData<List<String>> = MutableLiveData()
    var sourceIsLoading: MutableLiveData<Boolean> = MutableLiveData()
    var destCompletion: MutableLiveData<List<String>> = MutableLiveData()
    var destIsLoading: MutableLiveData<Boolean> = MutableLiveData()


    fun loadCompletion(searchString: String, searchViewType: Int) {
        val autoCompleteSingle = apiService.getAutoComplete(searchString)
        autoCompleteSingle
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
//                searchView.isLoading = true
            }
            .doFinally {
//                searchView.isLoading = false
            }
            .subscribe(object : SingleObserver<List<String>> {
                override fun onSuccess(suggestions: List<String>) {
                    Log.d(SearchFragment.TAG, "suggestions: $suggestions")

                    if(searchViewType == 0)
                        sourceCompletion.value = suggestions
                    else
                        destCompletion.value = suggestions
                }

                override fun onSubscribe(d: Disposable) {
                    Log.d(SearchFragment.TAG, "onSubscribe called")
//                    compositeDisposable.add(d)
                }

                override fun onError(e: Throwable) {
                    Log.d(SearchFragment.TAG, "Some error $e") //TODO add error message in dialog
                }

            })
    }

    companion object {
        const val SEARCH_VIEW_TYPE_SOURCE = 0
        const val SEARCH_VIEW_TYPE_DEST = 1
    }

}