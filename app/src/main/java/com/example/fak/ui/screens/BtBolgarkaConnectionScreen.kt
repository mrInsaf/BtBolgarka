package com.example.fak.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.fak.ui.components.BtDeviceComponent
import com.example.fak.ui.viewModel.BtBolgarkaViewModel

@Composable
fun BtBolgarkaConnectionScreen(
    viewModel: BtBolgarkaViewModel,
    modifier: Modifier,
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.fetchPairedDevices()
    }

    // Отображение устройств из btBolgarkaUiState
    val pairedDevices = viewModel.btBolgarkaUiState.pairedDevices
    val scannedDevices = viewModel.btBolgarkaUiState.scannedDevices
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(
                state = rememberScrollState()
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Сопряженные устройства")
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                pairedDevices.forEach { device ->
                    val name = device.name ?: "Неизвестное устройство" // Добавляем значение по умолчанию
                    val address = device.address
                    BtDeviceComponent(
                        deviceName = name,
                        deviceAddress = address
                    ) {
                        viewModel.onConnectButtonClick(device)
                    }
                }
            } else {
                // Обработайте ситуацию, когда разрешение не предоставлено
                println("Bluetooth_CONNECT permission is not granted.")
            }
        }
        Button(onClick = { viewModel.fetchPairedDevices() }) {
            Text(text = "Обновить список сопряженных устройств")
        }

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Список доступных устройств")
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = { viewModel.startScanningDevices() }) {
                    Text(text = "Начать поиск")
                }

                Button(onClick = { viewModel.stopScanningDevices() }) {
                    Text(text = "Остановить поиск")
                }

            }
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                scannedDevices.forEach { device ->
                    val name = device.name ?: "Неизвестное устройство" // Добавляем значение по умолчанию
                    val address = device.address
                    BtDeviceComponent(
                        deviceName = name,
                        deviceAddress = address
                    ) {
                        viewModel.onConnectButtonClick(device)
                    }
                }
            } else {
                // Обработайте ситуацию, когда разрешение не предоставлено
                println("Bluetooth_CONNECT permission is not granted.")
            }
        }
    }
}