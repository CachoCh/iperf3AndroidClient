package com.example.iperf3client.ui

//import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
//import com.patrykandpatrick.vico.compose.common.of
//import com.patrykandpatrick.vico.compose.common.shader.color
//import com.patrykandpatrick.vico.core.common.Dimensions
//common.shader.DynamicShader
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.example.iperf3client.ui.ui.ChartScreen
import com.example.iperf3client.ui.ui.IperfRawOutputScreen
import com.example.iperf3client.ui.ui.ResultsListScreen
import com.example.iperf3client.ui.ui.ResultsTableScreen
import com.example.iperf3client.viewmodels.TestViewModel

@Composable
fun NewTestScreen(
    testViewModel: TestViewModel,
    modifier: Modifier = Modifier,
    onCancelButtonClicked: () -> Unit
) {

    Log.wtf("CACHO:", "RunningTestScreen START")

    val testResults by testViewModel.testResults.collectAsState()
    val testUiState by testViewModel.uiState.collectAsState()
    val isTestRunningState by testViewModel.isIPerfTestRunning.collectAsState()
    val modelProducer by testViewModel.modelProducer.collectAsState()
    val senderTransfer by testViewModel.enderTransfer.collectAsState()
    val senderBandwidth by testViewModel.senderBandwidth.collectAsState()
    val receiverTransfer by testViewModel.receiverTransfer.collectAsState()
    val receiverBandwidth by testViewModel.receiverBandwidth.collectAsState()

    var server by rememberSaveable { mutableStateOf(testUiState.server) }
    var port by rememberSaveable { mutableIntStateOf(testUiState.port) }
    var duration by rememberSaveable { mutableIntStateOf(testUiState.duration) }
    var interval by rememberSaveable { mutableIntStateOf(testUiState.interval) }
    var reverse by rememberSaveable { mutableStateOf(testUiState.reverse) }
    var udp by rememberSaveable { mutableStateOf(testUiState.udp) }
    var isSaved by rememberSaveable { mutableStateOf(false) }

    var context = LocalContext.current

    Column(
        modifier = modifier.padding(horizontal = 2.dp, vertical = 10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Test: ${testUiState.tid ?: "New"}", modifier = Modifier
                .weight(7f))
            IconButton(modifier = Modifier
                .weight(1f),
                enabled = testUiState.tid!=null,
                onClick = {
                    testViewModel.deleteTest(testUiState)
                    testViewModel.getTests()
                    //testUiState.tid=null
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
            }
        }
        Row(
            //modifier = Modifier.fillMaxWidth().padding(5.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(3f),
                value = server,
                maxLines = 1,
                onValueChange = {
                    server = it
                    isSaved = false
                },
                label = { Text("Server:") }
            )

            Spacer(Modifier.width(10.dp))

            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = port.toString(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = {
                    if (it.isDigitsOnly() and (it.length < 6) ) {
                        if (it=="") port = 0
                        else port = it.toInt()
                    }

                    isSaved = false
                },
                label = { Text("Port:") },
            )
        }

        Row(
            //modifier = Modifier.fillMaxWidth().padding(5.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {


            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = duration.toString(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = {
                    if (it.isDigitsOnly() and (it.length <= 4) and (it.isNotEmpty()))
                        duration = it.toInt()

                    isSaved = false
                },
                label = { Text("Duration:") },
            )

            Spacer(Modifier.width(10.dp))
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = interval.toString(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = {
                    if (it.isDigitsOnly() and (it.length <= 2) and (it.isNotEmpty()))
                        interval = it.toInt()

                    isSaved = false
                },
                label = { Text("Interval:") },
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Reverse")
            Checkbox(
                checked = reverse,
                onCheckedChange = {
                    reverse = it
                    isSaved = false
                }
            )

            Text("UDP")
            Checkbox(
                checked = udp,
                onCheckedChange = {
                    udp = it
                    isSaved = false
                }
            )

        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Button(
                enabled = !isTestRunningState,
                onClick = {
                    testViewModel.runIperfTest(
                        server,
                        port,
                        duration,
                        interval,
                        reverse,
                        udp,
                        context
                    )
                }) {
                Text("Run")
            }

            Button(
                enabled = !isSaved,
                onClick = {
                    if (port >= 0) {
                        testViewModel.saveUpdateTest(
                            server,
                            port,
                            duration,
                            interval,
                            reverse,
                            udp
                        )
                        isSaved = true
                    }
                }) {
                Text("Save")
            }

            Button(
                enabled = isTestRunningState,
                onClick = {
                    testViewModel.cancelIperfJob()

                }
            ) {
                Text("Stop")
            }
        }



        Spacer(Modifier.height(10.dp))

        Row() {
            Text("Sender:", modifier = Modifier.weight(1f))
            Text("Transfer \n$senderTransfer", modifier = Modifier.weight(2f))
            Text("Bandwidth \n$senderBandwidth", modifier = Modifier.weight(2f))
        }
        Row() {
            Text("Receiver:", modifier = Modifier.weight(1f))
            Text("Transfer \n$receiverTransfer", modifier = Modifier.weight(2f))
            Text("Bandwidth \n$receiverBandwidth", modifier = Modifier.weight(2f))
        }

        Spacer(Modifier.height(10.dp))

        ResultsTableScreen(testResults)
    }
}


