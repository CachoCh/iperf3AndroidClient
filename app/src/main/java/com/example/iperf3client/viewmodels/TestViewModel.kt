package com.example.iperf3client.viewmodels


import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iperf3client.data.ExecutedTestConfig
import com.example.iperf3client.data.LocationRepository
import com.example.iperf3client.data.MeasLatLon
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
import org.osmdroid.util.GeoPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.collections.last

class TestViewModel(applicationContext: Context, testDB: TestDatabase) : ViewModel() {
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    private val resultBuilder: StringBuilder? = StringBuilder()
    private val repository = TestRepository.getInstance(testDB)
    private val resultsRepository = ResultsRepository.getInstance(applicationContext)
    private val locationRepository = LocationRepository.getInstance(applicationContext)
    private val networkInfoRepository = NetworkInfoRepository.getInstance(applicationContext)
    private val modelProd: CartesianChartModelProducer = CartesianChartModelProducer()

    private var _mapMarkers = MutableStateFlow(listOf(SpeedMapMarker(GeoPoint(0.0, 0.0), 0F)))
    private var _transferArray = MutableStateFlow(listOf(0F))
    private var _bwArray = MutableStateFlow(listOf(0F))
    private val _uiStateFlow = MutableStateFlow(newTestConfig())
    private val _filterStateFlow = MutableStateFlow(FilterState())
    private val _iPerfRequestResultFlow = MutableStateFlow("")
    private var _isIPerfTestRunningFlow = MutableStateFlow(false)
    private var _testResults = MutableStateFlow(listOf<String>())
    private var _loadedTestResults = MutableStateFlow(listOf<MeasLatLon>())
    private val _testListFlow = MutableStateFlow(listOf<TestUiState>())
    private val _testCountFlow = MutableStateFlow(0)
    private val _modelProducer = MutableStateFlow(modelProd)
    private val _senderTransfer = MutableStateFlow("")
    private val _senderBandwidth = MutableStateFlow("")
    private val _receiverTransfer = MutableStateFlow("")
    private val _receiverBandwidth = MutableStateFlow("")

    private val _resultsCount = MutableStateFlow(0)

    private val _executedTestsList = MutableStateFlow(listOf<ExecutedTestConfig>())

    var uiState: StateFlow<TestUiState> = _uiStateFlow.asStateFlow()
    var filterState: StateFlow<FilterState> = _filterStateFlow.asStateFlow()
    var isIPerfTestRunning: StateFlow<Boolean> = _isIPerfTestRunningFlow.asStateFlow()
    var iPerfRequestResult: StateFlow<String> = _iPerfRequestResultFlow.asStateFlow()
    var testList: StateFlow<List<TestUiState>> = _testListFlow.asStateFlow()
    var testCount: StateFlow<Int> = _testCountFlow.asStateFlow()
    var testResults: StateFlow<List<String>> = _testResults.asStateFlow()
    val locationFlow = locationRepository.locationFlow
    val transferArray: StateFlow<List<Float>> = _transferArray.asStateFlow()
    val bwArray: StateFlow<List<Float>> = _bwArray.asStateFlow()
    var modelProducer = _modelProducer.asStateFlow()
    var executedTestsList: StateFlow<List<ExecutedTestConfig>> = _executedTestsList.asStateFlow()

    var enderTransfer = _senderTransfer.asStateFlow()
    var senderBandwidth = _senderBandwidth.asStateFlow()
    var receiverTransfer = _receiverTransfer.asStateFlow()
    var receiverBandwidth = _receiverBandwidth.asStateFlow()
    var resultsCount = _resultsCount.asStateFlow()
    var mapMarker = _mapMarkers.asStateFlow()

    private var filesDir: File = applicationContext.applicationContext.filesDir
    private lateinit var iperfJob: Job

    private var resultID: Long = 0

    fun clearTestScreen() {
        if (!_isIPerfTestRunningFlow.value) {
            _senderTransfer.value = ""
            _senderBandwidth.value = ""
            _receiverTransfer.value = ""
            _receiverBandwidth.value = ""
            _transferArray.value = listOf(0F)
            _bwArray.value = listOf(0F)
            modelProducer(_bwArray.value, _transferArray.value)
            locationRepository.tracker.stopLocationUpdates()
        }
    }

