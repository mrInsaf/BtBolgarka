package com.example.fak.utils

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class PermissionManager(
    private val activity: ComponentActivity,
    private val onBluetoothEnabled: () -> Unit,
    private val onPermissionDenied: () -> Unit
) {
    private val permissionLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        // Проверяем, были ли разрешения предоставлены
        if (permissions.values.all { it }) {
            requestBluetoothEnable()
        } else {
            onPermissionDenied()
        }
    }

    private val bluetoothLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onBluetoothEnabled()
        } else {
            onPermissionDenied()
        }
    }

    fun checkAndRequestPermissions() {
        val requiredPermissions = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
        )

        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isEmpty()) {
            requestBluetoothEnable()
        } else {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
    private fun requestBluetoothEnable() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        bluetoothLauncher.launch(intent)
    }
}
