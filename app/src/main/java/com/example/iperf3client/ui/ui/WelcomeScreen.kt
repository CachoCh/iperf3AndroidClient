package com.example.iperf3client.ui.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.iperf3client.R
import com.example.iperf3client.ui.chart
import com.example.iperf3client.viewmodels.TestViewModel

@Composable
fun WelcomeScreen(
    onNewTestButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
    testViewModel: TestViewModel
) {
    val modelProducer by testViewModel.modelProducer.collectAsState()
    val testResults by testViewModel.testResults.collectAsState()
    val testUiState by testViewModel.uiState.collectAsState()

    var server by rememberSaveable { mutableStateOf(testUiState.server) }
    var port by rememberSaveable { mutableIntStateOf(testUiState.port) }
    var duration by rememberSaveable { mutableIntStateOf(testUiState.duration) }
    var reverse by rememberSaveable { mutableStateOf(testUiState.reverse) }

    var direction: String = if(reverse) {
        stringResource(R.string.download)
    } else {
        stringResource(R.string.upload)
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "IPerf3 Android client",
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center)
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {

        Button(onClick = onNewTestButtonClicked) {
            Text("New Test")
        }
        Text(text = "Last / running test:")
        Text(text = "$direction for $duration s")
        Text(text = "Server: $server:$port")
        chart(modelProducer)

        ResultsList(testResults)
    }

}