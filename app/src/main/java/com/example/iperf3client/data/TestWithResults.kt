package com.example.iperf3client.data

import androidx.room.Embedded
import androidx.room.Relation

data class TestWithResults(
    @Embedded
    val executedTestConfig: ExecutedTestConfig,
    @Relation(
        parentColumn = "tid",
        entityColumn = "tid")
    val executedTestResults: List<ExecutedTestResults>
)