package com.quangthe.nhatky.ui.features.diary

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quangthe.nhatky.R
import com.quangthe.nhatky.commons.utils.DateUtils
import com.quangthe.nhatky.ui.base.EasyDiaryComposeBaseActivity
import com.quangthe.nhatky.ui.features.diary.DiaryDetailActivity
import com.quangthe.nhatky.extensions.config
import com.quangthe.nhatky.extensions.getThemeId
import com.quangthe.nhatky.core.config.DIARY_SEQUENCE
import com.quangthe.nhatky.core.navigation.TransitionHelper
import com.quangthe.nhatky.models.Diary
import com.quangthe.nhatky.repositories.DiaryRepository
import com.quangthe.nhatky.ui.theme.AppTheme
import java.util.Calendar

class TimelineActivity : EasyDiaryComposeBaseActivity() {
    private val diaryRepository = DiaryRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(getThemeId())
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme { TimelineScreen() }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TimelineScreen() {
        val context = LocalContext.current
        val primaryColor = MaterialTheme.colorScheme.primary
        var diaries by remember { mutableStateOf(emptyList<Diary>()) }
        var filterVisible by remember { mutableStateOf(false) }
        var searchQuery by remember { mutableStateOf("") }
        var startMillis by remember { mutableStateOf(0L) }
        var endMillis by remember { mutableStateOf(0L) }
        var showStartPicker by remember { mutableStateOf(false) }
        var showEndPicker by remember { mutableStateOf(false) }

        LaunchedEffect(searchQuery, startMillis, endMillis) {
            diaries = if (startMillis > 0L && endMillis > 0L) {
                diaryRepository.findDiary(
                    searchQuery.ifEmpty { null },
                    false, startMillis, endMillis
                )
            } else {
                diaryRepository.findDiary(
                    searchQuery.ifEmpty { null },
                    checkFutureDiaryOption = true
                )
            }
            diaries = diaries.sortedBy { it.currentTimeMillis }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.timeline_title)) },
                    navigationIcon = {
                        IconButton(onClick = { finishActivityWithTransition() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { filterVisible = !filterVisible }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = primaryColor,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        val intent = Intent(this@TimelineActivity, DiaryDetailActivity::class.java)
                        TransitionHelper.startActivityWithTransition(this@TimelineActivity, intent)
                    },
                    containerColor = primaryColor
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                AnimatedVisibility(
                    visible = filterVisible,
                    enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                ) {
                    FilterPanel(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        startMillis = startMillis,
                        endMillis = endMillis,
                        onStartDateClick = { showStartPicker = true },
                        onEndDateClick = { showEndPicker = true },
                        onClear = {
                            searchQuery = ""
                            startMillis = 0L
                            endMillis = 0L
                        },
                        primaryColor = primaryColor
                    )
                }

                if (diaries.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(id = R.string.no_diary_message),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 80.dp, top = 8.dp)
                    ) {
                        itemsIndexed(
                            items = diaries,
                            key = { _, diary -> diary.sequence }
                        ) { index, diary ->
                            val showHeader = index == 0 || diary.dateString != diaries[index - 1].dateString
                            TimelineItem(
                                diary = diary,
                                showDateHeader = showHeader,
                                isLast = index == diaries.size - 1,
                                searchQuery = searchQuery,
                                primaryColor = primaryColor,
                                onClick = {
                                    val intent = Intent(this@TimelineActivity, DiaryDetailActivity::class.java)
                                    intent.putExtra(DIARY_SEQUENCE, diary.sequence)
                                    TransitionHelper.startActivityWithTransition(this@TimelineActivity, intent)
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showStartPicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = if (startMillis > 0L) startMillis else System.currentTimeMillis()
            )
            DatePickerDialog(
                onDismissRequest = { showStartPicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { startMillis = it }
                        showStartPicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showStartPicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showEndPicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = if (endMillis > 0L) endMillis else System.currentTimeMillis()
            )
            DatePickerDialog(
                onDismissRequest = { showEndPicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { endMillis = it + 86400000L - 1L }
                        showEndPicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showEndPicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
private fun FilterPanel(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    startMillis: Long,
    endMillis: Long,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit,
    onClear: () -> Unit,
    primaryColor: Color,
) {
    val startText = if (startMillis > 0L)
        DateUtils.getDateStringFromTimeMillis(startMillis) else "Start date"
    val endText = if (endMillis > 0L)
        DateUtils.getDateStringFromTimeMillis(endMillis) else "End date"

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search...") },
                singleLine = true,
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedCard(
                    modifier = Modifier.weight(1f).clickable { onStartDateClick() },
                    colors = CardDefaults.outlinedCardColors()
                ) {
                    Text(
                        text = "From: $startText",
                        modifier = Modifier.padding(12.dp),
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                OutlinedCard(
                    modifier = Modifier.weight(1f).clickable { onEndDateClick() },
                    colors = CardDefaults.outlinedCardColors()
                ) {
                    Text(
                        text = "To: $endText",
                        modifier = Modifier.padding(12.dp),
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (searchQuery.isNotEmpty() || startMillis > 0L || endMillis > 0L) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onClear,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text("Clear Filter")
                }
            }
        }
    }
}

@Composable
private fun TimelineItem(
    diary: Diary,
    showDateHeader: Boolean,
    isLast: Boolean,
    searchQuery: String,
    primaryColor: Color,
    onClick: () -> Unit,
) {
    Column {
        if (showDateHeader) {
            DateHeader(diary = diary, primaryColor = primaryColor)
        }

        Row(
            modifier = Modifier.height(IntrinsicSize.Min).padding(top = 4.dp, bottom = 4.dp)
        ) {
            TimelineIndicator(
                isLast = isLast,
                primaryColor = primaryColor,
                modifier = Modifier.width(36.dp).fillMaxHeight()
            )

            Card(
                modifier = Modifier.weight(1f).padding(end = 8.dp).clickable { onClick() },
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    val timeText = if (diary.isAllDay)
                        stringResource(id = R.string.all_day)
                    else
                        DateUtils.getTimeStringFromTimeMillis(diary.currentTimeMillis)
                    Text(
                        text = timeText,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val title = diary.title
                    val contents = diary.contents

                    if (!title.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (!contents.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        HighlightedText(
                            text = contents,
                            query = searchQuery,
                            fontSize = 14.sp,
                            maxLines = 3
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineIndicator(
    isLast: Boolean,
    primaryColor: Color,
    modifier: Modifier = Modifier,
) {
    val circleRadius = 6.dp
    val strokeWidth = 2.dp

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val topY = 0f
            val bottomY = size.height
            val circleCenterY = circleRadius.toPx() + 4.dp.toPx()

            if (!isLast) {
                drawLine(
                    color = primaryColor,
                    start = androidx.compose.ui.geometry.Offset(centerX, topY),
                    end = androidx.compose.ui.geometry.Offset(centerX, bottomY),
                    strokeWidth = strokeWidth.toPx()
                )
            } else {
                drawLine(
                    color = primaryColor,
                    start = androidx.compose.ui.geometry.Offset(centerX, topY),
                    end = androidx.compose.ui.geometry.Offset(centerX, circleCenterY + circleRadius.toPx()),
                    strokeWidth = strokeWidth.toPx()
                )
            }

            drawCircle(
                color = primaryColor,
                radius = circleRadius.toPx(),
                center = androidx.compose.ui.geometry.Offset(centerX, circleCenterY)
            )
            drawCircle(
                color = Color.White,
                radius = (circleRadius - strokeWidth / 2).toPx(),
                center = androidx.compose.ui.geometry.Offset(centerX, circleCenterY)
            )
        }
    }
}

@Composable
private fun DateHeader(
    diary: Diary,
    primaryColor: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 36.dp, top = 8.dp, bottom = 4.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = primaryColor)
        ) {
            Text(
                text = DateUtils.getDateStringFromTimeMillis(diary.currentTimeMillis),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        if (diary.currentTimeMillis > System.currentTimeMillis()) {
            val dDay = DateUtils.getOnlyDayRemaining(diary.currentTimeMillis)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = dDay,
                color = primaryColor,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun HighlightedText(
    text: String,
    query: String,
    fontSize: androidx.compose.ui.unit.TextUnit = 14.sp,
    maxLines: Int = Int.MAX_VALUE,
) {
    if (query.isBlank()) {
        Text(
            text = text,
            fontSize = fontSize,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
    } else {
        val annotated = buildAnnotatedString {
            var currentIndex = 0
            val lowerText = text.lowercase()
            val lowerQuery = query.lowercase()
            while (currentIndex < text.length) {
                val matchIndex = lowerText.indexOf(lowerQuery, currentIndex)
                if (matchIndex == -1) {
                    append(text.substring(currentIndex))
                    break
                }
                if (matchIndex > currentIndex) {
                    append(text.substring(currentIndex, matchIndex))
                }
                withStyle(
                    SpanStyle(
                        background = Color.Yellow.copy(alpha = 0.4f),
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(text.substring(matchIndex, matchIndex + query.length))
                }
                currentIndex = matchIndex + query.length
            }
        }
        Text(
            text = annotated,
            fontSize = fontSize,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
