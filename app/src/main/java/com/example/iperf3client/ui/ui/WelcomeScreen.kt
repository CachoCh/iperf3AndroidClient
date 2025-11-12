package com.example.iperf3client.ui.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iperf3client.R
import com.example.iperf3client.ui.ui.WelcomeScreen
import com.example.iperf3client.viewmodels.TestViewModel

@Composable
fun WelcomeScreen(
    onNewTestButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
    testViewModel: TestViewModel
) {
    BackHandler {
        testViewModel.getTestCount()
        testViewModel.getResultsCount()
    }

    val resultsCount by testViewModel.resultsCount.collectAsState()
    val testsCount by testViewModel.testCount.collectAsState()

    var resultsNum by rememberSaveable { mutableIntStateOf(resultsCount) }
    var testsNum by rememberSaveable { mutableIntStateOf(testsCount) }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),


        contentAlignment = Alignment.TopCenter
    ) {
        val screenHeight = this.maxHeight
        val screenWidth = this.maxWidth

        val upperThreeQuartersHeight = screenHeight * 0.75f

        val iconCenterY = upperThreeQuartersHeight / 2

        val iconSize = 300.dp

        val yOffset = iconCenterY - (iconSize / 2)
        val textYOffset = screenHeight / 6
        val buttonYOffset = screenHeight / 2

        Text(
            text = "IPerf3 Android client",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            // --- NEW WAY ---
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = textYOffset)
                .fillMaxWidth()
                .padding(horizontal = 15.dp)

        )

        Image(
            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
            contentDescription = "App Icon",
            modifier = Modifier
                .size(iconSize)
                // Apply the calculated vertical offset.
                .offset(y = yOffset)
        )

        Button(onClick = onNewTestButtonClicked,modifier = Modifier
            .align(Alignment.TopCenter)
            .offset(y = buttonYOffset)
            .fillMaxWidth()
            .padding(horizontal = screenWidth / 4, vertical = 32.dp)) {
            Text("New Test")
        }

        Text(
            text = "Saved Tests: $testsNum",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (screenHeight* .8f) )
                .fillMaxWidth()
                .padding(horizontal = 32.dp)

        )
        Text(
            text = "Saved Results: $resultsNum",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = screenHeight * .9f)
                .fillMaxWidth()
                .padding(horizontal = 32.dp)

        )
    }
}
