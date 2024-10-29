package com.example.fak.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.fak.ui.viewModel.BtBolgarkaViewModel

@Composable
fun BtBolgarkaConnectionScreen(
    viewModel: BtBolgarkaViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.fetchPairedDevices()
    }

    // Отображение устройств из btBolgarkaUiState
    val pairedDevices = viewModel.btBolgarkaUiState.pairedDevices
    val scannedDevices = viewModel.btBolgarkaUiState.scannedDevices
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Сопряженные устройства")
            pairedDevices.forEach { device ->
                val name = device["name"] ?: "Неизвестное устройство"
                val address = device["address"] ?: "Нет адреса"
                Text(text = "$name - $address")
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
            scannedDevices.forEach { device ->
                val name = device["name"] ?: "Неизвестное устройство"
                val address = device["address"] ?: "Нет адреса"
                Text(text = "$name - $address")
            }
        }
    }
}