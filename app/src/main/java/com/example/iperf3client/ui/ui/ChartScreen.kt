package com.example.iperf3client.ui.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.room.util.TableInfo
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.core.cartesian.AutoScrollCondition
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer

@Composable
fun ChartScreen(modelProducer: CartesianChartModelProducer, testResults: List<String>) {
    Column() {
        CartesianChartHost(
            modifier = Modifier,
            chart =
                rememberCartesianChart(
                    rememberLineCartesianLayer(),
                    /* listOf(
                     rememberLineSpec(
                         DynamicShader.color(Color.Green)
                     ),
                     rememberLineSpec(DynamicShader.color(Color.Blue))
                 )
             ),*/
                    startAxis = VerticalAxis.rememberStart(label = rememberAxisLabelComponent(color = Color.Green)),
                    //legend = rememberLegend(),
                    /*endAxis = rememberEndAxis(
                    label = rememberAxisLabelComponent(color = Color.Blue)
                ),*/
                    bottomAxis = HorizontalAxis.rememberBottom(),

                    /*decorations =
                listOf(
                    rememberHorizontalLine(
                        y = { 2f },
                        line = rememberLineComponent(color = Color.Red, thickness = 2.dp),
                        labelComponent =
                        rememberTextComponent(Color.Red, padding = Dimensions.of(horizontal = 8.dp)),
                    ),
                    rememberHorizontalLine(
                        y = { 3f },
                        line = rememberLineComponent(color = Color.Yellow, thickness = 2.dp),
                        labelComponent =
                        rememberTextComponent(Color.Yellow, padding = Dimensions.of(horizontal = 8.dp)),
                    )
                ),*/
                ),
            modelProducer = modelProducer,
            scrollState = rememberVicoScrollState(
                scrollEnabled = true,
                initialScroll = Scroll.Absolute.End,
                autoScroll = Scroll.Absolute.End,
                autoScrollCondition = AutoScrollCondition.OnModelGrowth
            ),
            //runInitialAnimation = true
        )
    }
    if(testResults.isNotEmpty())  ResultsTableScreen(testResults)
}