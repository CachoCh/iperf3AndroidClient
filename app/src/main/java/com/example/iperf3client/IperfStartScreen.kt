package com.example.iperf3client

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.iperf3client.data.TestDatabase
import com.example.iperf3client.ui.MeasurementsScreen
import com.example.iperf3client.ui.NewTestScreen
import com.example.iperf3client.ui.SavedTestsScreen
import com.example.iperf3client.ui.ui.AboutScreen
import com.example.iperf3client.ui.ui.RunningTestScreen
import com.example.iperf3client.ui.ui.WelcomeScreen
import com.example.iperf3client.ui.ui.navigator.NavigationItems
import com.example.iperf3client.utils.DbUtils
import com.example.iperf3client.utils.DbUtils.Companion.shareRoomDatabase
import com.example.iperf3client.viewmodels.TestViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream

val EXPORT_DATA_REQUEST = 101

/**
 * enum values that represent the screens in the app
 */
enum class IperfScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    NewTest(title = R.string.new_test),
    RunningTest(title = R.string.running_test),
    SavedTests(title = R.string.saved_tests),
    Measurements(title = R.string.measurements),
    About(title = R.string.measurements)

}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun IperfApp(
    testViewModel: TestViewModel,
    context: Context,
    navController: NavHostController = rememberNavController(),

    ) {
    NavigationDrawer(navController, testViewModel, context)
}

@Composable
fun Navigation(
    navController: NavHostController,
    innerPadding: PaddingValues,
    testVM: TestViewModel,
    context: Context
) {
    val testUiState by testVM.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = IperfScreen.Start.name,
        modifier = Modifier
            .fillMaxSize()
            //.verticalScroll(rememberScrollState())
            .padding(innerPadding)
    ) {


        composable(route = IperfScreen.Start.name) {
            WelcomeScreen(
                onNewTestButtonClicked = {
                    testUiState.tid=null
                    navController.navigate(IperfScreen.NewTest.name)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(R.dimen.padding_medium)),
                testVM
            )
        }
        composable(route = IperfScreen.NewTest.name) {
            NewTestScreen(
                testVM,
                onCancelButtonClicked = {
                    cancelOrderAndNavigateToStart(navController)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(R.dimen.padding_medium))
            )
        }
        composable(route = IperfScreen.RunningTest.name) {
            RunningTestScreen(
                testVM,
                onCancelButtonClicked = {
                    cancelOrderAndNavigateToStart(navController)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(R.dimen.padding_medium))
            )
        }
        composable(route = IperfScreen.SavedTests.name) {
            SavedTestsScreen(
                testVM,
                onCancelButtonClicked = {
                    cancelOrderAndNavigateToStart(navController)
                },
                onItemClick = {
                    if (it != null) {
                        testVM.getTest(it)
                        testVM.clearTestScreen()
                    }
                    navController.navigate(IperfScreen.NewTest.name)
                },
                onRunTestClicked = { server, port, duration, interval, reverse, udp ->
                    if (port != null && server != null && duration != null && interval != null && reverse != null) {
                        testVM.runIperfTest(
                            server,
                            port,
                            duration,
                            interval,
                            reverse,
                            udp,
                            context
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(R.dimen.padding_medium))
            )
        }
        composable(route = IperfScreen.Measurements.name) {
            MeasurementsScreen(
                testVM,
                onCancelButtonClicked = {
                    cancelOrderAndNavigateToStart(navController)
                },
                onItemClick = { tid ->
                    testVM.getExecutedTestResults(tid)
                    navController.navigate(IperfScreen.RunningTest.name)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(R.dimen.padding_medium)),
                onShareClick = {
                    DbUtils.backupDatabase(
                        context,
                        TestDatabase.DATABASE_NAME,
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    )
                }
            )
        }
        composable(route = IperfScreen.About.name) {
            AboutScreen(context)
        }
    }
}

//navController.navigate(CupcakeScreen.Pickup.name
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawer(
    navController: NavHostController,
    testViewModel: TestViewModel,
    context: Context
) {
    val items = listOf(
        NavigationItems(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            screen = IperfScreen.Start

        ),
        NavigationItems(
            title = "Saved Tests",
            selectedIcon = Icons.Filled.Star,
            unselectedIcon = Icons.Outlined.Star,
            screen = IperfScreen.SavedTests
        ),
        NavigationItems(
            title = "New Test",
            selectedIcon = Icons.Filled.Add,
            unselectedIcon = Icons.Outlined.Add,
            screen = IperfScreen.NewTest,
        ),
        NavigationItems(
            title = "Running Test",
            selectedIcon = Icons.Filled.PlayArrow,
            unselectedIcon = Icons.Outlined.PlayArrow,
            screen = IperfScreen.RunningTest
        ),
        NavigationItems(
            title = "Results",
            selectedIcon = Icons.Filled.DateRange,
            unselectedIcon = Icons.Outlined.DateRange,
            screen = IperfScreen.Measurements
        ),
        NavigationItems(
            title = "About",
            selectedIcon = Icons.Filled.Info,
            unselectedIcon = Icons.Outlined.Info,
            screen = IperfScreen.About
        )
    )

    //Remember Clicked item state
    var selectedItemIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                items.forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        label = { Text(text = item.title) },
                        selected = index == selectedItemIndex,
                        onClick = {
                            when (item.screen) {
                                IperfScreen.SavedTests -> testViewModel.getTests()
                                IperfScreen.NewTest -> testViewModel.resetTestConfig()
                                IperfScreen.Measurements -> testViewModel.getExecutedTests()
                                IperfScreen.RunningTest -> {} //TODO() //testViewModel.testResults = MutableStateFlow(listOf<String>()).asStateFlow()
                                IperfScreen.Start -> {} //TODO()
                                else -> {}
                            }
                            navController.navigate(item.screen.name)

                            selectedItemIndex = index
                            scope.launch {
                                drawerState.close()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (index == selectedItemIndex) {
                                    item.selectedIcon
                                } else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        badge = {
                            item.badgeCount?.let {
                                Text(text = item.badgeCount.toString())
                            }
                        },
                        modifier = Modifier
                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }

            }
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "iPerf3")
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    },
                    actions = {
                        DropdownMenu()
                    }

                )
            }
        ) { innerPadding ->
            Navigation(navController, innerPadding, testViewModel, context)
        }
    }
}

