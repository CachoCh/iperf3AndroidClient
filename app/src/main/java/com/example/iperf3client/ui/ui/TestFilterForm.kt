package com.example.iperf3client.ui.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import com.example.iperf3client.viewmodels.TestViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestFilterForm(
    showSheet: Boolean,
    onDismiss: () -> Unit,
    testViewModel: TestViewModel
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch { sheetState.hide() }
                    .invokeOnCompletion { onDismiss() }
            },
            sheetState = sheetState
        ) {
            NetworkOptionsForm(onDismiss, testViewModel)

        }
    }
}

@Composable
fun NetworkOptionsForm(onDismiss: () -> Unit, testViewModel: TestViewModel) {
    val filterState by testViewModel.filterState.collectAsState()

    // Checkbox states
    var upload by rememberSaveable { mutableStateOf(filterState.upload) }
    var download by rememberSaveable { mutableStateOf(filterState.download) }
    var tcp by rememberSaveable { mutableStateOf(filterState.tcp) }
    var udp by rememberSaveable { mutableStateOf(filterState.udp) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Test/Results Filter",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider()

            CheckboxRow("Upload", upload, onCheckedChange = {upload = it})
            CheckboxRow("Download", download, onCheckedChange = {download = it})
            CheckboxRow("TCP", tcp, onCheckedChange = {tcp = it})
            CheckboxRow("UDP", udp, onCheckedChange = {udp = it})

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    testViewModel.saveFilterState(tcp,udp,upload,download)
                    testViewModel.getExecutedTests()
                    testViewModel.getTests()
                    onDismiss()
                },
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Apply")
            }
        }
    }
}

@Composable
fun CheckboxRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 16.sp)
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
