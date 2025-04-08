package com.example.iperf3client

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import app.cash.turbine.test
import com.example.iperf3client.data.TestRepository
import com.example.iperf3client.data.TestUiState
import com.example.iperf3client.viewmodels.TestViewModel
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var testViewModel: TestViewModel
    private val testRepository: TestRepository = mockk(relaxed = true)


    @Before
    fun setup() {
        val applicationContext: Context = InstrumentationRegistry.getInstrumentation().context
        // Initialize TestViewModel with the mocked dependencies
        testViewModel = TestViewModel(applicationContext)
    }


    /*
     * Test using turbine
     */
    @Test
    fun `saveUpdateTest should create a new test when tid is null`() = runTest {
        // Arrange

        // Act
        testViewModel.saveUpdateTest(
            server = "192.168.1.1",
            port = 8080,
            duration = 10,
            interval = 1,
            reverse = true
        )
        // Wait for the state change and verify the repository method was called
        testViewModel.uiState.test {
            // Collect the latest value
            val uiState = awaitItem()

            // Verify that the UI state was updated with the correct values
            assertNotNull(uiState)
            assertEquals("192.168.1.1", uiState.server)
            assertEquals(8080, uiState.port)
            assertEquals(10, uiState.duration)
            assertEquals(1, uiState.interval)
            assertEquals(true, uiState.reverse)
            assertNull(uiState.tid) // Tid should still be null

        }
    }

    /**
     * Test using turbine
     */
    @Test
    fun `saveUpdateTest should update an existing test when tid is not null`() = runTest {
        // Arrange
        testViewModel.uiState.value.tid = 99
        // Act

        testViewModel.saveUpdateTest(
            server = "192.168.1.1",
            port = 443,
            duration = 10,
            interval = 1,
            reverse = false
        )
        // Wait for the state change and verify the repository method was called
        testViewModel.uiState.test {
            // Collect the latest value
            val uiState = awaitItem()

            // Verify that the UI state was updated with the correct values
            assertNotNull(uiState)
            assertEquals("192.168.1.1", uiState.server)
            assertEquals(443, uiState.port)
            assertEquals(10, uiState.duration)
            assertEquals(1, uiState.interval)
            assertEquals(false, uiState.reverse)
            assertNotNull(uiState.tid)// Tid should not be null
            assertEquals(99,uiState.tid) // Tid should be 99
        }
    }

    @Test
    fun `saveUpdateTest should call updateTest if tid is not null`() = runTest {
        // Arrange
        val existingTest = TestUiState(
            tid = 1,
            server = "192.168.1.1",
            port = 8080,
            duration = 10,
            interval = 1,
            reverse = true,
            fav = false,
            output = ""
        )
        val updatedTest = TestUiState(
            tid = 1,
            server = "192.168.1.1",
            port = 8080,
            duration = 20,
            interval = 2,
            reverse = false,
            fav = false,
            output = ""
        )


        // Act
        testViewModel.saveUpdateTest(
            server = "192.168.1.1",
            port = 8080,
            duration = 20,
            interval = 2,
            reverse = false
        )

        // Verify that updateTest was called
        coVerify { testRepository.updateTest(updatedTest) }
    }
}
