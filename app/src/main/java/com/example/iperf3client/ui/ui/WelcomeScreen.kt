package com.example.iperf3client.ui.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

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