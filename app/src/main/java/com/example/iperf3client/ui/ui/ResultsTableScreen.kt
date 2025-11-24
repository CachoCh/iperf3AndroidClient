package com.example.iperf3client.ui.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ResultsTableScreen (testResults: List<String>) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically){

        Text("Interval")
        Text("Transfer")
        Text("Bandwidth")
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        items(testResults) { result ->
            val rowItem = parseLine(result)
            if (rowItem?.transfer != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    //Text("ID: ${result.id}", color = Color.White)
                    Text(" ${rowItem?.start}-${rowItem?.end}s", color = Color.White)
                    Text("${rowItem?.transfer} ${rowItem?.transferUnit}", color = Color.White)
                    Text("${rowItem?.bitsPerSec} ${rowItem?.bitsPerSecUnit}", color = Color.White)
                }
            }
        }
    }
    
    
}

data class ParsedLine(
    val id: Int,
    val start: Double,
    val end: Double,
    val transfer: Double,
    val transferUnit: String,
    val bitsPerSec: Double,
    val bitsPerSecUnit: String
)

fun parseLine(line: String): ParsedLine? {
    // \[139\]  10\.01-11\.01  sec\s 5\.53 [A-Za-z0-9]+  46\.4 Mbits/sec
    val regex = """\[\s*(\d+)]\s+(\d+\.\d+)-(\d+\.\d+)\s+sec\s+(\d+\.\d+)\s+([A-Za-z0-9]+)\s+(\d+\.\d+)\s([A-Za-z]+/[A-Za-z]+)""".toRegex()

    val matchResult = regex.find(line)

    return matchResult?.destructured?.let { (id, start, end, transfer, transferUnit, bitsPerSec, bitsPerSecUnit) ->
        ParsedLine(
            id = id.toInt(),
            start = start.toDouble(),
            end = end.toDouble(),
            transfer = transfer.toDouble(),
            transferUnit = transferUnit.toString(),
            bitsPerSec = bitsPerSec.toDouble(),
            bitsPerSecUnit = bitsPerSecUnit.toString()
        )
    }
}
