package com.example.fak.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BtDeviceComponent(
    deviceName: String,
    deviceAddress: String,
    onConnectButtonClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Text(text = deviceName, modifier = Modifier.width(160.dp))
        Button(onClick = { onConnectButtonClick() }) {
            Text(text = "Подключиться")
        }
    }
}

@Preview
@Composable
fun BtDeviceComponentPreview() {
    BtDeviceComponent(
        deviceName = "device name",
        deviceAddress = "device address"
    ) {

    }
}