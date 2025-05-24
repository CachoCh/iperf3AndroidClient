package com.example.iperf3client.ui.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iperf3client.viewmodels.TestViewModel

@Composable
fun IperfRawOutputScreen(testViewModel: TestViewModel) {
    val lines by testViewModel.testResults.collectAsState()
    val scrollState = rememberLazyListState()

    // Scroll to bottom when new lines added
/*    LaunchedEffect(lines.size) {
        scrollState.animateScrollToItem(lines.size)
    }*/

    LazyColumn(
        state = scrollState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(12.dp)
    ) {
        items(lines) { line ->
            Text(
                text = line,
                //text = line
                color = Color(0xFFD4D4D4),
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp
            )
        }
    }
}


