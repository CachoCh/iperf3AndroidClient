package com.example.iperf3client.ui.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.iperf3client.viewmodels.TestViewModel
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer

@Composable
fun RunningTestScreen(
    testViewModel: TestViewModel,
    modifier: Modifier = Modifier,
    onCancelButtonClicked: () -> Unit
) {
    val testResults by testViewModel.testResults.collectAsState()
    val modelProducer by testViewModel.modelProducer.collectAsState()


    TabScreen(testViewModel, testResults, modelProducer)
    //ResultsList(testResults)

}

@Composable
fun TabScreen(testViewModel: TestViewModel, testResults: List<String>, modelProducer: CartesianChartModelProducer) {
    var tabIndex by remember { mutableStateOf(0) }

    val tabs = listOf("Chart", "Map")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index }
                )
            }
        }
        when (tabIndex) {
            0 -> ChartScreen(modelProducer, testResults)
            1 -> MapScreen(testViewModel)
        }
    }
}



