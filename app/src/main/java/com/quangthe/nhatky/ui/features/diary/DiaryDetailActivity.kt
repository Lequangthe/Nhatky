package com.quangthe.nhatky.ui.features.diary

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quangthe.nhatky.R
import com.quangthe.nhatky.commons.utils.DateUtils
import com.quangthe.nhatky.commons.utils.EasyDiaryUtils
import com.quangthe.nhatky.core.config.*
import com.quangthe.nhatky.enums.*
import com.quangthe.nhatky.extensions.*
import com.quangthe.nhatky.models.*
import com.quangthe.nhatky.ui.base.EasyDiaryComposeBaseActivity
import com.quangthe.nhatky.ui.theme.AppTheme
import com.quangthe.nhatky.viewmodels.DiaryEditingViewModel
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import com.simplemobiletools.commons.extensions.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.Locale

import com.quangthe.nhatky.ui.features.media.PhotoViewPagerActivity
import java.util.regex.Pattern

class DiaryDetailActivity : EasyDiaryComposeBaseActivity() {
    private val viewModel: DiaryEditingViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(getThemeId())
        super.onCreate(savedInstanceState)

        val sequence = intent.getIntExtra(DIARY_SEQUENCE, -1)
        if (sequence != -1) {
            viewModel.loadDiary(sequence, this)
        } else {
            intent.getStringExtra("share_text")?.let { viewModel.updateContents(it) }
            intent.getStringArrayListExtra("share_uris")?.let { paths ->
                val photoUris = paths.mapNotNull { pathStr ->
                    val parts = pathStr.split("||", limit = 2)
                    if (parts.size == 2) com.quangthe.nhatky.models.PhotoUri(parts[0], parts[1]) else null
                }
                if (photoUris.isNotEmpty()) viewModel.addPhotoUris(photoUris)
            }
        }

        setContent {
            AppTheme {
                DetailScreen(isNewEntry = sequence == -1)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DetailScreen(isNewEntry: Boolean) {
        val diary by viewModel.diary.collectAsState()
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        
        var isEditMode by remember { mutableStateOf(isNewEntry) }

        // --- Photo Picker Launcher ---
        val photoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(),
            onResult = { uris ->
                viewModel.addMediaUris(context, uris)
            }
        )

        // --- Video Picker Launcher ---
        val videoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(),
            onResult = { uris ->
                viewModel.addMediaUris(context, uris)
            }
        )

        // --- Camera ---
        var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
        val cameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture(),
            onResult = { success ->
                val uri = cameraImageUri
                if (success && uri != null) {
                    viewModel.addMediaUris(context, listOf(uri))
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
                    viewModel.addMediaUris(context, listOf(uri))
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

        // --- Audio Picker Launcher ---
        val audioPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetMultipleContents(),
            onResult = { uris ->
                viewModel.addMediaUris(context, uris)
            }
        )

        // --- Audio Recording Launcher ---
        val recordAudioLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = { result ->
                if (result.resultCode == android.app.Activity.RESULT_OK) {
                    val uri = result.data?.data
                    if (uri != null) {
                        viewModel.addMediaUris(context, listOf(uri))
                    }
                }
            }
        )

        // --- Speech to Text Launcher ---
        val speechLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = { result ->
                if (result.resultCode == android.app.Activity.RESULT_OK) {
                    val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
                    if (!spokenText.isNullOrBlank()) {
                        val currentContents = diary.contents ?: ""
                        viewModel.updateContents(if (currentContents.isBlank()) spokenText else "$currentContents $spokenText")
                    }
                }
            }
        )

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

        // --- Media Choice States ---
        var showPhotoChoice by remember { mutableStateOf(false) }
        var showVideoChoice by remember { mutableStateOf(false) }
        var showAudioChoice by remember { mutableStateOf(false) }

