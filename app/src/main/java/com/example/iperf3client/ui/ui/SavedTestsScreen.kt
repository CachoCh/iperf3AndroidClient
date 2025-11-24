package com.example.iperf3client.ui

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.iperf3client.data.TestUiState
import com.example.iperf3client.viewmodels.TestViewModel

@Composable
fun SavedTestsScreen(
    testViewModel: TestViewModel,
    modifier: Modifier = Modifier,
    onCancelButtonClicked: () -> Unit,
    onItemClick: (id: Int?) -> Unit,
    onRunTestClicked: (
        server: String?,
        port: Int?,
        duration: Int?,
        interval: Int?,
        reverse: Boolean?,
        udp: Boolean
        ) -> Unit
) {
    BackHandler {
        testViewModel.getTests()
    }
    val testListState by testViewModel.testList.collectAsState()
    Log.wtf("CACHO", "SavedTestsScreen:testListState.size: ${testListState.size}")
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        LazyColumn(
            modifier = Modifier
                .wrapContentHeight()
                .heightIn(max = 1000.dp)
        ) {
            items(testListState) { item ->
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
                        Column(modifier = Modifier.weight(4f)) {
                            Text(text = "Test: ${item.tid.toString() ?: "New"}")
                            Text(text = "${item.server}: ${item.port}")
                            Row {
                                Text(text = if (item.reverse) "Download" else "Upload", Modifier.weight(1f))
                                Text(text = if (item.udp) "UDP" else "TCP", Modifier.weight(1f))
                            }
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            IconButton(
                                onClick = {
                                    onRunTestClicked(
                                        item.server,
                                        item.port,
                                        item.duration,
                                        item.interval,
                                        item.reverse,
                                        item.udp
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null
                                )
                            }

                        }
                    }


                }

            }
        }
    }

}