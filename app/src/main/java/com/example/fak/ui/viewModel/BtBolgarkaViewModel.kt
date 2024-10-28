package com.example.fak.ui.viewModel

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.Manifest

class BtBolgarkaViewModel(application: Application) : ViewModel() {
    private val context = application
    private val bluetoothAdapter: BluetoothAdapter? = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

    var btBolgarkaUiState  by mutableStateOf(BtBolgarkaUiState())
        private set

    fun fetchPairedDevices() {
        val devicesList = mutableListOf<Map<String, String>>()

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
            pairedDevices?.forEach { device ->
                val deviceInfo = mapOf(
                    "name" to (device.name ?: "Неизвестное устройство"),
                    "address" to device.address
                )
                devicesList.add(deviceInfo)
            }
        } else {
            println("Bluetooth_CONNECT permission is not granted.")
        }

        // Обновление состояния UI
        btBolgarkaUiState = btBolgarkaUiState.copy(pairedDevices = devicesList)
    }

}


class BtBolgarkaViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BtBolgarkaViewModel::class.java)) {
            return BtBolgarkaViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}