        if (showPhotoChoice) {
            AlertDialog(
                onDismissRequest = { showPhotoChoice = false },
                title = { Text("Select Photo Source") },
                text = { Text("Choose a photo from your gallery or take a new one.") },
                confirmButton = {
                    TextButton(onClick = {
                        showPhotoChoice = false
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }) { Text("Gallery") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showPhotoChoice = false
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
                    }) { Text("Camera") }
                }
            )
        }

        if (showVideoChoice) {
            AlertDialog(
                onDismissRequest = { showVideoChoice = false },
                title = { Text("Select Video Source") },
                text = { Text("Choose a video from your gallery or record a new one.") },
                confirmButton = {
                    TextButton(onClick = {
                        showVideoChoice = false
                        videoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                    }) { Text("Gallery") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showVideoChoice = false
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
                    }) { Text("Camera") }
                }
            )
        }

        if (showAudioChoice) {
            AlertDialog(
                onDismissRequest = { showAudioChoice = false },
                title = { Text("Select Audio Source") },
                text = { Text("Choose an audio file from your device or record a new one.") },
                confirmButton = {
                    TextButton(onClick = {
                        showAudioChoice = false
                        val intent = Intent(android.provider.MediaStore.Audio.Media.RECORD_SOUND_ACTION)
                        try {
                            recordAudioLauncher.launch(intent)
                        } catch (e: Exception) {
                            context.toast("Sound recorder not supported")
                        }
                    }) { Text("Record") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showAudioChoice = false
                        audioPickerLauncher.launch("audio/*")
                    }) { Text("Library") }
                }
            )
        }

        // --- Link dialog state ---
        var showLinkDialog by remember { mutableStateOf(false) }
        var linkUrl by remember { mutableStateOf("") }
        var linkLabel by remember { mutableStateOf("") }

        // --- Auto-location ---
        /* Disabled auto-location as per user request */
        /*
        LaunchedEffect(diary.currentTimeMillis) {
            if (context.config.enableLocationInfo && diary.location == null && isEditMode) {
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
        */

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
                    title = { Text(if (isEditMode) "Edit Diary" else "Read Diary") },
                    navigationIcon = {
                        IconButton(onClick = { 
                            if (isEditMode && !isNewEntry) {
                                isEditMode = false
                            } else {
                                finishActivityWithTransition() 
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        if (isEditMode) {
                            IconButton(onClick = {
                                viewModel.saveDiary()
                                if (isNewEntry) {
                                    finishActivityWithTransition()
                                } else {
                                    isEditMode = false
                                }
                            }) {
                                Icon(Icons.Default.Done, contentDescription = "Save")
                            }
                        } else {
                            IconButton(onClick = { isEditMode = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                if (!isEditMode) {
                    FloatingActionButton(
                        onClick = { isEditMode = true },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
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
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (isEditMode) {
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
                } else {
                    Text(
                        text = diary.title ?: "Untitled",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isEditMode) {
                    TextField(
                        value = diary.contents ?: "",
                        onValueChange = { viewModel.updateContents(it) },
                        placeholder = { Text("Contents") },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp),
                        textStyle = MaterialTheme.typography.bodyMedium,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                } else {
                    Text(
                        text = diary.contents ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                }

                Spacer(Modifier.height(12.dp))

                // --- Media Preview Area ---
                MediaEditPreview(
                    diary = diary,
                    isEditMode = isEditMode,
                    onRemovePhoto = { viewModel.removePhotoUri(it) },
                    onRemoveLocation = { viewModel.removeLocation() }
                )

                Spacer(Modifier.height(12.dp))

                // --- Action buttons row ---
                AnimatedVisibility(
                    visible = isEditMode,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(onClick = { showPhotoChoice = true }) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = "Photo", tint = MaterialTheme.colorScheme.primary)
                                Text("Photo", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        IconButton(onClick = { showVideoChoice = true }) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Videocam, contentDescription = "Video", tint = MaterialTheme.colorScheme.primary)
                                Text("Video", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        IconButton(onClick = { showAudioChoice = true }) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Mic, contentDescription = "Audio", tint = MaterialTheme.colorScheme.primary)
                                Text("Audio", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        IconButton(onClick = {
                            val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
                            }
                            try {
                                speechLauncher.launch(speechIntent)
                            } catch (e: Exception) {
                                context.toast("Speech recognition not supported")
                            }
                        }) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.RecordVoiceOver, contentDescription = "Dictation")
                                Text("Speech", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        IconButton(onClick = { showLinkDialog = true }) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Link, contentDescription = "Link")
                                Text("Link", style = MaterialTheme.typography.labelSmall)
                            }
                        }

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
    }

    private fun createMediaFile(mimeType: String): java.io.File {
        val dirName = if (mimeType.startsWith("video")) DIARY_VIDEO_DIRECTORY else DIARY_PHOTO_DIRECTORY
        val dir = java.io.File(EasyDiaryUtils.getApplicationDataDirectory(this) + dirName)
        if (!dir.exists()) dir.mkdirs()
        val ext = if (mimeType.startsWith("video")) ".mp4" else ".jpg"
        return java.io.File(dir, "${UUID.randomUUID()}$ext")
    }
}

@Composable
fun MediaEditPreview(
    diary: Diary,
    isEditMode: Boolean,
    onRemovePhoto: (PhotoUri) -> Unit,
    onRemoveLocation: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Parse links from contents
    val links = remember(diary.contents) {
        val pattern = Pattern.compile("\\[(.*?)\\]\\((.*?)\\)")
        val matcher = pattern.matcher(diary.contents ?: "")
        val result = mutableListOf<Pair<String, String>>()
        while (matcher.find()) {
            result.add(Pair(matcher.group(1) ?: "", matcher.group(2) ?: ""))
        }
        result
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        val photos = diary.photoUris ?: emptyList()
        val location = diary.location
        
        if (photos.isNotEmpty() || location != null || links.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                // Photos / Videos / Audio
                items(photos.size) { index ->
                    val photo = photos[index]
                    val photoPath = remember(photo.photoUri) {
                        if (photo.isContentUri()) {
                            photo.photoUri
                        } else {
                            if (photo.photoUri?.startsWith("/") == true) {
                                photo.photoUri
                            } else {
                                EasyDiaryUtils.getApplicationDataDirectory(context) + photo.getFilePath()
                            }
                        }
                    }
                    Box(modifier = Modifier.size(if (isEditMode) 80.dp else 120.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                .clickable(!isEditMode) {
                                    when {
                                        photo.isVideo() || photo.isAudio() -> {
                                            val intent = Intent(context, com.quangthe.nhatky.ui.features.media.MediaViewerActivity::class.java).apply {
                                                putExtra("path", photoPath)
                                                putExtra("mimeType", photo.mimeType)
                                            }
                                            context.startActivity(intent)
                                        }
                                        else -> {
                                            val intent = Intent(context, PhotoViewPagerActivity::class.java).apply {
                                                putExtra(DIARY_SEQUENCE, diary.sequence)
                                                putExtra(DIARY_ATTACH_PHOTO_INDEX, index)
                                            }
                                            context.startActivity(intent)
                                        }
                                    }
                                }
                        ) {
                            GlideImage(
                                imageModel = { photoPath },
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier.fillMaxSize(),
                                loading = {
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(Modifier.size(24.dp))
                                    }
                                },
                                failure = {
                                    Icon(if (photo.isAudio()) Icons.Default.Audiotrack else Icons.Default.BrokenImage, 
                                        null, Modifier.align(Alignment.Center), 
                                        tint = if (photo.isAudio()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                                }
                            )
                            if (photo.isVideo()) {
                                Box(
                                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(32.dp))
                                }
                            }
                            if (photo.isAudio()) {
                                Box(
                                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.secondaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Audiotrack, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                                }
                            }
                        }
                        if (isEditMode) {
                            IconButton(
                                onClick = { onRemovePhoto(photo) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 8.dp, y = (-8).dp)
                                    .size(24.dp)
                                    .background(MaterialTheme.colorScheme.error, CircleShape)
                            ) {
                                Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                // Links
                items(links) { link ->
                    Box(modifier = Modifier.size(if (isEditMode) 80.dp else 120.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f))
                                .border(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable(!isEditMode) {
                                    var url = link.second
                                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                        url = "https://$url"
                                    }
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        context.toast("Cannot open link: $url")
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Link, null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(32.dp))
                                Text(
                                    text = link.first,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Location
                if (location != null) {
                    item {
                        Box(modifier = Modifier.size(if (isEditMode) 80.dp else 120.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
                                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .clickable(!isEditMode) {
                                        val lat = location.latitude
                                        val lng = location.longitude
                                        val label = location.address ?: "Location"
                                        val uri = Uri.parse("geo:0,0?q=$lat,$lng(${Uri.encode(label)})")
                                        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                                        try {
                                            context.startActivity(mapIntent)
                                        } catch (e: Exception) {
                                            val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng")
                                            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                                    Text(
                                        text = location.address ?: "Vị trí",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                }
                            }
                            if (isEditMode) {
                                IconButton(
                                    onClick = onRemoveLocation,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 8.dp, y = (-8).dp)
                                        .size(24.dp)
                                        .background(MaterialTheme.colorScheme.error, CircleShape)
                                ) {
                                    Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
