package com.quangthe.nhatky.compose

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.coroutines.*
import com.quangthe.nhatky.commons.utils.BiometricUtils.Companion.startListeningBiometric
import com.quangthe.nhatky.commons.utils.DateUtils
import com.quangthe.nhatky.commons.utils.EasyDiaryUtils
import com.quangthe.nhatky.BuildConfig
import com.quangthe.nhatky.R
import com.quangthe.nhatky.dialogs.ActionLogDialog
import com.quangthe.nhatky.enums.DialogMode
import com.quangthe.nhatky.enums.Launcher
import com.quangthe.nhatky.extensions.*
import com.quangthe.nhatky.helper.*
import com.quangthe.nhatky.repositories.*
import com.quangthe.nhatky.models.*
import com.quangthe.nhatky.services.NotificationService
import com.quangthe.nhatky.ui.components.*
import com.quangthe.nhatky.ui.theme.AppTheme
import com.quangthe.nhatky.viewmodels.BaseDevViewModel
import com.quangthe.nhatky.commons.utils.*
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

open class BaseDevActivity : EasyDiaryComposeBaseActivity() {
    private var mNotificationCount = 9000
    private var mCoroutineJob: Job? = null
    protected val mViewModel: BaseDevViewModel by viewModels()
    private val mLocationManager by lazy { getSystemService(LOCATION_SERVICE) as LocationManager }
    
    private val mGPSLocationListener = object : LocationListener {
        override fun onLocationChanged(p0: Location) {
            if (config.enableDebugOptionToastLocation) makeToast("GPS location has been updated")
            mLocationManager.removeUpdates(this)
        }
    }
    
    private val mNetworkLocationListener = object : LocationListener {
        override fun onLocationChanged(p0: Location) {
            if (config.enableDebugOptionToastLocation) makeToast("Network location has been updated")
            mLocationManager.removeUpdates(this)
        }
    }

