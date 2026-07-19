package com.quangthe.nhatky.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quangthe.nhatky.R
import com.quangthe.nhatky.compose.EasyDiaryComposeBaseActivity
import com.quangthe.nhatky.extensions.config
import com.quangthe.nhatky.ui.theme.AppTheme
import com.quangthe.nhatky.viewmodels.NoteViewModel

class SimpleNoteActivity : EasyDiaryComposeBaseActivity() {
    private val viewModel: NoteViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sequence = intent.getIntExtra("sequence", -1)
        viewModel.loadNote(sequence)

        setContent {
            val note by viewModel.note.collectAsState()
            var title by remember { mutableStateOf("") }
            var contents by remember { mutableStateOf("") }
            var selectedColor by remember { mutableIntStateOf(0) }
            var showColorPicker by remember { mutableStateOf(false) }

            val colors = listOf(
                0, // Default
                0xFFE57373.toInt(), // Red
                0xFFFFB74D.toInt(), // Orange
                0xFFFFF176.toInt(), // Yellow
                0xFF81C784.toInt(), // Green
                0xFF4DB6AC.toInt(), // Teal
                0xFF64B5F6.toInt(), // Blue
                0xFF9575CD.toInt(), // Purple
                0xFFF06292.toInt(), // Pink
                0xFFA1887F.toInt()  // Brown
            )

            LaunchedEffect(note) {
                note?.let {
                    title = it.title ?: ""
                    contents = it.contents ?: ""
                    selectedColor = it.color
                }
            }

            AppTheme {
                val surfaceColor = if (selectedColor != 0) Color(selectedColor) else MaterialTheme.colorScheme.surface
                val onSurfaceColor = if (selectedColor != 0) Color.White else MaterialTheme.colorScheme.onSurface

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(id = R.string.tab_note), color = onSurfaceColor) },
                            navigationIcon = {
                                IconButton(onClick = { finishActivityWithTransition() }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = onSurfaceColor)
                                }
                            },
                            actions = {
                                IconButton(onClick = { showColorPicker = !showColorPicker }) {
                                    Icon(Icons.Default.ColorLens, contentDescription = "Color", tint = onSurfaceColor)
                                }
                                IconButton(onClick = {
                                    viewModel.saveNote(title, contents, selectedColor)
                                    finishActivityWithTransition()
                                }) {
                                    Icon(Icons.Default.Check, contentDescription = "Save", tint = onSurfaceColor)
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = if (selectedColor != 0) surfaceColor else MaterialTheme.colorScheme.primary,
                                titleContentColor = onSurfaceColor,
                                navigationIconContentColor = onSurfaceColor,
                                actionIconContentColor = onSurfaceColor
                            )
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        if (showColorPicker) {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                LazyRow(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(colors) { colorInt ->
                                        val color = if (colorInt == 0) Color.Gray.copy(alpha = 0.3f) else Color(colorInt)
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                                .clickable {
                                                    selectedColor = colorInt
                                                    showColorPicker = false
                                                }
                                                .padding(4.dp)
                                        ) {
                                            if (selectedColor == colorInt) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.align(Alignment.Center)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            colors = CardDefaults.cardColors(containerColor = surfaceColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                TextField(
                                    value = title,
                                    onValueChange = { title = it },
                                    placeholder = { Text(stringResource(id = R.string.note_title_hint), color = onSurfaceColor.copy(alpha = 0.5f)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = LocalTextStyle.current.copy(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = onSurfaceColor
                                    ),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = if (selectedColor != 0) Color.White else MaterialTheme.colorScheme.primary,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        cursorColor = onSurfaceColor
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                TextField(
                                    value = contents,
                                    onValueChange = { contents = it },
                                    placeholder = { Text(stringResource(id = R.string.note_contents_hint), color = onSurfaceColor.copy(alpha = 0.5f)) },
                                    modifier = Modifier.fillMaxSize(),
                                    textStyle = LocalTextStyle.current.copy(
                                        fontSize = 16.sp,
                                        color = onSurfaceColor
                                    ),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        cursorColor = onSurfaceColor
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
