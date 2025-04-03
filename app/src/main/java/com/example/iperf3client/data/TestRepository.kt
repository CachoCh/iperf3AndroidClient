package com.example.iperf3client.data

import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.encoding.Encoder


class TestRepository(database: TestDatabase) {

    var testDao: TestDao = database.testDao()
    suspend fun createTest(test : TestUiState) : TestUiState {
        testDao.insertAll(test)
        return getLastTests()//TODO: check if there is a better way to do this
    }

    suspend fun updateTest(test : TestUiState) {
        testDao.updateTest(test)
    }

    suspend fun deleteTest(test : TestUiState) {
        testDao.deleteTest(test)
    }

    suspend fun getTests(): List<TestUiState> {
        return testDao.getAll()
    }
    suspend fun getTestCount(): Int {
        return testDao.getTestCount()
    }

    fun getTest(testID: Int) : TestUiState {
        return testDao.getTest(testID)
    }

    private fun getLastTests() : TestUiState {
        return testDao.getLastTest()
    }

    companion object {
        @Volatile private var instance: TestRepository? = null
        fun getInstance(database: TestDatabase) =
            instance ?: synchronized(this) {
                instance ?: TestRepository(database).also { instance = it }
            }
    }
}