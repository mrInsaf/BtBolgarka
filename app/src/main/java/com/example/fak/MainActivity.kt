package com.example.fak

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.fak.ui.theme.FakTheme
import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import androidx.activity.viewModels
import com.example.fak.ui.screens.BtBolgarkaMainScreen
import com.example.fak.ui.viewModel.BtBolgarkaViewModel
import com.example.fak.ui.viewModel.BtBolgarkaViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var bluetoothLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var bluetoothAdapter: BluetoothAdapter // Добавление переменной
    private val discoveredDevices = mutableListOf<BluetoothDevice>()

    private val viewModel: BtBolgarkaViewModel by viewModels {
        BtBolgarkaViewModelFactory(application)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Обнаружено устройство, получаем объект BluetoothDevice
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                            val deviceName = it.name
                            val deviceHardwareAddress = it.address // MAC address
                            println("Discovered device: $deviceName, Address: $deviceHardwareAddress")
                            discoveredDevices.add(it) // Добавляем устройство в список
                        } else {
                            println("Bluetooth_CONNECT permission is not granted.")
                        }
                    }
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBluetooth() // Инициализация BluetoothAdapter
        initializeLaunchers()


        startDiscovery() // Запуск сканирования устройств

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

        // Регистрация для получения событий обнаружения устройств
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)
    }

    private fun initializeBluetooth() {
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter // Инициализация bluetoothAdapter
        if (bluetoothAdapter == null) {
            println("Bluetooth is not supported")
        }
    }

    private fun initializeLaunchers() {
        // Регистрация для получения результатов разрешений
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                requestBluetoothEnable() // Запрос на включение Bluetooth, если разрешение предоставлено
            } else {
                println("Bluetooth permission denied")
            }
        }

        // Регистрация для получения результатов запуска активностей
        bluetoothLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                println("Bluetooth enabled")
            } else {
                println("Bluetooth not enabled")
            }
        }

        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED -> {
                requestBluetoothEnable() // Разрешение уже предоставлено
            }
            else -> {
                // Запрашиваем разрешение
                permissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }
    }

    private fun requestBluetoothEnable() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        bluetoothLauncher.launch(intent)
    }


    private fun startDiscovery() {
        // Проверка разрешения на сканирование Bluetooth
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            if (bluetoothAdapter.isDiscovering) {
                bluetoothAdapter.cancelDiscovery() // Отмена предыдущего поиска, если он идет
            }
            val discoveryStarted = bluetoothAdapter.startDiscovery() // Запуск сканирования
            if (discoveryStarted) {
                println("Discovery started")
            } else {
                println("Discovery failed to start")
            }
        } else {
            // Запрашиваем разрешение на сканирование Bluetooth
            permissionLauncher.launch(Manifest.permission.BLUETOOTH_SCAN)
        }
    }

}
