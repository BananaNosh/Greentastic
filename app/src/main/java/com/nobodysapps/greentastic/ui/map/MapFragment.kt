package com.nobodysapps.greentastic.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.nobodysapps.greentastic.BuildConfig
import com.nobodysapps.greentastic.R
import com.nobodysapps.greentastic.activity.GreentasticActivity
import com.nobodysapps.greentastic.activity.PermissionListener
import kotlinx.android.synthetic.main.fragment_map.*
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint

class MapFragment : Fragment() {

    private lateinit var mapViewModel: MapViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val provider = Configuration.getInstance()
        provider.userAgentValue = BuildConfig.APPLICATION_ID
//        mapViewModel =
//            ViewModelProviders.of(this).get(MapViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_map, container, false)
//        mapViewModel.text.observe(this, Observer {
//        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapController = mapView.controller
        mapController.setZoom(10.0)
//        mapController.animateTo(GeoPoint(50.79700, 8.92270))
        mapView.setMultiTouchControls(true)

    }

    override fun onResume() {
        super.onResume()
        val listener = object : PermissionListener {
            override fun onPermissionGranted(permission: String) {

            }

            override fun onPermissionDenied(permission: String) {

            }
        }
        (activity as GreentasticActivity).withPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            listener,
            getString(R.string.map_permission_explication)
        )
        mapView.onResume()
    }
}