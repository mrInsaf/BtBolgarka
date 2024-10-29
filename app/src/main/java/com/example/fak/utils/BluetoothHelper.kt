package com.example.fak.utils

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.UUID

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

    fun getPairedDevices(): List<BluetoothDevice> {
        val devicesList = mutableListOf<BluetoothDevice>()

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
            pairedDevices?.forEach { device ->
                devicesList.add(device)
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

    private inner class ConnectThread(device: BluetoothDevice) : Thread() {
        private val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        private val mmSocket: BluetoothSocket? = try {
            when (device.bondState) {
                BluetoothDevice.BOND_NONE -> {
                    println("Device is not bonded")
                    device.createBond()
                }
                BluetoothDevice.BOND_BONDING -> println("Device is bonding")
                BluetoothDevice.BOND_BONDED -> {
                    println("Device is bonded")
                    unpairDevice(device)
                    sleep(100L)
                    device.createBond()
                }
                else -> println("Unknown bond state: ${device.bondState}")
            }
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val method = device.javaClass.getMethod("createRfcommSocket", Int::class.javaPrimitiveType)
                method.invoke(device, 1) as BluetoothSocket
            } else {
                null
            }
        } catch (e: SecurityException) {
            println("Bluetooth_CONNECT permission was revoked $e")
            null
        }

        override fun run() {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                println("wtf")
            }
            bluetoothAdapter?.cancelDiscovery()

            mmSocket?.let { socket ->
                try {
                    socket.connect()
                } catch (e: IOException) {
                    println("Could not connect to device $e")
                    cancel()
                }
            }
        }

        private fun unpairDevice(device: BluetoothDevice) {
            try {
                val method = device.javaClass.getMethod("removeBond")
                method.invoke(device)
                println("Device unpaired successfully.")
            } catch (e: Exception) {
                println("Failed to unpair device: $e")
            }
        }

        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                println("Could not close the client socket")
            }
        }
    }

    fun connectToDevice(device: BluetoothDevice) {
        ConnectThread(device).start()
    }

}

