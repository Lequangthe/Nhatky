package com.quangthe.nhatky.ui.features.task

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import com.quangthe.nhatky.ui.base.EasyDiaryComposeBaseActivity
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.quangthe.nhatky.ui.theme.AppTheme
import com.quangthe.nhatky.viewmodels.TaskViewModel

class TodoTaskActivity : EasyDiaryComposeBaseActivity() {
    private val viewModel: TaskViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sequence = intent.getIntExtra("sequence", -1)
        viewModel.loadTask(sequence)

        setContent {
            val task by viewModel.task.collectAsState()
            var title by remember { mutableStateOf("") }
            var items by remember { mutableStateOf(listOf(Pair("", false))) }
            var priority by remember { mutableIntStateOf(0) }

            LaunchedEffect(task) {
                task?.let {
                    title = it.title ?: ""
                    items = it.items.map { item -> Pair(item.text, item.isChecked) }.ifEmpty { listOf(Pair("", false)) }
                    priority = it.priority
                }
            }

            AppTheme {
                TaskScreen(
                    title = title,
                    onTitleChange = { title = it },
                    items = items,
                    onItemsChange = { items = it },
                    priority = priority,
                    onPriorityChange = { priority = it },
                    onSave = {
                        viewModel.saveTask(title, items.filter { it.first.isNotEmpty() || it.second }, priority)
                        finishActivityWithTransition()
                    },
                    onBack = { finishActivityWithTransition() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskScreen(
    title: String,
    onTitleChange: (String) -> Unit,
    items: List<Pair<String, Boolean>>,
    onItemsChange: (List<Pair<String, Boolean>>) -> Unit,
    priority: Int,
    onPriorityChange: (Int) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
) {
    val priorityColor = when (priority) {
        3 -> Color(0xFFEF4444)
        2 -> Color(0xFFF97316)
        1 -> Color(0xFF22C55E)
        else -> Color(0xFF9CA3AF)
    }
    val priorityLabel = when (priority) {
        3 -> "High"
        2 -> "Medium"
        1 -> "Low"
        else -> ""
    }
    val checkedCount = items.count { it.second }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(onClick = onSave) {
                        Icon(Icons.Filled.Check, contentDescription = "Save", tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Save", color = Color.White, fontWeight = FontWeight.Medium)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(priorityColor.copy(alpha = 0.12f), Color(0xFFF8FAFC)),
                            startY = 0f,
                            endY = 600f
                        )
                    )
            ) {
                Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        listOf(0, 1, 2, 3).forEach { p ->
                            val pColor = when (p) {
                                3 -> Color(0xFFEF4444)
                                2 -> Color(0xFFF97316)
                                1 -> Color(0xFF22C55E)
                                else -> Color(0xFFD1D5DB)
                            }
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(if (priority == p) pColor else pColor.copy(alpha = 0.2f))
                                    .pointerInput(Unit) { detectTapGestures { onPriorityChange(p) } },
                                contentAlignment = Alignment.Center
                            ) {
                                if (p >= 2) {
                                    Text("!", color = if (priority == p) Color.White else pColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                } else if (p == 1) {
                                    Canvas(Modifier.size(10.dp)) {
                                        drawCircle(Color.White, size.width / 2)
                                    }
                                }
                            }
                            Spacer(Modifier.width(6.dp))
                        }
                        Spacer(Modifier.weight(1f))
                        if (priority > 0) {
                            Text(priorityLabel, color = priorityColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(priorityColor)
                        )
                        Spacer(Modifier.width(12.dp))
                        BasicTextField(
                            value = title,
                            onValueChange = { if (!it.contains("\n")) onTitleChange(it) },
                            textStyle = MaterialTheme.typography.headlineSmall.copy(color = Color(0xFF1E293B), textAlign = androidx.compose.ui.text.style.TextAlign.Start),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { inner ->
                                Box {
                                    if (title.isEmpty()) Text("What needs to be done?", style = MaterialTheme.typography.headlineSmall.copy(color = Color(0xFFCBD5E1), textAlign = androidx.compose.ui.text.style.TextAlign.Start))
                                    inner()
                                }
                            }
                        )
                    }
                    if (items.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "$checkedCount / ${items.size} done",
                            color = if (checkedCount == items.size) Color(0xFF22C55E) else Color(0xFF94A3B8),
                            fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 20.dp)
                        )
                    }
                }
            }

            Checklist(items = items, onItemsChange = onItemsChange)
        }
    }
}

