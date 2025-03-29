package com.example.iperf3client.data

import android.content.Context

class ResultsRepository(application: Context) {

    private var resultsDAO: TestsWithResultsDAO
    private val database = TestDatabase.getInstance(application)

    init {
        resultsDAO = database.resultsDao()
    }


    suspend fun createTestConfig(testConfig: ExecutedTestConfig): Long {
        return resultsDAO.save(testConfig)
    }

    suspend fun updateTestConfigTestConfig(testConfig: ExecutedTestConfig) {
        resultsDAO.update(testConfig)
    }

    suspend fun addResult(
        resultID: Long,
        measurement: String,
        latitude: Double,
        longitude: Double,
        altitude: Double,
        networkType: String
    ) {
        resultsDAO.save(
            ExecutedTestResults(
                resultID.toInt(),
                measurement,
                null,
                latitude,
                longitude,
                altitude,
                networkType
            )
        )
    }

    suspend fun getExecutedTests(): List<ExecutedTestConfig> {
        return resultsDAO.getExecutedTests()
    }

    suspend fun getExecutedTestResults(testID: Int?): List<String> {
        return resultsDAO.getExecutedTestResult(testID)
    }

    fun deleteExecutedTestsWithResults(executedTest: ExecutedTestConfig) {
        resultsDAO.cascadeDeletionsFromUser(executedTest)
    }

    companion object {
        @Volatile
        private var instance: ResultsRepository? = null
        fun getInstance(application: Context) =
            instance ?: synchronized(this) {
                instance ?: ResultsRepository(application).also { instance = it }
            }
    }
}