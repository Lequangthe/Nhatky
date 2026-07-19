package com.quangthe.nhatky.compose

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.quangthe.nhatky.R
import com.quangthe.nhatky.extensions.*
import com.quangthe.nhatky.helper.*
import com.quangthe.nhatky.models.Diary
import com.quangthe.nhatky.ui.theme.AppTheme
import com.quangthe.nhatky.viewmodels.DiaryReadViewModel

class DiaryReadingActivity : EasyDiaryComposeBaseActivity() {
    private val viewModel: DiaryReadViewModel by viewModels()

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
                ReadingScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ReadingScreen() {
        val diary by viewModel.diary.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.read_diary_detail_title)) },
                    navigationIcon = {
                        IconButton(onClick = { finishActivityWithTransition() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            val intent = Intent(this@DiaryReadingActivity, DiaryWritingActivity::class.java)
                            intent.putExtra(DIARY_SEQUENCE, diary.sequence)
                            TransitionHelper.startActivityWithTransition(this@DiaryReadingActivity, intent)
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Text(text = diary.title ?: "", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = diary.contents ?: "", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
