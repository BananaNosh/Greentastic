package com.nobodysapps.greentastic.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.nobodysapps.greentastic.networking.ApiService
import com.nobodysapps.greentastic.ui.search.SearchFragment
import com.nobodysapps.greentastic.utils.ResultObject
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchApiRepository @Inject constructor(private val apiService: ApiService){


    var sourceCompletion: MutableLiveData<ResultObject<List<String>>> = MutableLiveData()
    var destCompletion: MutableLiveData<ResultObject<List<String>>> = MutableLiveData()

//    private val compositeDisposable = CompositeDisposable()

    fun loadCompletion(searchString: String, searchViewType: Int) {
        // TODO add caching
        val autoCompleteSingle = apiService.getAutoComplete(searchString)
        autoCompleteSingle
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                when (searchViewType) {
                    SEARCH_VIEW_TYPE_SOURCE -> sourceCompletion
                    else -> destCompletion
                }.value = ResultObject()
            }
            .subscribe(object : SingleObserver<List<String>> {
                override fun onSuccess(suggestions: List<String>) {
                    Log.d(SearchFragment.TAG, "suggestions: $suggestions")

                    when (searchViewType) {
                        SEARCH_VIEW_TYPE_SOURCE -> sourceCompletion
                        else -> destCompletion
                    }.value = ResultObject(suggestions)
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