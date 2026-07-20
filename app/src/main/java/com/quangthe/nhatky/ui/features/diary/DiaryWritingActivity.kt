package com.quangthe.nhatky.ui.features.diary

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.quangthe.nhatky.R
import com.quangthe.nhatky.commons.utils.DateUtils
import com.quangthe.nhatky.enums.*
import com.quangthe.nhatky.extensions.*
import com.quangthe.nhatky.core.config.*
import com.quangthe.nhatky.models.*
import com.quangthe.nhatky.ui.base.EasyDiaryComposeBaseActivity
import com.quangthe.nhatky.ui.theme.AppTheme
import com.quangthe.nhatky.viewmodels.DiaryEditingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.quangthe.nhatky.commons.utils.EasyDiaryUtils
import java.util.UUID

class DiaryWritingActivity : EasyDiaryComposeBaseActivity() {
    private val viewModel: DiaryEditingViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(getThemeId())
        super.onCreate(savedInstanceState)

        val sequence = intent.getIntExtra(DIARY_SEQUENCE, -1)
        if (sequence != -1) {
            viewModel.loadDiary(sequence)
        }

        setContent {
            AppTheme {
                WritingScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun WritingScreen() {
        val diary by viewModel.diary.collectAsState()
        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        // --- Photo Picker ---
        val photoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(),
            onResult = { uris ->
                viewModel.addPhotoUris(uris.map { PhotoUri(it.toString()) })
            }
        )

        // --- Camera ---
        var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
        val cameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture(),
            onResult = { success ->
                val uri = cameraImageUri
                if (success && uri != null) {
                    viewModel.addPhotoUris(listOf(PhotoUri(uri.toString(), "image/jpeg")))
                }
            }
        )
        val cameraPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                val file = createMediaFile("image/jpeg")
                val uri = context.getUriForFile(file)
                cameraImageUri = uri
                cameraLauncher.launch(uri)
            }
        }

        // --- Video Capture ---
        var videoCaptureUri by remember { mutableStateOf<Uri?>(null) }
        val videoCaptureLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CaptureVideo(),
            onResult = { success ->
                val uri = videoCaptureUri
                if (success && uri != null) {
                    viewModel.addPhotoUris(listOf(PhotoUri(uri.toString(), "video/mp4")))
                }
            }
        )
        val videoPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                val file = createMediaFile("video/mp4")
                val uri = context.getUriForFile(file)
                videoCaptureUri = uri
                videoCaptureLauncher.launch(uri)
            }
        }

        // --- Location Permission ---
        val locationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { granted ->
            if (granted.values.all { it }) {
                scope.launch {
                    val location = withContext(Dispatchers.IO) { context.getLastKnownLocation() }
                    if (location != null) {
                        val addresses = withContext(Dispatchers.IO) { context.getFromLocation(location.latitude, location.longitude, 1) }
                        val addressText = addresses?.firstOrNull()?.let { context.fullAddress(it) }
                        viewModel.updateLocation(
                            Location(
                                address = addressText,
                                latitude = location.latitude,
                                longitude = location.longitude
                            )
                        )
                    }
                }
            }
        }

        // --- Link dialog state ---
        var showLinkDialog by remember { mutableStateOf(false) }
        var linkUrl by remember { mutableStateOf("") }
        var linkLabel by remember { mutableStateOf("") }

        // --- Auto-location ---
        LaunchedEffect(diary.currentTimeMillis) {
            if (context.config.enableLocationInfo && diary.location == null) {
                val location = withContext(Dispatchers.IO) { context.getLastKnownLocation() }
                if (location != null) {
                    val addresses = withContext(Dispatchers.IO) { context.getFromLocation(location.latitude, location.longitude, 1) }
                    val addressText = addresses?.firstOrNull()?.let { context.fullAddress(it) }
                    viewModel.updateLocation(
                        Location(
                            address = addressText,
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                    )
                }
            }
        }

        // --- Link dialog ---
        if (showLinkDialog) {
            AlertDialog(
                onDismissRequest = { showLinkDialog = false; linkUrl = ""; linkLabel = "" },
                title = { Text("Add Link") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = linkUrl,
                            onValueChange = { linkUrl = it },
                            label = { Text("URL") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = linkLabel,
                            onValueChange = { linkLabel = it },
                            label = { Text("Label") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (linkUrl.isNotBlank()) {
                            val label = linkLabel.ifBlank { linkUrl }
                            val currentContents = diary.contents ?: ""
                            viewModel.updateContents(
                                if (currentContents.isBlank()) "[$label]($linkUrl)" else "$currentContents\n[$label]($linkUrl)"
                            )
                        }
                        showLinkDialog = false
                        linkUrl = ""
                        linkLabel = ""
                    }) { Text("Add") }
                },
                dismissButton = {
                    TextButton(onClick = { showLinkDialog = false; linkUrl = ""; linkLabel = "" }) {
                        Text("Cancel")
                    }
                }
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.create_diary_title)) },
                    navigationIcon = {
                        IconButton(onClick = { finishActivityWithTransition() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            viewModel.saveDiary()
                            finishActivityWithTransition()
                        }) {
                            Icon(Icons.Default.Done, contentDescription = "Save")
                        }
                    }
                )
            }
        ) { padding ->
            val dateTimeFormat = DateTimeFormat.valueOf(context.config.settingDatetimeFormat)
            val dateString = DateUtils.getDateTimeStringFromTimeMillis(
                diary.currentTimeMillis,
                dateTimeFormat.getDateKey(),
                dateTimeFormat.getTimeKey()
            )

            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = diary.title ?: "",
                    onValueChange = { viewModel.updateTitle(it) },
                    placeholder = {
                        Text(
                            "Title",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = diary.contents ?: "",
                    onValueChange = { viewModel.updateContents(it) },
                    placeholder = { Text("Contents") },
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(Modifier.height(12.dp))

                // --- Action buttons row ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Gallery
                    IconButton(onClick = {
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery")
                            Text("Gallery", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    // Camera
                    IconButton(onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (context.checkPermission(arrayOf(Manifest.permission.CAMERA))) {
                                val file = createMediaFile("image/jpeg")
                                val uri = context.getUriForFile(file)
                                cameraImageUri = uri
                                cameraLauncher.launch(uri)
                            } else {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        } else {
                            val file = createMediaFile("image/jpeg")
                            val uri = context.getUriForFile(file)
                            cameraImageUri = uri
                            cameraLauncher.launch(uri)
                        }
                    }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
                            Text("Camera", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    // Video
                    IconButton(onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (context.checkPermission(arrayOf(Manifest.permission.CAMERA))) {
                                val file = createMediaFile("video/mp4")
                                val uri = context.getUriForFile(file)
                                videoCaptureUri = uri
                                videoCaptureLauncher.launch(uri)
                            } else {
                                videoPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        } else {
                            val file = createMediaFile("video/mp4")
                            val uri = context.getUriForFile(file)
                            videoCaptureUri = uri
                            videoCaptureLauncher.launch(uri)
                        }
                    }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Videocam, contentDescription = "Video")
                            Text("Video", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    // Link
                    IconButton(onClick = { showLinkDialog = true }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Link, contentDescription = "Link")
                            Text("Link", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    // Location
                    IconButton(onClick = {
                        if (context.checkPermission(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))) {
                            scope.launch {
                                val location = withContext(Dispatchers.IO) { context.getLastKnownLocation() }
                                if (location != null) {
                                    val addresses = withContext(Dispatchers.IO) {
                                        context.getFromLocation(location.latitude, location.longitude, 1)
                                    }
                                    val addressText = addresses?.firstOrNull()?.let { context.fullAddress(it) }
                                    viewModel.updateLocation(
                                        Location(
                                            address = addressText,
                                            latitude = location.latitude,
                                            longitude = location.longitude
                                        )
                                    )
                                }
                            }
                        } else {
                            locationPermissionLauncher.launch(
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                            )
                        }
                    }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.LocationOn, contentDescription = "Location")
                            Text("Location", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }

    private fun createMediaFile(mimeType: String): java.io.File {
        val dirName = if (mimeType.startsWith("video")) DIARY_VIDEO_DIRECTORY else DIARY_PHOTO_DIRECTORY
        val dir = java.io.File(EasyDiaryUtils.getApplicationDataDirectory(this) + dirName)
        if (!dir.exists()) dir.mkdirs()
        val ext = if (mimeType.startsWith("video")) ".mp4" else ".jpg"
        return java.io.File(dir, "${UUID.randomUUID()}$ext")
    }
}
