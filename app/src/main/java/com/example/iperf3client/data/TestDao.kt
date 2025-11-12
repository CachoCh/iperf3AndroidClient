package com.example.iperf3client.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TestDao {
    @Insert
    fun insertAll(vararg test: TestUiState)

    @Update
    fun updateTest(vararg test: TestUiState)

    @Delete
    fun deleteTest(test: TestUiState)

    @Query("SELECT * FROM TestUiState WHERE udp = :protocol AND reverse = :direction ORDER BY tid DESC")
    fun getAll(protocol: Boolean, direction: Boolean): List<TestUiState>

    @Query("SELECT COUNT(tid) FROM TestUiState")
    fun getTestCount(): Int

    @Query("SELECT * FROM TestUiState WHERE tid = :testID ORDER BY tid DESC")
    fun getTest(testID: Int): TestUiState

    @Query("SELECT * FROM TestUiState ORDER BY tid DESC LIMIT 1")
    fun getLastTest(): TestUiState

}