    fun saveFilterState(tcp: Boolean, udp: Boolean, upload: Boolean, download: Boolean) {
        _filterStateFlow.value = FilterState("", tcp, udp, upload, download)
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
        udp: Boolean
    ) {
        _uiStateFlow.value = TestUiState(
            _uiStateFlow.value.tid,
            server,
            port,
            duration,
            interval,
            reverse,
            "",
            true,
            udp
        )
        viewModelScope.launch(Dispatchers.IO) { //this: CoroutineScope
            if (_uiStateFlow.value.tid == null) {
                _uiStateFlow.value = repository.createTest(_uiStateFlow.value)
            } else {
                repository.updateTest(_uiStateFlow.value)
            }
        }
    }


    fun saveNewTest(
        server: String,
        port: Int,
        duration: Int,
        interval: Int,
        reverse: Boolean,
        udp: Boolean
    ) {
        val test = TestUiState(
            null,
            server,
            port,
            duration,
            interval,
            reverse,
            "",
            true,
            udp
        )
        viewModelScope.launch(Dispatchers.IO) { //this: CoroutineScope
            _uiStateFlow.value = repository.createTest(test)
        }
    }

    fun deleteTest(test: TestUiState) {
        viewModelScope.launch(Dispatchers.IO) { //this: CoroutineScope
            repository.deleteTest(test)
        }
    }

    fun getTests(
        server: String = "",
        tcp: Boolean = true,
        udp: Boolean = true,
        upload: Boolean = true,
        download: Boolean = true
    ) {
        _testListFlow.value = emptyList()
        viewModelScope.launch(Dispatchers.IO) { //this: CoroutineScope
            _testListFlow.value = _testListFlow.value + repository.getTests(
                _filterStateFlow.value.tcp,
                _filterStateFlow.value.udp,
                _filterStateFlow.value.upload,
                _filterStateFlow.value.download
            )
        }
    }

