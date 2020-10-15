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
import androidx.lifecycle.ViewModelProvider
import com.nobodysapps.greentastic.R
import com.nobodysapps.greentastic.activity.GreentasticActivity
import com.nobodysapps.greentastic.activity.PermissionListener
import com.nobodysapps.greentastic.application.GreentasticApplication
import com.nobodysapps.greentastic.errorHandling.NoGPSError
import com.nobodysapps.greentastic.errorHandling.NoLocationPermissionError
import com.nobodysapps.greentastic.repository.SearchApiRepository
import com.nobodysapps.greentastic.repository.TransportRepository
import com.nobodysapps.greentastic.ui.ViewModelFactory
import com.nobodysapps.greentastic.ui.map.MapFragment
import com.nobodysapps.greentastic.ui.transport.TransportFragment
import com.nobodysapps.greentastic.ui.views.SearchView
import com.nobodysapps.greentastic.utils.ResultObject
import com.nobodysapps.greentastic.utils.ResultState
import kotlinx.android.synthetic.main.search_fragment.*
import java.util.*
import javax.inject.Inject

class SearchFragment : Fragment() {
    @Inject
    lateinit var searchApiRepository: SearchApiRepository

    @Inject
    lateinit var transportRepository: TransportRepository

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var searchWasStarted: Boolean = false

    private lateinit var viewModel: SearchViewModel

    override fun onAttach(context: Context) {
        (requireActivity().application as GreentasticApplication).appComponent.inject(this)
        super.onAttach(context)
    }

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
        Log.d(TAG, sourceSearchView.toString())
    }

    private fun replaceFragment(fragmentToGo: Class<*>) {
        replaceFragment(fragmentToGo.newInstance() as Fragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_layout, fragment).commit()
    }

    private fun setupSearchTextViews() {
        listOf(viewModel.sourceData, viewModel.destData).zip(
            listOf(
                sourceSearchView,
                destinationSearchView
            )
        ).forEach { dataViewPair ->
            val searchView = dataViewPair.second
            dataViewPair.first.searchString.observe(viewLifecycleOwner, { t ->
                if (t != searchView.editText.text.toString()) {
                    searchView.editText.setText(t)
                }
            })
            dataViewPair.first.completionResult.observe(viewLifecycleOwner, { completionsResult ->
                searchView.isLoading = completionsResult.state == ResultState.LOADING
                if (completionsResult.state == ResultState.LOADED) {
                    searchView.showPopup(completionsResult.value ?: emptyList())
                }
            })
            // TODO change focus to second searchView only after completion was clicked
            val onEditTextAction = { _: Int, _: KeyEvent? ->
                val searchString = searchView.editText.text.toString()
                if (searchString.isNotEmpty()) {
                    searchApiRepository.loadCompletion(
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
            val onNoPopupItemClicked: () -> Unit = {
                dataViewPair.first.completionResult.value?.value?.firstOrNull()?.let {
                    onCompletionClicked(it, searchView)
                }
            }
            val onTextChanged = { text: String ->
                dataViewPair.first.searchString.value = text
            }
            searchView.setListener(
                onEditTextAction,
                onPopupItemClicked,
                onNoPopupItemClicked,
                onTextChanged
            )
        }
    }

    private fun onCompletionClicked(selectedCompletion: String, searchView: SearchView) {
        val searchViewData = when (searchView) {
            sourceSearchView -> viewModel.sourceData
            else -> viewModel.destData
        }
        searchViewData.searchString.value = selectedCompletion
        searchViewData.completionResult.value = ResultObject(Collections.emptyList())
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
                searchForRouteAndOpenTransportFragment("$lat,$long", dest)
            }, { error ->
                Log.d(TAG, "location error $error")
                // TODO dialog source oder enable GPS
                // searchwasstarted = false ??
            })
        } else {
            searchForRouteAndOpenTransportFragment(source, dest)
        }
    }

    private fun searchForRouteAndOpenTransportFragment(source: String, dest: String) {
        replaceFragment(TransportFragment.newInstance(source, dest))
//        lifecycleScope.launchWhenCreated {
//            transportRepository.loadVehicles(source, dest)
//        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "flag: $searchWasStarted")
        if (searchWasStarted) {
            getSourceAndDestAndSearch()
        }
//        searchForRoute("zurich", "frankfurt") // TODO remove
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
        builder.setMessage(getString(R.string.dialog_enable_gps_message))//TODO string
            .setCancelable(false)
            .setPositiveButton(
                android.R.string.yes
            ) { _, _ ->
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

    override fun onDestroyView() {
        super.onDestroyView()
        sourceSearchView.dismissPopup()
        destinationSearchView.dismissPopup()
    }

    companion object {
        const val TAG = "SearchFragment"
        private const val SEARCH_STARTED_KEY = "searchStarted"
    }
}
