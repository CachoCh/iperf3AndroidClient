package com.example.iperf3client.ui

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.example.iperf3client.data.ExecutedTestConfig
import com.example.iperf3client.data.TestDatabase
import com.example.iperf3client.viewmodels.TestViewModel

@Composable
fun MeasurementsScreen(
    testViewModel: TestViewModel,
    modifier: Modifier = Modifier,
    onCancelButtonClicked: () -> Unit,
    onItemClick: (tid: Int?) -> Unit,
    onShareClick: () -> Unit
) {

    val testResults by testViewModel.executedTestsList.collectAsState()

    Column() {
        Row( verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Tests Results: ",
                modifier = Modifier.weight(7f)
            )
        }
        executedTestsList(
            testResults,
            onDeleteTestResultsClicked = { executedTest ->
                testViewModel.deleteExecutedTestsWithResults(
                    executedTest
                )
            },
            onItemClick
        )
    }
}

@Composable
fun executedTestsList(
    testResults: List<ExecutedTestConfig>,
    onDeleteTestResultsClicked: (executedTestId: ExecutedTestConfig) -> Unit,
    onItemClick: (tid: Int?) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .wrapContentHeight()
            .heightIn(max = 1000.dp)
    ) {
        items(testResults) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clickable {
                        onItemClick(item.tid)
                    },
                elevation = CardDefaults.cardElevation(10.dp),
            ) {
                Row {
                    Column(modifier = Modifier.weight(5f)) {
                        Text(text = "${item.date}")
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        IconButton(
                            onClick = {
                                onDeleteTestResultsClicked(item)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null
                            )
                        }
                    }
                }

                /*
                CACHO: update  [ ID] Interval           Transfer     Bandwidth       Retr
                CACHO: update  [ 65]   0.00-10.04  sec   445 MBytes   372 Mbits/sec    0             sender
                CACHO: update  [ 65]   0.00-10.04  sec   443 MBytes   370 Mbits/sec                  receiver
                 */

                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = "BW: ${item.bandwidth}",
                        Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        color = Color.Cyan
                    )
                    Text(
                        text = "Trans: ${item.transfer}",
                        Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        color = Color.Green
                    )

                }
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Icon(
                        imageVector = if (item.reverse) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        "",
                        Modifier.weight(1f, false)
                    )
                    Text(text = "Test: ${item.tid}", Modifier.weight(1f))
                    Text(text = "Duration: ${item.duration}", Modifier.weight(1f))
                }

                Row {
                    Text(text = "Server: ${item.server}", Modifier.weight(1f), color = Color.Yellow)
                    Text(text = "Port: ${item.port}", Modifier.weight(1f))
                    Text(text = if (item.upd) "UDP" else "TCP", Modifier.weight(1f))


                }

            }

        }
    }


}
