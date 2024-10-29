package com.example.fak.utils

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class BluetoothHelper(
    private val context: Context,
    private val onDeviceFound: (BluetoothDevice) -> Unit
) {

    private val bluetoothManager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == BluetoothDevice.ACTION_FOUND) {
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                device?.let { onDeviceFound(it) }
            }
        }
    }

    fun isBluetoothSupported(): Boolean {
        return bluetoothAdapter != null
    }

    fun getPairedDevices(): List<Map<String, String>> {
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

        return devicesList
    }

    fun startDiscovery() {
        // Проверяем наличие разрешения
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            try {
                // Регистрируем BroadcastReceiver и запускаем сканирование
                val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                context.registerReceiver(receiver, filter)
                val isDiscoveryStarted = bluetoothAdapter?.startDiscovery() ?: false

                if (isDiscoveryStarted) {
                    println("Bluetooth discovery started")
                } else {
                    println("Failed to start Bluetooth discovery")
                }
            } catch (e: SecurityException) {
                println("SecurityException: Unable to start discovery due to missing permission")
            }
        } else {
            // Логика для запроса разрешения, если оно не предоставлено
            println("Bluetooth_SCAN permission is not granted.")
        }
    }

    fun stopDiscovery() {
        try {
            context.unregisterReceiver(receiver) // Отключение BroadcastReceiver
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                bluetoothAdapter?.cancelDiscovery() // Остановка сканирования
                println("Bluetooth discovery stopped")
            } else {
                println("BLUETOOTH_SCAN permission is not granted.")
            }
        } catch (e: SecurityException) {
            println("SecurityException: Unable to stop discovery due to missing permission")
        }
    }

}

