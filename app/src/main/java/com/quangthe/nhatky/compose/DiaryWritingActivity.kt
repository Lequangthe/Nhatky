package com.quangthe.nhatky.compose

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.quangthe.nhatky.R
import com.quangthe.nhatky.enums.*
import com.quangthe.nhatky.extensions.*
import com.quangthe.nhatky.helper.*
import com.quangthe.nhatky.models.*
import com.quangthe.nhatky.repositories.DiaryRepository
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
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                TextField(
                    value = diary.title ?: "",
                    onValueChange = { viewModel.updateTitle(it) },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = diary.contents ?: "",
                    onValueChange = { viewModel.updateContents(it) },
                    label = { Text("Contents") },
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
                Button(onClick = {
                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) {
                    Text("Add Photos")
                }
            }
        }
    }
}
