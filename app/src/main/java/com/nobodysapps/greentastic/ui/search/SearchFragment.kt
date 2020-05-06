package com.nobodysapps.greentastic.ui.search

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nobodysapps.greentastic.R
import com.nobodysapps.greentastic.activity.GreentasticActivity
import com.nobodysapps.greentastic.activity.PermissionListener
import com.nobodysapps.greentastic.application.GreentasticApplication
import com.nobodysapps.greentastic.errorHandling.NoGPSError
import com.nobodysapps.greentastic.errorHandling.NoLocationPermissionError
import com.nobodysapps.greentastic.networking.ApiService
import com.nobodysapps.greentastic.networking.model.VehicleAggregate
import com.nobodysapps.greentastic.ui.ViewModelFactory
import com.nobodysapps.greentastic.ui.map.MapFragment
import com.nobodysapps.greentastic.ui.views.SearchView
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.map_fragment.*
import kotlinx.android.synthetic.main.search_fragment.*
import javax.inject.Inject

class SearchFragment : Fragment() {

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var searchWasStarted: Boolean = false

    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            searchWasStarted = it.getBoolean(SEARCH_STARTED_KEY)
        }
        viewModel = ViewModelProvider(this, viewModelFactory).get(
            SearchViewModel::class.java
        )
        Log.d(TAG, viewModel.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        replaceFragment(MapFragment::class.java)
        setupSearchTextViews()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as GreentasticApplication).appComponent.inject(this)
    }

    private fun replaceFragment(fragmentToGo: Class<*>) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_layout, fragmentToGo.newInstance() as Fragment).commit()
    }

    private fun setupSearchTextViews() {
        listOf(viewModel.sourceData, viewModel.destData).zip(
            listOf(
                sourceSearchView,
                destinationSearchView
            )
        ).forEach { dataViewPair ->
            val searchView = dataViewPair.second
            dataViewPair.first.searchString.observe(viewLifecycleOwner, Observer { t ->
                if (t != searchView.editText.text.toString()) {
                    searchView.editText.setText(t)
                }
            })
            dataViewPair.first.completionList.observe(viewLifecycleOwner, Observer { completions ->
                searchView.showPopup(completions)
            })
            // TODO change focus to second searchView only after completion was clicked
            val onEditTextAction = { _: Int, _: KeyEvent? ->
                val searchString = searchView.editText.text.toString()
                if (searchString.isNotEmpty()) {
                    viewModel.repository.loadCompletion(
                        searchString,
                        if (searchView == destinationSearchView) SearchApiRepository.SEARCH_VIEW_TYPE_DEST
                        else SearchApiRepository.SEARCH_VIEW_TYPE_SOURCE
                    )
                    true
                } else {
                    false
                }
            }
            val onPopupItemClicked = { text: String, _: Int ->
                onCompletionClicked(text, searchView)
                true
            }
            val onTextChanged = { text: String ->
                dataViewPair.first.searchString.value = text
            }
            searchView.setListener(onEditTextAction, onPopupItemClicked, onTextChanged)
        }
    }

    private fun showCompletionFor(searchString: String, searchView: SearchView) {
        val autoCompleteSingle = apiService.getAutoComplete(searchString)
        autoCompleteSingle
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                searchView.isLoading = true
            }
            .doFinally {
                searchView.isLoading = false
            }
            .subscribe(object : SingleObserver<List<String>> {
                override fun onSuccess(suggestions: List<String>) {
                    Log.d(TAG, "suggestions: $suggestions")
                    context?.let {
                        searchView.showPopup(suggestions)
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "onSubscribe called")
                    compositeDisposable.add(d)
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "Some error $e") //TODO add error message in dialog
                }

            })
    }

    private fun onCompletionClicked(selectedCompletion: String, searchView: SearchView) {
        when (searchView) {
            sourceSearchView -> viewModel.sourceData
            else -> viewModel.destData
        }.searchString.value = selectedCompletion
        val dest = viewModel.destData.searchString.value
        if (dest != null && dest.isNotEmpty()) {
            searchWasStarted = true
            getSourceAndDestAndSearch()
        } else {
            destinationSearchView.editText.requestFocus()

        }
    }

    private fun getSourceAndDestAndSearch() {
        val source = sourceSearchView.editText.text.toString()
        val dest = destinationSearchView.editText.text.toString()

        if (source.isEmpty()) {
            getCurrentLocation({ (lat, long) ->
                Log.d(TAG, "location $lat, $long")
                searchForRoute("$lat,$long", dest)
            }, { error ->
                Log.d(TAG, "location error $error")
                // TODO dialog source oder enable GPS
                // searchwasstarted = false ??
            })
        } else {
            searchForRoute(source, dest)
        }
    }

    private fun searchForRoute(source: String, dest: String) {
        val vehiclesSingle = apiService.getDirections(source, dest) // TODO cartype, weights
        vehiclesSingle
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                // TODO loading
            }
            .doFinally {
                searchWasStarted = false
            }
            .subscribe(object : SingleObserver<VehicleAggregate> {
                override fun onSuccess(vehicles: VehicleAggregate) {
                    Log.d(TAG, "vehicles: $vehicles")

                }

                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "onSubscribe called")
                    compositeDisposable.add(d)
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "Some error $e")
                }

            })
    }

    override fun onResume() {
        super.onResume()
        checkStoragePermission()
        Log.d(TAG, "flag: $searchWasStarted")
        if (searchWasStarted) {
            getSourceAndDestAndSearch()
        }
    }

    private fun checkStoragePermission() {
        val listener = object : PermissionListener {
            override fun onPermissionGranted(permission: String) {
                mapView.onResume()
            }

            override fun onPermissionDenied(permission: String) {

            }
        }
        (activity as GreentasticActivity).withPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            listener,
            getString(R.string.map_permission_explication)
        )
    }

    private fun getCurrentLocation(
        onSuccess: (Pair<Double, Double>) -> Unit,
        onFailure: (Error) -> Unit
    ) {
        context?.let {
            val locationManager = it.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                askForGPS(onFailure)
            } else {
                checkLocationPermission({
                    val lastKnownLocation =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    lastKnownLocation?.let {
                        onSuccess(Pair(it.latitude, it.longitude))
                    } ?: onFailure(Error("No location found"))
                }, onFailure)
            }
        }
    }

    private fun askForGPS(onFailure: (Error) -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(getString(R.string.dialog_enable_gps_message))
            .setCancelable(false)  //TODO string
            .setPositiveButton(
                android.R.string.yes
            ) { dialog, which ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton(
                android.R.string.no
            ) { dialog, _ ->
                onFailure(NoGPSError())
                dialog.cancel()
            }
        val alertDialog = builder.create()
        alertDialog.show()
    }


    private fun checkLocationPermission(
        onSuccess: () -> Unit,
        onFailure: (Error) -> Unit
    ) {
        val listener = object : PermissionListener {
            override fun onPermissionGranted(permission: String) {
                onSuccess()
            }

            override fun onPermissionDenied(permission: String) {
                onFailure(NoLocationPermissionError())  // TODO replace error with general ErrorType
            }
        }
        (activity as GreentasticActivity).withPermission(
            Manifest.permission.ACCESS_FINE_LOCATION,
            listener,
            null  // explication is not necessary here
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(SEARCH_STARTED_KEY, searchWasStarted)
        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        if (!compositeDisposable.isDisposed) {
            Log.d(TAG, "composite disposed")
            compositeDisposable.dispose()
        }
    }

    companion object {
        const val TAG = "SearchFragment"
        private const val SEARCH_STARTED_KEY = "searchStarted"
    }
}
