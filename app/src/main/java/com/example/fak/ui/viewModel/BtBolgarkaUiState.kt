package com.example.fak.ui.viewModel

data class BtBolgarkaUiState(
    val pairedDevices: List<Map<String, String>> = emptyList(),
    val scannedDevices: List<Map<String, String>> = mutableListOf()
)
