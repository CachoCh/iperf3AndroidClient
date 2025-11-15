package com.example.iperf3client.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface TestsWithResultsDAO {
    @Insert
    suspend fun save(testConfig: ExecutedTestConfig) : Long

    @Update
    suspend fun update(testConfig: ExecutedTestConfig)

    @Insert
    suspend fun save(vararg result: ExecutedTestResults)

    @Transaction
    @Query("SELECT * FROM ExecutedTestConfig")
    suspend fun getAllTestWithResults(): List<TestWithResults>
    @Transaction
    @Query("SELECT * FROM ExecutedTestConfig WHERE udp = :protocol AND reverse = :direction ORDER BY tid DESC")
    suspend fun getExecutedTests(protocol: Boolean, direction: Boolean): List<ExecutedTestConfig>

    @Transaction
    @Query("SELECT * FROM ExecutedTestConfig ORDER BY tid DESC")
    suspend fun getExecutedTests(): List<ExecutedTestConfig>

    @Query("SELECT measurment, latitude, longitude  FROM ExecutedTestResults WHERE tid = :testID ORDER BY tid DESC" )
    suspend fun getExecutedTestResult(testID: Int?): List<MeasLatLon>
    @Delete
    fun cascadeDeletionsFromUser(test: ExecutedTestConfig)

    @Transaction
    @Query("SELECT COUNT(tid) FROM ExecutedTestConfig")
    suspend fun getResultsCount(): Int


}

data class MeasLatLon(
    val measurment: String,
    val latitude: Double,
    val longitude: Double
)