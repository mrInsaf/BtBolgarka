package com.example.fak.ui.viewModel

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fak.utils.BluetoothHelper

class BtBolgarkaViewModel(
    application: Application,
    private val bluetoothHelper: BluetoothHelper
) : ViewModel() {
    private val context = application
    private val bluetoothAdapter: BluetoothAdapter? = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

    var btBolgarkaUiState  by mutableStateOf(BtBolgarkaUiState())
        private set

    fun fetchPairedDevices() {
        val devicesList = bluetoothHelper.getPairedDevices()
        btBolgarkaUiState = btBolgarkaUiState.copy(pairedDevices = devicesList)
    }

}


class BtBolgarkaViewModelFactory(
    private val application: Application,
    private val bluetoothHelper: BluetoothHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BtBolgarkaViewModel::class.java)) {
            return BtBolgarkaViewModel(application, bluetoothHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}