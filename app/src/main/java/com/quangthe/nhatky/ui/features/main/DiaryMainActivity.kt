package com.quangthe.nhatky.ui.features.main

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.lifecycleScope
import com.simplemobiletools.commons.extensions.toast
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.quangthe.nhatky.R
import com.quangthe.nhatky.ui.base.EasyDiaryComposeBaseActivity
import com.quangthe.nhatky.ui.features.note.SimpleNoteActivity
import com.quangthe.nhatky.ui.features.task.TodoTaskActivity
import com.quangthe.nhatky.ui.features.diary.DiaryDetailActivity
import com.quangthe.nhatky.ui.features.diary.TimelineActivity
import com.quangthe.nhatky.ui.features.diary.TreeTimelineActivity
import com.quangthe.nhatky.ui.features.settings.SettingsActivity
import com.quangthe.nhatky.enums.DiaryEntryType
import com.quangthe.nhatky.extensions.*
import com.quangthe.nhatky.core.config.ComposeConstants.HORIZONTAL_PADDING
import com.quangthe.nhatky.core.config.ComposeConstants.VERTICAL_PADDING
import com.quangthe.nhatky.core.config.DIARY_SEQUENCE
import com.quangthe.nhatky.core.navigation.TransitionHelper
import com.quangthe.nhatky.models.Diary
import com.quangthe.nhatky.models.TodoTask
import com.quangthe.nhatky.ui.components.*
import com.quangthe.nhatky.ui.models.DiaryMainItem
import com.quangthe.nhatky.ui.theme.AppTheme
import com.quangthe.nhatky.viewmodels.DiaryMainViewModel
import com.quangthe.nhatky.viewmodels.SettingsViewModel

class DiaryMainActivity : EasyDiaryComposeBaseActivity() {
    private val viewModel: DiaryMainViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    @OptIn(
        ExperimentalMaterial3Api::class,
        ExperimentalLayoutApi::class,
        ExperimentalMaterial3WindowSizeClassApi::class,
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(getThemeId())
        super.onCreate(savedInstanceState)

        setContent {
            val currentQuery: String by viewModel.currentQuery.collectAsState()
            val items: List<DiaryMainItem> by viewModel.diaryItems.collectAsState()
            val entryType by viewModel.entryType.collectAsState()
            val isTimelineMode by viewModel.isTimelineMode.collectAsState()
            var showTaskDialog by remember { mutableStateOf(false) }
            var editingTask by remember { mutableStateOf<TodoTask?>(null) }
            val focusManager = LocalFocusManager.current
            val isKeyboardVisible = WindowInsets.isImeVisible
            val windowSizeClass = calculateWindowSizeClass(this)

            LaunchedEffect(isKeyboardVisible) {
                if (!isKeyboardVisible) {
                    focusManager.clearFocus()
                }
            }

            LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
                viewModel.setTimelineMode(config.diaryViewMode == 1)
            }

            LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                viewModel.findDiary()
            }

            LaunchedEffect(Unit) {
                handleIntent(intent)
            }