@Composable
fun DropdownMenu() {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var exportUri by remember { mutableStateOf<Uri?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri: Uri? ->
        if (uri != null) {
            exportUri = uri
        }
    }

    LaunchedEffect(exportUri) {
        exportUri?.let { uri ->
            val dbFile = context.getDatabasePath(TestDatabase.DATABASE_NAME)
            if (dbFile.exists()) {
                try {
                    FileInputStream(dbFile).use { input ->
                        context.contentResolver.openOutputStream(uri)?.use { output ->
                            input.copyTo(output)
                        }
                    }
                    // Success message if needed
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Error message if needed
                }
            }
            exportUri = null
        }
    }

    Box(
        modifier = Modifier
            .padding(16.dp)
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            val launcher =
                rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
            DropdownMenuItem(
                text = { Text("Share") },
                onClick = {
                    shareRoomDatabase(context,
                        TestDatabase.DATABASE_NAME,
                        launcher)
                }
            )
            DropdownMenuItem(
                text = { Text("Export DB") },
                onClick = {
                    expanded = false
                    exportLauncher.launch(TestDatabase.DATABASE_NAME)
                }
            )
        }
    }
}

/**
 * Resets the [OrderUiState] and pops up to [CupcakeScreen.Start]
 */
private fun cancelOrderAndNavigateToStart(
    navController: NavHostController
) {
    navController.popBackStack(IperfScreen.Start.name, inclusive = false)
}

@Composable
private fun getCurrentScreen(navController: NavHostController): IperfScreen {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    return IperfScreen.valueOf(
        backStackEntry?.destination?.route ?: IperfScreen.Start.name
    )
}