    private val mRequestLocationSourceLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        makeSnackBar(if (isLocationEnabled()) "GPS provider setting is activated!!!" else "The request operation did not complete normally.")
    }

    private val mPickMultipleMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(10)) { uris ->
        if (uris.isNotEmpty()) {
            showAlertDialog(uris.joinToString(",") { it.toString() }, null, null, DialogMode.INFO, false)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                updateStatusBarAppearance()
                updateNavigationBarAppearance()
                
                Scaffold(
                    topBar = {
                        EasyDiaryActionBar(
                            title = "Easy-Diary Dev Mode",
                            subTitle = String.format(Locale.getDefault(), "v%s_%s (%d)", BuildConfig.VERSION_NAME, BuildConfig.BUILD_TYPE, BuildConfig.VERSION_CODE),
                        ) {
                            finishActivityWithPauseLock()
                        }
                    },
                    containerColor = Color(config.screenBackgroundColor),
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                        DevScreen(mViewModel)
                    }
                }
            }
        }
    }

    @Composable
    fun DevScreen(viewModel: BaseDevViewModel) {
        val configuration = LocalConfiguration.current
        val maxItemsInEachRow = if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 3
        
        CardContainer(modifier = Modifier.fillMaxSize()) {
            val settingCardModifier = Modifier.weight(1f)
            RefactoringBacklog(settingCardModifier, maxItemsInEachRow)
            CustomLauncher(settingCardModifier, maxItemsInEachRow)
            DevModeSettings(settingCardModifier, maxItemsInEachRow, viewModel)
            DebugToast(settingCardModifier, maxItemsInEachRow)
            Etc(settingCardModifier, maxItemsInEachRow)
            ComposeDemo(settingCardModifier, maxItemsInEachRow, viewModel)
            Notification(settingCardModifier, maxItemsInEachRow)
            AlertDialog(settingCardModifier, maxItemsInEachRow)
            LocationManager(settingCardModifier, maxItemsInEachRow, viewModel)
            Coroutine(settingCardModifier, maxItemsInEachRow, viewModel)
            FingerPrint(settingCardModifier, maxItemsInEachRow)
        }
    }

    @Composable
    protected fun RefactoringBacklog(modifier: Modifier, maxItemsInEachRow: Int) {
        val currentContext = LocalContext.current
        val currentActivity = LocalActivity.current
        CategoryTitleCard(title = "Refactoring Backlog", marginTop = 0)
        FlowRow(maxItemsInEachRow = maxItemsInEachRow, modifier = Modifier) {
            SimpleCard("Self Development", "Self Development Repository", modifier = modifier) {
                TransitionHelper.startActivityWithTransition(currentActivity, Intent(currentContext, SelfDevelopmentRepoActivity::class.java))
            }
            SimpleCard("Mig DiaryMain", "Migrated DiaryMain screen to Jetpack Compose", modifier = modifier) {
                TransitionHelper.startActivityWithTransition(currentActivity, Intent(currentContext, DiaryMainActivity::class.java))
            }
        }
    }

    @Composable
    protected fun CustomLauncher(modifier: Modifier, maxItemsInEachRow: Int) {
        CategoryTitleCard(title = "Custom Launcher")
        FlowRow(maxItemsInEachRow = maxItemsInEachRow, modifier = Modifier) {
            SimpleCardWithImage("EasyDiary Launcher", "Basic launcher icon", modifier = modifier, imageResourceId = R.drawable.ic_launcher_round) { toggleLauncher(Launcher.EASY_DIARY) }
            SimpleCardWithImage("Dark Launcher", "Dark theme launcher icon", modifier = modifier, imageResourceId = R.drawable.ic_launcher_dark_round) { toggleLauncher(Launcher.DARK) }
            SimpleCardWithImage("Green Launcher", "Green theme launcher icon", modifier = modifier, imageResourceId = R.drawable.ic_launcher_green_round) { toggleLauncher(Launcher.GREEN) }
            SimpleCardWithImage("Debug Launcher", "Debug launcher icon", modifier = modifier, imageResourceId = R.drawable.ic_launcher_debug_round) { toggleLauncher(Launcher.DEBUG) }
        }
    }

    @Composable
    protected fun DevModeSettings(modifier: Modifier, maxItemsInEachRow: Int, viewModel: BaseDevViewModel) {
        CategoryTitleCard(title = "DevMode Settings")
        FlowRow(modifier = Modifier, maxItemsInEachRow = maxItemsInEachRow) {
            SwitchCard("Display Diary Sequence", "Visible diary sequence", modifier = modifier, viewModel.enableDebugOptionVisibleDiarySequence) { viewModel.toggleDebugOptionVisibleDiarySequence() }
            SwitchCard("Display Alarm Sequence", "Visible alarm sequence", modifier = modifier, viewModel.enableDebugOptionVisibleAlarmSequence) { viewModel.toggleDebugOptionVisibleAlarmSequence() }
        }
    }

    @Composable
    protected fun DebugToast(modifier: Modifier, maxItemsInEachRow: Int) {
        val config = LocalContext.current.config
        CategoryTitleCard(title = "Debug Toast")
        FlowRow(maxItemsInEachRow = maxItemsInEachRow) {
            SwitchCard("Attached Photo Toast", null, modifier, config.enableDebugOptionToastAttachedPhoto) { config.enableDebugOptionToastAttachedPhoto = !config.enableDebugOptionToastAttachedPhoto }
        }
    }

    @Composable
    protected fun Etc(modifier: Modifier, maxItemsInEachRow: Int) {
        CategoryTitleCard(title = "Etc.")
        FlowRow(modifier = Modifier, maxItemsInEachRow = maxItemsInEachRow) {
            SimpleCard("Build Info", "Show device info", modifier = modifier) {
                showAlertDialog("Manufacturer: ${Build.MANUFACTURER}\nModel: ${Build.MODEL}\nOS: ${Build.VERSION.RELEASE}\nSDK: ${Build.VERSION.SDK_INT}")
            }
        }
    }

    @Composable
    protected fun ComposeDemo(modifier: Modifier, maxItemsInEachRow: Int, viewModel: BaseDevViewModel) {
        CategoryTitleCard(title = "ComposeDemo")
        FlowRow(modifier = Modifier, maxItemsInEachRow = maxItemsInEachRow) {
            SymbolCard(modifier = modifier, viewModel) { viewModel.plus() }
        }
    }

    @Composable
    protected fun Notification(modifier: Modifier, maxItemsInEachRow: Int) {
        CategoryTitleCard(title = "Notification")
        FlowRow(modifier = Modifier, maxItemsInEachRow = maxItemsInEachRow) {
            SimpleCard("Notification-01", "Basic", modifier = modifier) { createNotificationBasic() }
        }
    }

    @Composable
    protected fun AlertDialog(modifier: Modifier, maxItemsInEachRow: Int) {
        CategoryTitleCard(title = "Alert Dialog")
        FlowRow(modifier = Modifier, maxItemsInEachRow = maxItemsInEachRow) {
            SimpleCard("Dialog Info", "INFO", modifier = modifier) { showAlertDialog("Info message", null, null, DialogMode.INFO, false) }
        }
    }

    @Composable
    protected fun LocationManager(modifier: Modifier, maxItemsInEachRow: Int, viewModel: BaseDevViewModel) {
        CategoryTitleCard(title = "Location Manager")
        SimpleCard("Location Info", viewModel.locationInfo, modifier = Modifier.fillMaxWidth()) {}
    }

    @Composable
    protected fun Coroutine(modifier: Modifier, maxItemsInEachRow: Int, viewModel: BaseDevViewModel) {
        CategoryTitleCard(title = "Coroutine")
        ScrollableCard("Console", viewModel.coroutine1Console, Modifier.fillMaxWidth(), rememberScrollState())
    }

    @Composable
    protected fun FingerPrint(modifier: Modifier, maxItemsInEachRow: Int) {
        CategoryTitleCard(title = "Finger Print")
        FlowRow(maxItemsInEachRow = maxItemsInEachRow) {
            SimpleCard("Biometric", "Start Listening", modifier = modifier) { startListeningBiometric(this@BaseDevActivity) }
        }
    }

    private fun createNotificationBasic() {
        // Implementation for testing
    }

    override fun onDestroy() {
        super.onDestroy()
        mLocationManager.removeUpdates(mGPSLocationListener)
        mLocationManager.removeUpdates(mNetworkLocationListener)
    }
}
