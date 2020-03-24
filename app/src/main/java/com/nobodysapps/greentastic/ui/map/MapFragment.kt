package com.nobodysapps.greentastic.ui.map

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.appcompat.widget.ListPopupWindow
import androidx.fragment.app.Fragment
import com.nobodysapps.greentastic.BuildConfig
import com.nobodysapps.greentastic.R
import com.nobodysapps.greentastic.activity.GreentasticActivity
import com.nobodysapps.greentastic.activity.PermissionListener
import com.nobodysapps.greentastic.networking.ApiService
import com.nobodysapps.greentastic.networking.retrofit
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

    var apiService: ApiService = retrofit().create(ApiService::class.java)
    var compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        setupSearchTextView()
    }

    private fun setupSearchTextView() {
        destinationEditText.setOnEditorActionListener { v, actionId, event ->
            val searchString = v.text.toString()
            if (searchString.isNotEmpty()
                && (actionId == EditorInfo.IME_ACTION_SEARCH
                        || (event != null && event.action == ACTION_DOWN && event.keyCode == KEYCODE_ENTER))
            ) {
                showCompletionFor(searchString)
            }
            true
        }
    }

    private fun showCompletionFor(searchString: String) {
        val autoCompleteSingle = apiService.getAutoComplete(searchString)
        autoCompleteSingle
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<List<String>> {
                override fun onSuccess(suggestions: List<String>) {
                    Log.d("MapFragment", "suggestions: $suggestions")
                    context?.let {
                        ListPopupWindow(it).apply {
                            anchorView = destinationEditText
                            val adapter =
                                ArrayAdapter(it, android.R.layout.simple_list_item_1, suggestions)
                            setAdapter(adapter)
                            width = 300
                            height = 400
                            isModal = true
                        }.show()
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onError(e: Throwable) {
                    Log.d("MapFragment", "Some error $e")
                }

            })
    }

    override fun onResume() {
        super.onResume()
        checkStoragePermission()
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

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        if (!compositeDisposable.isDisposed) compositeDisposable.dispose()
    }
}