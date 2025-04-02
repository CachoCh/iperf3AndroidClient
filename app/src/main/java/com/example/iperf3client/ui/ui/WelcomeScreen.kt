package com.example.iperf3client.ui.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun WelcomeScreen(
    onNewTestButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        Text(text = "WELCOME SCREEN")
        Button(onClick = onNewTestButtonClicked) {
            Text("New Test")
        }
    }

}