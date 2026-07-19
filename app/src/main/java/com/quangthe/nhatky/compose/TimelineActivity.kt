package com.quangthe.nhatky.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.quangthe.nhatky.R
import com.quangthe.nhatky.extensions.*
import com.quangthe.nhatky.helper.*
import com.quangthe.nhatky.models.Diary
import com.quangthe.nhatky.repositories.DiaryRepository
import com.quangthe.nhatky.ui.theme.AppTheme

class TimelineActivity : EasyDiaryComposeBaseActivity() {
    private val diaryRepository = DiaryRepository()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(getThemeId())
        super.onCreate(savedInstanceState)
        
        setContent {
            AppTheme {
                TimelineScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TimelineScreen() {
        var diaries by remember { mutableStateOf(emptyList<Diary>()) }
        
        LaunchedEffect(Unit) {
            diaries = diaryRepository.findDiary(null, entryType = 0)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.timeline_title)) },
                    navigationIcon = {
                        IconButton(onClick = { finishActivityWithTransition() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
                items(diaries) { diary ->
                    Text(text = diary.title ?: "", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}
