package com.nobodysapps.greentastic.ui.map

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
import com.nobodysapps.greentastic.BuildConfig
import com.nobodysapps.greentastic.R
import com.nobodysapps.greentastic.activity.GreentasticActivity
import com.nobodysapps.greentastic.activity.PermissionListener
import com.nobodysapps.greentastic.error_handling.NoGPSError
import com.nobodysapps.greentastic.error_handling.NoLocationPermissionError
import com.nobodysapps.greentastic.networking.ApiService
import com.nobodysapps.greentastic.networking.model.VehicleAggregate
import com.nobodysapps.greentastic.networking.retrofit
import com.nobodysapps.greentastic.ui.views.SearchView
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_map.*
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.CopyrightOverlay

class MapFragment : Fragment() {

    private var apiService: ApiService = retrofit().create(ApiService::class.java)
    var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var searchWasStarted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            searchWasStarted = it.getBoolean(SEARCH_STARTED_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val provider = Configuration.getInstance()
        provider.userAgentValue = BuildConfig.APPLICATION_ID
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapController = mapView.controller
        mapController.setZoom(10.0)
        mapController.animateTo(GeoPoint(50.79700, 8.92270))
        mapView.setMultiTouchControls(true)


        //Attribution
        val attribution = CopyrightOverlay(context).apply {
            setAlignRight(true)
        }
        mapView.overlays.add(attribution)

        setupSearchTextViews()
    }

    private fun setupSearchTextViews() {
        listOf(destinationSearchView, sourceSearchView).forEach { searchView ->
            val onEditTextAction = { _: Int, _: KeyEvent? ->
                val searchString = searchView.editText.text.toString()
                if (searchString.isNotEmpty()) {
                    showCompletionFor(searchString, searchView)
                    true
                } else {
                    false
                }
            }
            val onPopupItemClicked = { text: String, _: Int ->
                onCompletionClicked(text, searchView)
                true
            }
            searchView.setListener(onEditTextAction, onPopupItemClicked)
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
        searchView.editText.setText(selectedCompletion)
        val source = sourceSearchView.editText.text.toString()
        val dest = destinationSearchView.editText.text.toString()
        if (dest.isNotEmpty()) {
            if (source.isEmpty()) {
                // source = currentLocation TODO
            } else {
                searchForRoute(source, dest)
            }
        } else {
            destinationSearchView.editText.requestFocus()
        }
    }

    private fun searchForRoute(source: String, dest: String) {
        // TODO API
        val vehiclesSingle = apiService.getDirections(source, dest) // TODO cartype, weights
        vehiclesSingle
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                // TODO loading
            }
            .doFinally {

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
        getCurrentLocation({ location ->  // TODO remove
            Log.d(TAG, "location $location")
        }, { error ->
            Log.d(TAG, "location error $error")
        })

        checkStoragePermission()
        if (searchWasStarted) {
            // TODO(implement)
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
        if (!compositeDisposable.isDisposed) compositeDisposable.dispose()
    }

    companion object {
        const val TAG = "MapFragment"
        private const val SEARCH_STARTED_KEY = "searchStarted"
    }
}