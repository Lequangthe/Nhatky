package com.quangthe.nhatky.ui.features.diary

import android.content.Intent
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
import androidx.compose.material.icons.filled.Done
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
import com.quangthe.nhatky.repositories.DiaryRepository
import com.quangthe.nhatky.ui.base.EasyDiaryComposeBaseActivity
import com.quangthe.nhatky.ui.components.*
import com.quangthe.nhatky.ui.theme.AppTheme
import com.quangthe.nhatky.viewmodels.DiaryEditingViewModel

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
        
        val photoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(),
            onResult = { uris ->
                viewModel.addPhotoUris(uris.map { PhotoUri(it.toString()) })
            }
        )

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
            val context = LocalContext.current
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
                
                Button(
                    onClick = {
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text("Add Photos")
                }
            }
        }
    }
}