            AppTheme {
                enableEdgeToEdge()
                applyFullScreenStatusBarTheme()
                updateNavigationBarAppearance()
                
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    topBar = {
                        Column(modifier = Modifier.background(MaterialTheme.colorScheme.primary)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, top = 40.dp, bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextField(
                                    value = currentQuery,
                                    onValueChange = { viewModel.findDiary(it) },
                                    placeholder = { Text(stringResource(id = R.string.guide_message_2), color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f),
                                        unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        cursorColor = MaterialTheme.colorScheme.onPrimary,
                                        focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    shape = RoundedCornerShape(24.dp),
                                    trailingIcon = {
                                        if (currentQuery.isNotEmpty()) {
                                            IconButton(onClick = { viewModel.findDiary("") }) {
                                                Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                                            }
                                        }
                                    }
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = {
                                    TransitionHelper.startActivityWithTransition(this@DiaryMainActivity, Intent(this@DiaryMainActivity, TreeTimelineActivity::class.java))
                                }) {
                                    Icon(imageVector = Icons.Default.AccountTree, contentDescription = "Tree View", tint = MaterialTheme.colorScheme.onPrimary)
                                }
                                IconButton(onClick = {
                                    TransitionHelper.startActivityWithTransition(this@DiaryMainActivity, Intent(this@DiaryMainActivity, TimelineActivity::class.java))
                                }) {
                                    Icon(Icons.Default.DateRange, contentDescription = "Timeline", tint = MaterialTheme.colorScheme.onPrimary)
                                }
                                IconButton(onClick = {
                                    TransitionHelper.startActivityWithTransition(this@DiaryMainActivity, Intent(this@DiaryMainActivity, SettingsActivity::class.java))
                                }) {
                                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onPrimary)
                                }
                            }
                            TabRow(
                                selectedTabIndex = entryType.value,
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                indicator = { tabPositions ->
                                    TabRowDefaults.SecondaryIndicator(
                                        Modifier.tabIndicatorOffset(tabPositions[entryType.value]),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                },
                                divider = {}
                            ) {
                                Tab(
                                    selected = entryType == DiaryEntryType.DIARY,
                                    onClick = { viewModel.setEntryType(DiaryEntryType.DIARY) },
                                    text = { Text(stringResource(id = R.string.tab_diary), color = MaterialTheme.colorScheme.onPrimary) }
                                )
                                Tab(
                                    selected = entryType == DiaryEntryType.NOTE,
                                    onClick = { viewModel.setEntryType(DiaryEntryType.NOTE) },
                                    text = { Text(stringResource(id = R.string.tab_note), color = MaterialTheme.colorScheme.onPrimary) }
                                )
                                Tab(
                                    selected = entryType == DiaryEntryType.TASK,
                                    onClick = { viewModel.setEntryType(DiaryEntryType.TASK) },
                                    text = { Text(stringResource(id = R.string.tab_task), color = MaterialTheme.colorScheme.onPrimary) }
                                )
                            }
                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                when (entryType) {
                                    DiaryEntryType.TASK -> {
                                        editingTask = TodoTask()
                                        showTaskDialog = true
                                    }
                                    DiaryEntryType.NOTE -> {
                                        val intent = Intent(this@DiaryMainActivity, SimpleNoteActivity::class.java)
                                        TransitionHelper.startActivityWithTransition(this@DiaryMainActivity, intent)
                                    }
                                    else -> {
                                        val intent = Intent(this@DiaryMainActivity, DiaryDetailActivity::class.java)
                                        TransitionHelper.startActivityWithTransition(this@DiaryMainActivity, intent)
                                    }
                                }
                            },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                        }
                    },
                    content = { innerPadding ->
                        val listState = rememberLazyListState()
                        var thumbVisible by remember { mutableStateOf(false) }
                        var containerSize by remember { mutableStateOf(IntSize.Zero) }
                        var isDraggingThumb by remember { mutableStateOf(false) }
                        var hideJob: Job? by remember { mutableStateOf(null) }
                        val delayTimeMillis = 500L

                        LaunchedEffect(listState) {
                            snapshotFlow { listState.isScrollInProgress }
                                .collect { isScrolling ->
                                    if (isScrolling) {
                                        hideJob?.cancel()
                                        thumbVisible = true
                                    } else {
                                        hideJob?.cancel()
                                        hideJob = launch {
                                            delay(delayTimeMillis)
                                            if (!isDraggingThumb) thumbVisible = false
                                        }
                                    }
                                }
                        }

                        fun itemLongClickCallback() {
                            toast("itemLongClickCallback")
                        }

                        Box(modifier = Modifier.padding(innerPadding)) {
                            when (windowSizeClass.widthSizeClass) {
                                WindowWidthSizeClass.Compact -> {
                                    CompactLayout(
                                        modifier = Modifier,
                                        listState = listState,
                                        containerSize = containerSize,
                                        isDraggingThumb = isDraggingThumb,
                                        thumbVisible = thumbVisible,
                                        hideJob = hideJob,
                                        items = items,
                                        isTimelineMode = isTimelineMode,
                                        itemLongClickCallback = { itemLongClickCallback() },
                                        thumbVisibleCallback = { thumbVisible = it },
                                        isDraggingThumbCallback = { isDraggingThumb = it },
                                        hideJobCallback = { hideJob = it },
                                        containerSizeCallback = { containerSize = it },
                                        onTaskClick = { editingTask = it; showTaskDialog = true },
                                    )
                                }
                                else -> {
                                    MediumLayout(
                                        modifier = Modifier,
                                        listState = listState,
                                        containerSize = containerSize,
                                        isDraggingThumb = isDraggingThumb,
                                        thumbVisible = thumbVisible,
                                        hideJob = hideJob,
                                        items = items,
                                        isTimelineMode = isTimelineMode,
                                        itemLongClickCallback = { itemLongClickCallback() },
                                        thumbVisibleCallback = { thumbVisible = it },
                                        isDraggingThumbCallback = { isDraggingThumb = it },
                                        hideJobCallback = { hideJob = it },
                                        containerSizeCallback = { containerSize = it },
                                        onTaskClick = { editingTask = it; showTaskDialog = true },
                                    )
                                }
                            }
                        }
                    },
                )

                if (showTaskDialog && editingTask != null) {
                    TaskEditDialog(
                        task = editingTask!!,
                        onDismiss = { showTaskDialog = false; editingTask = null },
                        onSave = { task ->
                            viewModel.saveTask(task)
                            showTaskDialog = false
                            editingTask = null
                        }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return
        val action = intent.action
        val type = intent.type

        if (Intent.ACTION_SEND == action && type != null) {
            val detailIntent = Intent(this, DiaryDetailActivity::class.java)
            detailIntent.putExtra(DIARY_SEQUENCE, -1)
            
            if ("text/plain" == type) {
                val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                if (sharedText?.contains("maps.google.com") == true || sharedText?.contains("goo.gl/maps") == true || sharedText?.contains("maps.app.goo.gl") == true) {
                    detailIntent.putExtra("shared_location_text", sharedText)
                } else {
                    detailIntent.putExtra("shared_text", sharedText)
                }
            } else if (type.startsWith("image/") || type.startsWith("video/") || type.startsWith("audio/")) {
                if (Build.VERSION.SDK_INT >= 33) {
                    intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)?.let { uri ->
                        detailIntent.putExtra("shared_uris", arrayListOf(uri))
                    }
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)?.let { uri ->
                        detailIntent.putExtra("shared_uris", arrayListOf(uri))
                    }
                }
            }
            TransitionHelper.startActivityWithTransition(this, detailIntent)
            intent.action = "" 
        } else if (Intent.ACTION_SEND_MULTIPLE == action && type != null) {
            val detailIntent = Intent(this, DiaryDetailActivity::class.java)
            detailIntent.putExtra(DIARY_SEQUENCE, -1)
            
            if (type.startsWith("image/") || type.startsWith("video/") || type.startsWith("audio/")) {
                val uris = if (Build.VERSION.SDK_INT >= 33) {
                    intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM, Uri::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)
                }
                if (uris != null) {
                    detailIntent.putExtra("shared_uris", uris)
                }
            }
            TransitionHelper.startActivityWithTransition(this, detailIntent)
            intent.action = ""
        }
    }

    @Composable
    fun CompactLayout(
        modifier: Modifier,
        listState: LazyListState,
        containerSize: IntSize,
        isDraggingThumb: Boolean,
        thumbVisible: Boolean,
        hideJob: Job?,
        items: List<DiaryMainItem>,
        isTimelineMode: Boolean = false,
        thumbVisibleCallback: (Boolean) -> Unit,
        isDraggingThumbCallback: (Boolean) -> Unit,
        hideJobCallback: (Job) -> Unit,
        containerSizeCallback: (IntSize) -> Unit,
        itemLongClickCallback: () -> Unit,
        onTaskClick: (TodoTask) -> Unit = {},
    ) {
        Column(modifier = modifier.fillMaxSize()) {
            val noteFolderStack by viewModel.noteFolderStack.collectAsState()
            if (noteFolderStack.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(id = R.string.tab_note),
                        color = Color(config.primaryColor),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { viewModel.leaveNoteFolder() },
                    )
                    noteFolderStack.forEach { folder ->
                        Text(text = " › ", color = Color(config.textColor).copy(alpha = 0.4f), fontSize = 12.sp)
                        Text(text = folder.name, color = Color(config.textColor).copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                if (items.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = when (viewModel.entryType.collectAsState().value) {
                                DiaryEntryType.NOTE -> stringResource(id = R.string.no_note_message)
                                DiaryEntryType.TASK -> stringResource(id = R.string.no_task_message)
                                else -> stringResource(id = R.string.no_diary_message)
                            },
                            color = Color(config.textColor).copy(alpha = 0.5f),
                            fontSize = 16.sp
                        )
                    }
                }
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().onSizeChanged { containerSizeCallback(it) },
                    contentPadding = PaddingValues(bottom = 80.dp),
                ) {
                    itemsIndexed(items) { index, item ->
                        val padding = if (item is DiaryMainItem.TaskEntry || item is DiaryMainItem.Header) {
                            Modifier.padding(horizontal = 16.dp, vertical = 0.dp)
                        } else {
                            Modifier.padding(HORIZONTAL_PADDING.dp, VERTICAL_PADDING.dp)
                        }
                        Box(modifier = padding) {
                            when (item) {
                                is DiaryMainItem.DiaryEntry -> DiaryItemCard(
                                    diary = item.diary,
                                    isTimelineMode = isTimelineMode,
                                    isLast = index == items.size - 1,
                                    itemClickCallback = {
                                        val intent = Intent(this@DiaryMainActivity, DiaryDetailActivity::class.java)
                                        intent.putExtra(DIARY_SEQUENCE, it.sequence)
                                        TransitionHelper.startActivityWithTransition(this@DiaryMainActivity, intent)
                                    },
                                    itemLongClickCallback = itemLongClickCallback,
                                    onDeleteClick = { viewModel.deleteItem(item) }
                                )
                                is DiaryMainItem.NoteEntry -> NoteItemCard(
                                    note = item.note,
                                    itemClickCallback = {
                                        val intent = Intent(this@DiaryMainActivity, SimpleNoteActivity::class.java)
                                        intent.putExtra("sequence", it.sequence)
                                        TransitionHelper.startActivityWithTransition(this@DiaryMainActivity, intent)
                                    },
                                    itemLongClickCallback = itemLongClickCallback,
                                    onDeleteClick = { viewModel.deleteItem(item) }
                                )
                                is DiaryMainItem.NoteFolderEntry -> NoteFolderCard(
                                    folder = item.folder,
                                    itemClickCallback = { viewModel.enterNoteFolder(it) },
                                    itemLongClickCallback = itemLongClickCallback
                                )
                                is DiaryMainItem.TaskEntry -> TaskItemCard(
                                    task = item.task,
                                    itemClickCallback = { onTaskClick(it) },
                                    itemLongClickCallback = itemLongClickCallback,
                                    toggleTaskCallback = { viewModel.toggleTask(it) },
                                    onDeleteClick = { viewModel.deleteItem(item) }
                                )
                                is DiaryMainItem.Header -> SectionHeader(
                                    title = stringResource(id = item.titleRes),
                                    count = item.count
                                )
                            }
                        }
                    }
                }

                FastScroll(
                    items = items,
                    listState = listState,
                    containerHeightPx = containerSize.height.toFloat(),
                    isDraggingThumb = isDraggingThumb,
                    thumbVisible = thumbVisible,
                    containerSize = containerSize,
                    modifier = Modifier.align(Alignment.TopEnd),
                    showDebugCard = false,
                    updateThumbVisible = { thumbVisibleCallback(it) },
                    updateDraggingThumb = { isDraggingThumbCallback(it) },
                    dragEndCallback = {
                        hideJob?.cancel()
                        lifecycleScope.launch {
                            val job = launch {
                                delay(500L)
                                if (!isDraggingThumb) thumbVisibleCallback(false)
                            }
                            hideJobCallback(job)
                        }
                    },
                )
            }
        }
    }

    @Composable
    fun MediumLayout(
        modifier: Modifier,
        listState: LazyListState,
        containerSize: IntSize,
        isDraggingThumb: Boolean,
        thumbVisible: Boolean,
        hideJob: Job?,
        items: List<DiaryMainItem>,
        isTimelineMode: Boolean = false,
        thumbVisibleCallback: (Boolean) -> Unit,
        isDraggingThumbCallback: (Boolean) -> Unit,
        hideJobCallback: (Job) -> Unit,
        containerSizeCallback: (IntSize) -> Unit,
        itemLongClickCallback: () -> Unit,
        onTaskClick: (TodoTask) -> Unit = {},
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = modifier.weight(0.4f).fillMaxSize()) {}
            Column(modifier = Modifier.weight(0.6f)) {
                val noteFolderStack by viewModel.noteFolderStack.collectAsState()
                if (noteFolderStack.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(id = R.string.tab_note),
                            color = Color(config.primaryColor),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable { viewModel.leaveNoteFolder() },
                        )
                        noteFolderStack.forEach { folder ->
                            Text(text = " › ", color = Color(config.textColor).copy(alpha = 0.4f), fontSize = 12.sp)
                            Text(text = folder.name, color = Color(config.textColor).copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    if (items.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = when (viewModel.entryType.collectAsState().value) {
                                    DiaryEntryType.NOTE -> stringResource(id = R.string.no_note_message)
                                    DiaryEntryType.TASK -> stringResource(id = R.string.no_task_message)
                                    else -> stringResource(id = R.string.no_diary_message)
                                },
                                color = Color(config.textColor).copy(alpha = 0.5f),
                                fontSize = 16.sp
                            )
                        }
                    }
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize().onSizeChanged { containerSizeCallback(it) },
                        contentPadding = PaddingValues(bottom = 80.dp),
                    ) {
                        itemsIndexed(items) { index, item ->
                            val padding = if (item is DiaryMainItem.TaskEntry || item is DiaryMainItem.Header) {
                                Modifier.padding(horizontal = 16.dp, vertical = 0.dp)
                            } else {
                                Modifier.padding(HORIZONTAL_PADDING.dp, VERTICAL_PADDING.dp)
                            }
                            
                            Box(modifier = padding) {
                                when (item) {
                                    is DiaryMainItem.DiaryEntry -> DiaryItemCard(
                                        diary = item.diary,
                                        isTimelineMode = isTimelineMode,
                                        isLast = index == items.size - 1,
                                        itemClickCallback = {
                                            val intent = Intent(this@DiaryMainActivity, DiaryDetailActivity::class.java)
                                            intent.putExtra(DIARY_SEQUENCE, it.sequence)
                                            TransitionHelper.startActivityWithTransition(this@DiaryMainActivity, intent)
                                        },
                                        itemLongClickCallback = itemLongClickCallback,
                                        onDeleteClick = { viewModel.deleteItem(item) }
                                    )
                                    is DiaryMainItem.NoteEntry -> NoteItemCard(
                                        note = item.note,
                                        itemClickCallback = {
                                            val intent = Intent(this@DiaryMainActivity, SimpleNoteActivity::class.java)
                                            intent.putExtra("sequence", it.sequence)
                                            TransitionHelper.startActivityWithTransition(this@DiaryMainActivity, intent)
                                        },
                                        itemLongClickCallback = itemLongClickCallback,
                                        onDeleteClick = { viewModel.deleteItem(item) }
                                    )
                                    is DiaryMainItem.NoteFolderEntry -> NoteFolderCard(
                                        folder = item.folder,
                                        itemClickCallback = { viewModel.enterNoteFolder(it) },
                                        itemLongClickCallback = itemLongClickCallback
                                    )
                                    is DiaryMainItem.TaskEntry -> TaskItemCard(
                                        task = item.task,
                                        itemClickCallback = { onTaskClick(it) },
                                        itemLongClickCallback = itemLongClickCallback,
                                        toggleTaskCallback = { viewModel.toggleTask(it) },
                                        onDeleteClick = { viewModel.deleteItem(item) }
                                    )
                                    is DiaryMainItem.Header -> SectionHeader(
                                        title = stringResource(id = item.titleRes),
                                        count = item.count
                                    )
                                }
                            }
                        }
                    }

                    FastScroll(
                        items = items,
                        listState = listState,
                        containerHeightPx = containerSize.height.toFloat(),
                        isDraggingThumb = isDraggingThumb,
                        thumbVisible = thumbVisible,
                        containerSize = containerSize,
                        modifier = Modifier.align(Alignment.TopEnd),
                        showDebugCard = false,
                        updateThumbVisible = { thumbVisibleCallback(it) },
                        updateDraggingThumb = { isDraggingThumbCallback(it) },
                        dragEndCallback = {
                            hideJob?.cancel()
                            lifecycleScope.launch {
                                val job = launch {
                                    delay(500L)
                                    if (!isDraggingThumb) thumbVisibleCallback(false)
                                }
                                hideJobCallback(job)
                            }
                        },
                    )
                }
            }
        }
    }
}
