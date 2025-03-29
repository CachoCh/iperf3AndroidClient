package com.example.iperf3client.data

import android.content.Context


class TestRepository(application: Context) {

    private var testDao: TestDao
    private val database = TestDatabase.getInstance(application)

    init {
        testDao = database.testDao()
    }
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

    fun getTest(testID: Int) : TestUiState {
        return testDao.getTest(testID)
    }

    private fun getLastTests() : TestUiState {
        return testDao.getLastTest()
    }

    companion object {
        @Volatile private var instance: TestRepository? = null
        fun getInstance(application: Context) =
            instance ?: synchronized(this) {
                instance ?: TestRepository(application).also { instance = it }
            }
    }
}