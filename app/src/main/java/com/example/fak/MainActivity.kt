package com.example.fak

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.fak.ui.theme.FakTheme
import android.Manifest
import androidx.activity.viewModels
import com.example.fak.ui.screens.BtBolgarkaMainScreen
import com.example.fak.ui.viewModel.BtBolgarkaViewModel
import com.example.fak.ui.viewModel.BtBolgarkaViewModelFactory
import com.example.fak.utils.BluetoothHelper
import com.example.fak.utils.PermissionManager

class MainActivity : ComponentActivity() {
    private lateinit var bluetoothHelper: BluetoothHelper
    private lateinit var permissionManager: PermissionManager

    private val viewModel: BtBolgarkaViewModel by viewModels {
        BtBolgarkaViewModelFactory(bluetoothHelper)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bluetoothHelper = BluetoothHelper(
            this,
            onDeviceFound = { device ->
                viewModel.addScannedDeviceToList(
                    device
                )
            }
        )

        permissionManager = PermissionManager(
            this,
            onBluetoothEnabled = {
                println("Bluetooth enabled")
//                bluetoothHelper.startDiscovery() // Начало поиска устройств
            },
            onPermissionDenied = {
                println("Bluetooth permission denied")
            }
        )

        if (bluetoothHelper.isBluetoothSupported()) {
            permissionManager.checkAndRequestPermissions()
        } else {
            println("Bluetooth is not supported")
        }

        setContent {
            FakTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BtBolgarkaMainScreen(viewModel = viewModel)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothHelper.stopDiscovery()
    }
}
