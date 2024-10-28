package com.example.fak.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.fak.ui.viewModel.BtBolgarkaViewModel

@Composable
fun BtBolgarkaMainScreen(
    viewModel: BtBolgarkaViewModel
) {
    // Ваш контекст
    val context = LocalContext.current

    // Получение списка спаренных устройств
    LaunchedEffect(Unit) {
        viewModel.fetchPairedDevices()
    }

    // Отображение устройств из btBolgarkaUiState
    val pairedDevices = viewModel.btBolgarkaUiState.pairedDevices
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(text = "Сопряженные устройства")
        pairedDevices.forEach { device ->
            val name = device["name"] ?: "Неизвестное устройство"
            val address = device["address"] ?: "Нет адреса"
            Text(text = "$name - $address")
        }
    }
}
