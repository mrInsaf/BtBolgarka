package com.example.fak.ui.viewModel

import android.bluetooth.BluetoothDevice

data class BtBolgarkaUiState(
    val pairedDevicesString: List<Map<String, String>> = emptyList(),
    val scannedDevicesString: List<Map<String, String>> = mutableListOf(),

    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val scannedDevices: List<BluetoothDevice> = mutableListOf(),
)
