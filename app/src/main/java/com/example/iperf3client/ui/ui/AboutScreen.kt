package com.example.iperf3client.ui.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iperf3client.R
import com.example.iperf3client.utils.Utils

@Composable
fun AboutScreen(context: Context) {
    val VERSION_NAME = Utils.getVersionName(context)
    R.string.website

    Column {

        Text(
            text =  stringResource(R.string.app_name),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 0.5.sp
            )
        )
        Text(text =  "v${VERSION_NAME}")
        Spacer(modifier = Modifier.size(20.dp))
        Text(
            text =  stringResource(R.string.website),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 0.5.sp
            )
        )
        Text(text =  "https://apt.izzysoft.de/fdroid/index/apk/com.example.iperf3client")

        Spacer(modifier = Modifier.size(20.dp))
        Text(
            text =  stringResource(R.string.license),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 0.5.sp
            )
        )
        Text(text =  stringResource(R.string.license_v))

        Spacer(modifier = Modifier.size(20.dp))
        HorizontalDivider(Modifier, thickness = 1.dp, color = Color.White)
        Spacer(modifier = Modifier.size(20.dp))
        Text(
            text =  stringResource(R.string.acknowledgments),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 0.5.sp
            )
        )
        Spacer(modifier = Modifier.size(20.dp))
        Text(text =  " - The main authors of iPerf3 are (in alphabetical order): Jon Dugan, Seth Elliott, Bruce A. Mah, Jeff Poskanzer, Kaustubh Prabhu. Additional code contributions have come from (also in alphabetical order): Mark Ashley, Aaron Brown, Aeneas Jaißle, Susant Sahani, Bruce Simpson, Brian Tierney.\n" +
                "\n" +
                " - Khandker Mahmudur Rahman (mahmudur85) iPerf3 implementation for Android iperf-jni\n")
    }

}

