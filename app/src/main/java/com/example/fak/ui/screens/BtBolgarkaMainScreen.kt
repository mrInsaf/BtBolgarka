package com.example.fak.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fak.R
import com.example.fak.ui.viewModel.BtBolgarkaViewModel

data class BottomNavigationBarItem(
    val name: String,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector,
)

enum class BtBolgarkaScreen() {
    Connect,
    Send,
    Settings,
}

@Composable
fun BtBolgarkaMainScreen(
    viewModel: BtBolgarkaViewModel
) {
    val bottomNavigationBarItems = listOf(
        BottomNavigationBarItem(
            name = stringResource(R.string.connect),
            selectedIcon = Icons.Filled.Search,
            unSelectedIcon = Icons.Outlined.Search
        ),
        BottomNavigationBarItem(
            name = stringResource(R.string.send),
            selectedIcon = Icons.Filled.Send,
            unSelectedIcon = Icons.Outlined.Send
        ),
        BottomNavigationBarItem(
            name = stringResource(R.string.settings),
            selectedIcon = Icons.Filled.Settings,
            unSelectedIcon =  Icons.Outlined.Settings
        )
    )
    var selectedIcon by rememberSaveable {
        mutableIntStateOf(0)
    }
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {
                bottomNavigationBarItems.forEachIndexed { index, navigationBarItem ->
                    NavigationBarItem(
                        selected = selectedIcon == index,
                        onClick = { selectedIcon = index },
                        label = { Text(text = navigationBarItem.name) },
                        icon = { Icon(
                            imageVector = if (
                                index == selectedIcon
                                ) {
                                    navigationBarItem.selectedIcon
                                }
                                else navigationBarItem.unSelectedIcon,
                            contentDescription = navigationBarItem.name
                        )}
                    )
                }
            }
        },
    ) {innerPadding ->
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = BtBolgarkaScreen.Connect.name
        ) {
            composable(route = BtBolgarkaScreen.Connect.name) {
                BtBolgarkaConnectionScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
