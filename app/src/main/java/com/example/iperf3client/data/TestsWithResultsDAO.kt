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
    @Query("SELECT * FROM ExecutedTestConfig ORDER BY tid DESC")
    suspend fun getExecutedTests(): List<ExecutedTestConfig>

    @Query("SELECT measurment FROM ExecutedTestResults WHERE tid = :testID ORDER BY tid DESC" )
    suspend fun getExecutedTestResult(testID: Int?): List<String>
    @Delete
    fun cascadeDeletionsFromUser(test: ExecutedTestConfig)

    @Transaction
    @Query("SELECT COUNT(tid) FROM ExecutedTestConfig")
    suspend fun getResultsCount(): Int


}