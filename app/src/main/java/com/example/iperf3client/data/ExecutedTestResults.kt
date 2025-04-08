package com.example.iperf3client.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity()
data class ExecutedTestConfig(
    @PrimaryKey(autoGenerate = true) var tid: Int?,
    @ColumnInfo(name = "server") var server: String,
    @ColumnInfo(name = "port") var port: Int,
    @ColumnInfo(name = "duration") var duration: Int,
    @ColumnInfo(name = "interval") var interval: Int,
    @ColumnInfo(name = "reverse") var reverse: Boolean,
    @ColumnInfo(name = "output") var output: String,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "bandwidth") var bandwidth: String,
    @ColumnInfo(name = "transfer") var transfer: String
)

@Entity(
    foreignKeys = arrayOf(
        ForeignKey(
            entity = ExecutedTestConfig::class,
            parentColumns = arrayOf("tid"),
            childColumns = arrayOf("tid"),
            onDelete = ForeignKey.CASCADE
        )
    )
)
data class ExecutedTestResults(
    @ColumnInfo(index = true) var tid: Int,
    @ColumnInfo() var measurment: String,
    @PrimaryKey(autoGenerate = true) var resultID: Int?,
    @ColumnInfo var latitude: Double,
    @ColumnInfo var longitude: Double,
    @ColumnInfo var altitude: Double,
    @ColumnInfo val networkType: String
)