    fun getTestCount() {
        viewModelScope.launch(Dispatchers.IO) { //this: CoroutineScope
            _testCountFlow.value = repository.getTestCount()
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
        reverse: Boolean,
        udp: Boolean,
        context: Context
    ) {

        _mapMarkers.value = listOf(SpeedMapMarker(GeoPoint(0.0, 0.0), 0F))
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
            _uiStateFlow.value.fav,
            udp
        )
        val stream = File(filesDir, "iperf3.XXXXXX")
        var config = IPerfConfig(
            hostname = _uiStateFlow.value.server,
            port = _uiStateFlow.value.port,
            stream = stream.path,
            download = _uiStateFlow.value.reverse,
            json = false,
            duration = _uiStateFlow.value.duration,
            interval = _uiStateFlow.value.interval,
            useUDP = udp
        )

        _isIPerfTestRunningFlow.update { true }
        locationRepository.tracker.startLocationUpdates()
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
                "0",
                config.useUDP
            )
        )
        iperfJob = viewModelScope.launch {
            doStartRequest(config, context)
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
        locationRepository.tracker.stopLocationUpdates()
    }

    private suspend fun doStartRequest(config: IPerfConfig, context: Context) {
        withContext(Dispatchers.IO) {
            try {
                IPerf.seCallBack {
                    success {
                        _isIPerfTestRunningFlow.update { false }
                        locationRepository.tracker.stopLocationUpdates()
                        saveMeasurement(
                            resultID,
                            "iPerf request done, running = ${_isIPerfTestRunningFlow.value}"
                        )
                        println("CACHO: iPerf request done, running = ${_isIPerfTestRunningFlow.value}")
                    }
                    update { text ->
                        resultBuilder?.append(text)
                        _isIPerfTestRunningFlow.update { true }
                        locationRepository.tracker.startLocationUpdates()
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
                        locationRepository.tracker.stopLocationUpdates()
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
                _senderTransfer.value =
                    IpersOutputParser.getFinalTransferOrBwValues(text, "transfer")
                _senderBandwidth.value = IpersOutputParser.getFinalTransferOrBwValues(text, "bw")
                if (!config.download) {
                    transfer = _senderTransfer.value
                    bandwidth = _senderBandwidth.value
                }
            } else if (text.contains(" receiver") == true) {
                _receiverTransfer.value =
                    IpersOutputParser.getFinalTransferOrBwValues(text, "transfer")
                _receiverBandwidth.value = IpersOutputParser.getFinalTransferOrBwValues(text, "bw")
                if (config.download) {
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
                    transfer,
                    config.useUDP
                )
            )
        }
    }


    /**
     * returns bw list //TODO: code better
     */
    private fun parseBwThr(iperfResultLine: String): List<Float> {
        try {
            if (!iperfResultLine.contains("sender") && !iperfResultLine.contains("receiver")) {
                val tr = listOf(
                    IpersOutputParser.getIntermediateTransferOrBwValuesInMBytes(
                        iperfResultLine,
                        "transfer"
                    )
                )
                val bw =
                    listOf(
                        IpersOutputParser.getIntermediateTransferOrBwValuesInMBytes(
                            iperfResultLine,
                            "bw"
                        )
                    )
                _transferArray.value = _transferArray.value + tr
                _bwArray.value = _bwArray.value + bw
                Log.wtf("CACHO:", " bw= $bw")
                return bw
            }
        } catch (e: IllegalStateException) {
            throw e
        }
        return listOf(0f)
    }

    private fun getGraphVals(iperfResultLine: String) {
        val bw = parseBwThr(iperfResultLine)
        modelProducer(_bwArray.value, _transferArray.value) // build graph
        if (bw.isNotEmpty()) { // add point to map
            _mapMarkers.value =
                _mapMarkers.value + listOf(
                    SpeedMapMarker(
                        GeoPoint(locationFlow.value),
                        bw.last()
                    )
                )
        }
    }

    private fun clearResultsLists(){
        _loadedTestResults.value = emptyList()
        _transferArray.value = emptyList()
        _bwArray.value = emptyList()
        _mapMarkers.value = emptyList()
        _testResults.value = emptyList()
        modelProducer(listOf(0f), listOf(0f))
    }

    //used to load existing results
    fun loadGraphAndMapExistingResults(tid: Int?) {
        clearResultsLists()
        getExecutedTestResults(tid)
    }

    private fun getGraphMapVals(results: List<MeasLatLon>) {

        val newMarkers = results.mapNotNull { result ->
            try {
                val bw = parseBwThr(result.measurment)
                if (bw.isEmpty()) return@mapNotNull null

                SpeedMapMarker(
                    GeoPoint(result.latitude, result.longitude),
                    bw.last()
                )
            } catch (e: IllegalStateException) {
                Log.wtf("CACHO", e.message ?: "Unknown error")
                null
            }
        }

        // Perform a single atomic update instead of inside the loop
        _mapMarkers.value = _mapMarkers.value + newMarkers

        // Build graph only once at the end
        if (_bwArray.value.isNotEmpty() || _transferArray.value.isNotEmpty()) {
            modelProducer(_bwArray.value, _transferArray.value)
        }
    }


    fun resetTestConfig() {
        _uiStateFlow.value = newTestConfig()
    }

    private fun newTestConfig(): TestUiState {
        return TestUiState(null, "111.111.111.111", 1000, 10, 1, true, "CHANGE ME", false, false)
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
            _executedTestsList.value = emptyList()

            _executedTestsList.value =
                _executedTestsList.value + resultsRepository.getExecutedTests(
                    _filterStateFlow.value.tcp,
                    _filterStateFlow.value.udp,
                    _filterStateFlow.value.upload,
                    _filterStateFlow.value.download
                )
        }
    }

    /*fun getExecutedTestResults(testID: Int?) {
        viewModelScope.launch(ioDispatcher) {
            _loadedTestResults.value = resultsRepository.getExecutedTestResults(testID)
            Log.d("CACHO","Results size: ${_loadedTestResults.value.size}")
            for (result in _loadedTestResults.value) {
                _testResults.value = _testResults.value + result.measurment
            }
        }
    }*/

    fun getExecutedTestResults(testID: Int?) {
        viewModelScope.launch(ioDispatcher) {
                        val results = resultsRepository.getExecutedTestResults(testID)
            _loadedTestResults.value = _loadedTestResults.value + results
            Log.d("CACHO", "Results size: ${results.size}")
            // Build a new list from measurement values
            val measurements = results.map { it.measurment }
            _testResults.value = _testResults.value + measurements

            getGraphMapVals(_loadedTestResults.value)
        }
    }


    fun deleteExecutedTestsWithResults(executedTestId: ExecutedTestConfig) {
        viewModelScope.launch(ioDispatcher) {
            resultsRepository.deleteExecutedTestsWithResults(executedTestId)
            _executedTestsList.value = resultsRepository.getExecutedTests(
                _filterStateFlow.value.tcp,
                _filterStateFlow.value.udp,
                _filterStateFlow.value.upload,
                _filterStateFlow.value.download
            )
        }
    }

    fun getResultsCount() {
        viewModelScope.launch(ioDispatcher) {
            _resultsCount.value = resultsRepository.getResultsCount()
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

data class FilterState(
    var server: String = "",
    var tcp: Boolean = true,
    var udp: Boolean = true,
    var upload: Boolean = true,
    var download: Boolean = true
) {
}

class SpeedMapMarker(val location: GeoPoint, val throughput: Float) {

}




