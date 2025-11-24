package com.example.iperf3client.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class TestUiState(
    @PrimaryKey(autoGenerate = true) var tid: Int?,
    @ColumnInfo(name = "server") var server: String,
    @ColumnInfo(name = "port") var port: Int,
    @ColumnInfo(name = "duration") var duration: Int,
    @ColumnInfo(name = "interval") var interval: Int,
    @ColumnInfo(name = "reverse") var reverse: Boolean,
    @ColumnInfo(name = "output") var output: String,
    @ColumnInfo(name = "favourite") var fav: Boolean,
    @ColumnInfo(name = "udp", defaultValue = "false") var udp: Boolean
)