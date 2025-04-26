package com.example.iperf3client

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import com.example.iperf3client.data.TestDatabase
import com.example.iperf3client.ui.ui.theme.IPerf3ClientTheme
import com.example.iperf3client.viewmodels.TestViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val testViewModel = TestViewModel(
                LocalContext.current,
                TestDatabase.getInstance(applicationContext)
            )
            IPerf3ClientTheme {
                testViewModel.getTestCount()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    applicationContext
                    checkPermissions(applicationContext)
                    IperfApp(testViewModel, applicationContext)
                }
            }
            addSampleTestsToDB(testViewModel)
        }
        if (shouldAskPermissions()) {
            askPermissions(this);
        }

    }
}

private fun addSampleTestsToDB(testViewModel: TestViewModel) {

    if (testViewModel.testCount.value == 0) {
        testViewModel.saveNewTest(
            "paris.bbr.iperf.bytel.fr",
            9220,
            100,
            1,
            true,
            false
        )

        testViewModel.saveNewTest(
            "paris.bbr.iperf.bytel.fr",
            9221,
            100,
            1,
            false,
            false
        )

        testViewModel.saveNewTest(
            "ch.iperf.014.fr",
            15317,
            100,
            1,
            true,
            false
        )

        testViewModel.saveNewTest(
            "ch.iperf.014.fr",
            15318,
            100,
            1,
            false,
            false
        )
    }
}

private fun shouldAskPermissions(): Boolean {
    return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
}

//@TargetApi(23)
fun askPermissions(myActivity: Activity) {
    val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.READ_PHONE_STATE
    )
    val requestCode = 200
    requestPermissions(myActivity, permissions, requestCode)
}

private fun checkPermissions(context: Context) {
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IPerf3ClientTheme {
        Greeting("Android")
    }
}