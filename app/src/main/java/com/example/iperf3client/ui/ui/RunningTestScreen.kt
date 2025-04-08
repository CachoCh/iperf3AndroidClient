package com.example.iperf3client.ui.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.iperf3client.viewmodels.TestViewModel

@Composable
fun RunningTestScreen(
    testViewModel: TestViewModel,
    modifier: Modifier = Modifier,
    onCancelButtonClicked: () -> Unit
) {
    val testResults by testViewModel.testResults.collectAsState()

    //VerticalScrollingText(testResults)
    ResultsList(testResults)

}

@Composable
fun VerticalScrollingText(testResults: List<String>) {
    Text(
        text = testResults.joinToString(separator = "\n"),
        modifier = Modifier.horizontalScroll(rememberScrollState())
    )
}
@Composable
fun ResultsList(testResults: List<String>) {
    LazyColumn(
        modifier = Modifier.height(500.dp)
    ) {
        items(testResults) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                elevation = CardDefaults.cardElevation(10.dp),
            ) {
                Text(text = "Test: $item")
            }

        }
    }
}