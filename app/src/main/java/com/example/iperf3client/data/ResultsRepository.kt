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

    /**
     *  tcp = false
     *  download = true
     */
    suspend fun getExecutedTests(
        tcp: Boolean = true,
        udp: Boolean = true,
        upload: Boolean = true,
        download: Boolean = true
    ): List<ExecutedTestConfig> {
        var _results = mutableListOf<ExecutedTestConfig>()
        if (tcp && upload) {
            _results += resultsDAO.getExecutedTests(false, false)
        }
        if (tcp && download) {
            _results += resultsDAO.getExecutedTests(false, true)
        }
        if (udp && upload) {
            _results += resultsDAO.getExecutedTests(true, false)
        }
        if (udp && download) {
            _results += resultsDAO.getExecutedTests(true, true)
        }

        return _results.sortedByDescending { it.tid }
    }

    suspend fun getExecutedTestResults(testID: Int?): List<MeasLatLon> {
        return resultsDAO.getExecutedTestResult(testID)
    }

    suspend fun getResultsCount(): Int {
        return resultsDAO.getResultsCount()
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