@Composable
private fun Checklist(
    items: List<Pair<String, Boolean>>,
    onItemsChange: (List<Pair<String, Boolean>>) -> Unit,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(items.size) {
        if (items.isNotEmpty()) {
            listState.animateScrollToItem(items.size - 1)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        itemsIndexed(items) { index, item ->
            val prevText = items.getOrNull(index - 1)?.first
            ChecklistRow(
                text = item.first,
                isChecked = item.second,
                showDivider = index > 0 && prevText?.isNotEmpty() == true,
                onTextChange = { newText ->
                    val mutable = items.toMutableList()
                    if (newText.contains("\n")) {
                        val parts = newText.split("\n", limit = 2)
                        mutable[index] = Pair(parts[0], item.second)
                        mutable.add(index + 1, Pair(parts[1], false))
                        onItemsChange(mutable)
                    } else {
                        mutable[index] = Pair(newText, item.second)
                        if (index == items.lastIndex && newText.isNotEmpty()) {
                            mutable.add(Pair("", false))
                        }
                        onItemsChange(mutable)
                    }
                },
                onCheck = {
                    val mutable = items.toMutableList()
                    mutable[index] = Pair(item.first, !item.second)
                    onItemsChange(mutable)
                },
                onDelete = {
                    val mutable = items.toMutableList()
                    mutable.removeAt(index)
                    if (mutable.isEmpty()) mutable.add(Pair("", false))
                    onItemsChange(mutable)
                }
            )
        }
    }
}

@Composable
private fun ChecklistRow(
    text: String,
    isChecked: Boolean,
    showDivider: Boolean,
    onTextChange: (String) -> Unit,
    onCheck: () -> Unit,
    onDelete: () -> Unit,
) {
    val isFocused by remember { mutableStateOf(false) }
    val bgAlpha by animateFloatAsState(
        targetValue = if (isChecked) 0.5f else 1f,
        animationSpec = tween(300), label = "bg"
    )

    Column {
        if (showDivider) {
            Spacer(Modifier.height(2.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 44.dp)
                    .height(0.5.dp)
                    .background(Color(0xFFE2E8F0))
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .background(Color.White.copy(alpha = bgAlpha), RoundedCornerShape(10.dp))
                .padding(horizontal = 6.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(if (isChecked) Color(0xFF22C55E) else Color(0xFFF1F5F9))
                    .pointerInput(Unit) { detectTapGestures { onCheck() } },
                contentAlignment = Alignment.Center
            ) {
                if (isChecked) {
                    Canvas(Modifier.size(13.dp)) {
                        val p = Path().apply {
                            moveTo(size.width * 0.2f, size.height * 0.52f)
                            lineTo(size.width * 0.42f, size.height * 0.75f)
                            lineTo(size.width * 0.8f, size.height * 0.28f)
                        }
                        drawPath(p, color = Color.White, style = Stroke(width = 2.5f, cap = StrokeCap.Round))
                    }
                } else {
                    Canvas(Modifier.size(13.dp)) {
                        if (text.isEmpty()) {
                            drawCircle(Color(0xFFCBD5E1), size.width / 2)
                        } else {
                            drawCircle(Color(0xFFE2E8F0), size.width / 2)
                        }
                    }
                }
            }
            Spacer(Modifier.width(10.dp))
            Box(Modifier.weight(1f)) {
                BasicTextField(
                    value = text,
                    onValueChange = { onTextChange(it) },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = if (isChecked) Color(0xFF94A3B8) else Color(0xFF334155),
                        textDecoration = if (isChecked) TextDecoration.LineThrough else null
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        Box {
                            if (text.isEmpty()) Text("Type something...", color = Color(0xFFCBD5E1), style = MaterialTheme.typography.bodyLarge)
                            inner()
                        }
                    }
                )
            }
            AnimatedVisibility(visible = !isChecked && text.isNotEmpty()) {
                IconButton(onClick = onDelete, modifier = Modifier.size(26.dp)) {
                    Icon(Icons.Filled.Close, contentDescription = "Delete", tint = Color(0xFFCBD5E1), modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}
