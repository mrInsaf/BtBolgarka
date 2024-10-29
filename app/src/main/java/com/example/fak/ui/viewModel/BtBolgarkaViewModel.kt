package com.example.fak.ui.viewModel

import android.bluetooth.BluetoothDevice
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fak.utils.BluetoothHelper

class BtBolgarkaViewModel(
    private val bluetoothHelper: BluetoothHelper
) : ViewModel() {
    var btBolgarkaUiState  by mutableStateOf(BtBolgarkaUiState())
        private set

    fun fetchPairedDevices() {
        val devicesList = bluetoothHelper.getPairedDevices()
        btBolgarkaUiState = btBolgarkaUiState.copy(pairedDevices = devicesList)
    }

    fun startScanningDevices() {
        bluetoothHelper.startDiscovery()
    }

    fun stopScanningDevices() {
        bluetoothHelper.stopDiscovery()
    }

    fun addScannedDeviceToList(deviceInfo: Map<String, String>) {
        btBolgarkaUiState = btBolgarkaUiState.copy(
            scannedDevicesString = btBolgarkaUiState.scannedDevicesString + deviceInfo
        )
    }

    fun addScannedDeviceToList(device: BluetoothDevice) {
        btBolgarkaUiState = btBolgarkaUiState.copy(
            scannedDevices = btBolgarkaUiState.scannedDevices + device
        )
    }

    fun onConnectButtonClick(device: BluetoothDevice) {
        bluetoothHelper.connectToDevice(device)
    }
}


class BtBolgarkaViewModelFactory(
    private val bluetoothHelper: BluetoothHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BtBolgarkaViewModel::class.java)) {
            return BtBolgarkaViewModel(bluetoothHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}