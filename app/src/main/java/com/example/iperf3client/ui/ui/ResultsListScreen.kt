package com.example.iperf3client.ui.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ResultsListScreen(testResults: List<String>) {

    LazyColumn(
        modifier = Modifier
            .wrapContentHeight()
            .padding(top = 10.dp)
    ) {
        items(testResults) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                elevation = CardDefaults.cardElevation(10.dp),
            ) {
                Text(text = "Test: $item",
                    modifier = Modifier.padding(10.dp))
            }

        }
    }
}