package com.example.iperf3client.viewmodels


import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iperf3client.data.ExecutedTestConfig
import com.example.iperf3client.data.LocationRepository
import com.example.iperf3client.data.NetworkInfoRepository
import com.example.iperf3client.data.ResultsRepository
import com.example.iperf3client.data.TestDatabase
import com.example.iperf3client.data.TestRepository
import com.example.iperf3client.data.TestUiState
import com.example.iperf3client.utils.IpersOutputParser
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.synaptictools.iperf.IPerf
import com.synaptictools.iperf.IPerfConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TestViewModel(applicationContext: Context, testDB : TestDatabase) : ViewModel() {
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    private val resultBuilder: StringBuilder? = StringBuilder()
    private val repository = TestRepository.getInstance(testDB)
    private val resultsRepository = ResultsRepository.getInstance(applicationContext)
    private val locationRepository = LocationRepository.getInstance(applicationContext)
    private val networkInfoRepository = NetworkInfoRepository.getInstance(applicationContext)
    private val modelProd: CartesianChartModelProducer = CartesianChartModelProducer()

    private var _transferArray = MutableStateFlow(listOf(0F))
    private var _bwArray = MutableStateFlow(listOf(0F))
    private val _uiStateFlow = MutableStateFlow(newTestConfig())
    private val _iPerfRequestResultFlow = MutableStateFlow("")
    private var _isIPerfTestRunningFlow = MutableStateFlow(false)
    private var _testResults = MutableStateFlow(listOf<String>())
    private val _testListFlow = MutableStateFlow(listOf<TestUiState>())
    private val _modelProducer = MutableStateFlow(modelProd)
    private val _senderTransfer = MutableStateFlow("")
    private val _senderBandwidth = MutableStateFlow("")
    private val _receiverTransfer = MutableStateFlow("")
    private val _receiverBandwidth = MutableStateFlow("")


    private val _executedTestsList = MutableStateFlow(listOf<ExecutedTestConfig>())

    var uiState: StateFlow<TestUiState> = _uiStateFlow.asStateFlow()
    var isIPerfTestRunning: StateFlow<Boolean> = _isIPerfTestRunningFlow.asStateFlow()
    var iPerfRequestResult: StateFlow<String> = _iPerfRequestResultFlow.asStateFlow()
    var testList: StateFlow<List<TestUiState>> = _testListFlow.asStateFlow()
    var testResults: StateFlow<List<String>> = _testResults.asStateFlow()
    private val locationFlow = locationRepository.locationFlow
    val transferArray: StateFlow<List<Float>> = _transferArray.asStateFlow()
    val bwArray: StateFlow<List<Float>> = _bwArray.asStateFlow()
    var modelProducer = _modelProducer.asStateFlow()

    var executedTestsList: StateFlow<List<ExecutedTestConfig>> = _executedTestsList.asStateFlow()

    var enderTransfer = _senderTransfer.asStateFlow()
    var senderBandwidth = _senderBandwidth.asStateFlow()
    var receiverTransfer = _receiverTransfer.asStateFlow()
    var receiverBandwidth = _receiverBandwidth.asStateFlow()


    private var filesDir: File = applicationContext.applicationContext.filesDir
    private lateinit var iperfJob: Job

    private var resultID: Long = 0

    fun clearTestScreen(){
        if (!_isIPerfTestRunningFlow.value) {
            _senderTransfer.value = ""
            _senderBandwidth.value = ""
            _receiverTransfer.value = ""
            _receiverBandwidth.value = ""
            _transferArray.value = listOf(0F)
            _bwArray.value = listOf(0F)
            modelProducer(_bwArray.value, _transferArray.value)
        }
    }

    /**
     *
     */
    fun saveUpdateTest(
        server: String,
        port: Int,
        duration: Int,
        interval: Int,
        reverse: Boolean,
    ) {
        _uiStateFlow.value = TestUiState(
            _uiStateFlow.value.tid,
            server,
            port,
            duration,
            interval,
            reverse,
            "",
            true
        )
        viewModelScope.launch(Dispatchers.IO) { //this: CoroutineScope
            if (_uiStateFlow.value.tid == null) {
                _uiStateFlow.value = repository.createTest(_uiStateFlow.value)
            } else {
                repository.updateTest(_uiStateFlow.value)
            }
        }
    }

    fun deleteTest(test: TestUiState) {
        viewModelScope.launch(Dispatchers.IO) { //this: CoroutineScope
            repository.deleteTest(test)
        }
    }

    fun getTests() {
        viewModelScope.launch(Dispatchers.IO) { //this: CoroutineScope
            _testListFlow.value = repository.getTests()
            Log.wtf("CACHO", "TestViewModel:getTests ${_testListFlow.value.size}")

        }
    }

    fun getTest(testID: Int): TestUiState {
        viewModelScope.launch(Dispatchers.IO) { //this: CoroutineScope
            _uiStateFlow.value = repository.getTest(testID)
            Log.wtf("CACHO", "TestViewModel:getTest!!! ${_uiStateFlow.value.tid}")
        }
        return _uiStateFlow.value
    }

    fun runIperfTest(
        server: String,
        port: Int,
        duration: Int,
        interval: Int,
        reverse: Boolean
    ) {
        _transferArray.value = listOf<Float>() //clear graph
        _bwArray.value = listOf<Float>() //clear graph
        _testResults.value = listOf<String>() // clear lazylist from previous results
        _uiStateFlow.value = TestUiState(
            _uiStateFlow.value.tid,
            server,
            port,
            duration,
            interval,
            reverse,
            "",
            _uiStateFlow.value.fav
        )
        val stream = File(filesDir, "iperf3.XXXXXX")
        var config = IPerfConfig(
            hostname = _uiStateFlow.value.server,
            port = _uiStateFlow.value.port,
            stream = stream.path,
            download = _uiStateFlow.value.reverse,
            json = false,
            duration = _uiStateFlow.value.duration,
            interval = _uiStateFlow.value.interval
        )

        _isIPerfTestRunningFlow.update { true }
        saveResultsTestConfig(
            ExecutedTestConfig(
                null,
                config.hostname,
                config.port,
                config.duration,
                config.interval,
                config.download,
                "CHANGE ME",
                getFormattedTime(),
                "0",
                "0"
            )
        )
        iperfJob = viewModelScope.launch {
            doStartRequest(config)
        }
    }

    private fun getFormattedTime(): String {
        val calendar: Calendar = Calendar.getInstance()
        val dateFormatter: SimpleDateFormat =
            SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        return dateFormatter.format(calendar.getTime())
    }

    fun cancelIperfJob() {
        IPerf.deInit()
        _isIPerfTestRunningFlow.update { false }
    }

    private suspend fun doStartRequest(config: IPerfConfig) {
        withContext(Dispatchers.IO) {
            try {
                IPerf.seCallBack {
                    success {
                        _isIPerfTestRunningFlow.update { false }
                        saveMeasurement(
                            resultID,
                            "iPerf request done, running = ${_isIPerfTestRunningFlow.value}"
                        )
                        println("CACHO: iPerf request done, running = ${_isIPerfTestRunningFlow.value}")
                    }
                    update { text ->
                        resultBuilder?.append(text)
                        getCurrentLocation()
                        _isIPerfTestRunningFlow.update { true }
                        _iPerfRequestResultFlow.value = (resultBuilder.toString())
                        _testResults.value = listOf(text ?: "") + _testResults.value

                        saveMeasurement(resultID, text ?: "")
                        //_testResults.value = _testResults.value.toMutableList().apply { add(text?:"") }
                        println("CACHO: update  $text, running = ${_isIPerfTestRunningFlow.value}")

                        try {
                            if (text != null) {
                                buildAndSaveMeasurmment(text, config)
                                getGraphVals(text)
                            }
                        } catch (e: Exception) {
                            println("CACHO: update -> exception   ${e.toString()}")
                        }
                    }
                    error { e ->
                        resultBuilder?.append("\niPerf request failed:\n error: $e")
                        _testResults.value =
                            listOf("iPerf request failed:\n error: $e") + _testResults.value
                        saveMeasurement(resultID, "iPerf request failed:\n error: $e")
                        //_testResults.value = _testResults.value.toMutableList().plus("iPerf request failed:\n error: $e")
                        _isIPerfTestRunningFlow.update { false }
                        println("CACHO:  error $resultBuilder, running = ${_isIPerfTestRunningFlow.value}")
                    }
                }
                IPerf.request(config)
            } catch (e: Exception) {
                println("CACHO: error on doStartRequest() -> ${e.message}")
            }

        }

    }

    /*
                CACHO: update  [ ID] Interval           Transfer     Bandwidth       Retr
                CACHO: update  [ 65]   0.00-10.04  sec   445 MBytes   372 Mbits/sec    0             sender
                CACHO: update  [ 65]   0.00-10.04  sec   443 MBytes   370 Mbits/sec                  receiver
                 */

    private fun buildAndSaveMeasurmment(text: String, config: IPerfConfig) {
        if (text.contains(" receiver") == true || text.contains(" sender") == true) {
            var transfer = ""
            var bandwidth = ""
            if (text.contains(" sender") == true) {
                _senderTransfer.value = IpersOutputParser.getFinalTransferOrBwValues(text, "transfer")
                _senderBandwidth.value = IpersOutputParser.getFinalTransferOrBwValues(text, "bw")
                if (!config.download){
                    transfer = _senderTransfer.value
                    bandwidth = _senderBandwidth.value
                }
            } else  if (text.contains(" receiver") == true){
                _receiverTransfer.value = IpersOutputParser.getFinalTransferOrBwValues(text, "transfer")
                _receiverBandwidth.value = IpersOutputParser.getFinalTransferOrBwValues(text, "bw")
                if (config.download){
                    transfer = _receiverTransfer.value
                    bandwidth = _receiverBandwidth.value
                }
            }

            updateResultsTestConfig(
                ExecutedTestConfig(
                    resultID.toInt(),
                    config.hostname,
                    config.port,
                    config.duration,
                    config.interval,
                    config.download,
                    "CHANGE ME",
                    getFormattedTime(),
                    bandwidth,
                    transfer
                )
            )
        }
    }


    private fun getGraphVals(text: String) {
        if (!text.contains("sender") && !text.contains("receiver")) {
            val tr = listOf(
                IpersOutputParser.getIntermediateTransferOrBwValuesInMBytes(text, "transfer")
            )
            val bw =
                listOf(IpersOutputParser.getIntermediateTransferOrBwValuesInMBytes(text, "bw"))
            _transferArray.value = _transferArray.value + tr
            _bwArray.value = _bwArray.value + bw
            modelProducer(_bwArray.value, _transferArray.value)
        }
    }

    fun resetTestConfig() {
        _uiStateFlow.value = newTestConfig()
    }

    private fun newTestConfig(): TestUiState {
        return TestUiState(null, "111.111.111.111", 1000, 10, 1, true, "CHANGE ME", false)
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    private fun saveResultsTestConfig(testConfig: ExecutedTestConfig) {
        viewModelScope.launch(ioDispatcher) { //this: CoroutineScope
            resultID = resultsRepository.createTestConfig(testConfig)
            System.out.println("CACHO: $resultID")
        }
    }

    private fun updateResultsTestConfig(testConfig: ExecutedTestConfig) {
        viewModelScope.launch(ioDispatcher) { //this: CoroutineScope
            resultsRepository.updateTestConfigTestConfig(testConfig)
        }
    }

    private fun saveMeasurement(resultID: Long, measurment: String) {

        viewModelScope.launch(ioDispatcher) {
            println("CACHO: ${networkInfoRepository.getNetworkType()}")
            println("CACHO: ${locationFlow.value.latitude}  ${locationFlow.value.longitude}  ${locationFlow.value.altitude}")
            resultsRepository.addResult(
                resultID,
                measurment,
                locationFlow.value.latitude,
                locationFlow.value.longitude,
                locationFlow.value.altitude,
                networkInfoRepository.getNetworkType()
            )
        }
    }

    fun getExecutedTests() {
        viewModelScope.launch(ioDispatcher) {
            _executedTestsList.value = resultsRepository.getExecutedTests()
        }
    }

    fun getExecutedTestResults(testID: Int?) {
        _testResults.value = listOf<String>()
        viewModelScope.launch(ioDispatcher) {
            _testResults.value = resultsRepository.getExecutedTestResults(testID)
        }
    }

    fun deleteExecutedTestsWithResults(executedTestId: ExecutedTestConfig) {
        viewModelScope.launch(ioDispatcher) {
            resultsRepository.deleteExecutedTestsWithResults(executedTestId)
            _executedTestsList.value = resultsRepository.getExecutedTests()
        }
    }

    ////////////////////////////////////////////////////////////////////////
    private fun getCurrentLocation() {
        viewModelScope.launch(ioDispatcher) {
            locationRepository.getCurrentLocation()
        }
    }


    private fun modelProducer(value: List<Float>, value1: List<Float>) {
        viewModelScope.launch(defaultDispatcher) {
            _modelProducer.value.runTransaction {
                lineSeries {
                    series(value)
                    series(value1)
                }
            }
        }
    }

}




