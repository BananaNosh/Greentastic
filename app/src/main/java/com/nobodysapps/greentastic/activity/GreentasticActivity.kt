package com.nobodysapps.greentastic.activity

import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

abstract class GreentasticActivity: AppCompatActivity() {
    private var requestCode = 0
    private val requestPermissionLambdas: MutableMap<Pair<Int, String>, PermissionListener> = HashMap()

    // for requesting Permissions
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (permissions.size == 1) {
            val permission = permissions[0]
            val requestPermissionPair = Pair(requestCode, permission)
            val lambda = requestPermissionLambdas.get(requestPermissionPair)
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                lambda?.onPermissionGranted(permission)
            } else {
                lambda?.onPermissionDenied(permission)
            }
            requestPermissionLambdas.remove(requestPermissionPair)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    // for requesting Permissions
    fun withPermission(permission: String, listener: PermissionListener, explicationText: String? = null) {
        if (ActivityCompat.checkSelfPermission(this, permission)
            != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission) && explicationText != null) {  // repeatedly denied
                showExplicationText(explicationText, permission, listener)
            } else {
                requestPermission(permission, listener)  //TODO handling after "never ask again" was clicked
            }
        } else {
            listener.onPermissionGranted(permission)
        }
    }

    private fun showExplicationText(
        explicationText: String,
        permission: String,
        listener: PermissionListener
    ) {
        AlertDialog.Builder(this).setMessage(explicationText)
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                listener.onPermissionDenied(permission)
            }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                requestPermission(permission, listener)
            }.setCancelable(false)
            .create().show()
    }

    private fun requestPermission(
        permission: String,
        listener: PermissionListener
    ) {
        requestPermissionLambdas.put(Pair(requestCode, permission), listener)
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        requestCode++
    }

    override fun onDestroy() {
        super.onDestroy()
        requestPermissionLambdas.clear()
    }
}


interface PermissionListener {
    fun onPermissionGranted(permission: String)
    fun onPermissionDenied(permission: String)
    fun onPermissionDeniedBefore(permission: String) {}
}