package com.quangthe.nhatky.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.quangthe.nhatky.models.TodoTask

@Composable
fun TaskEditDialog(
    task: TodoTask,
    onDismiss: () -> Unit,
    onSave: (TodoTask) -> Unit,
) {
    var title by remember { mutableStateOf(task.title ?: "") }
    var items by remember { mutableStateOf(task.items.map { Pair(it.text, it.isChecked) }.ifEmpty { listOf(Pair("", false)) }.toMutableList()) }
    var priority by remember { mutableIntStateOf(task.priority) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (task.sequence > 0) "Edit Task" else "New Task",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(Modifier.height(16.dp))

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
                                .pointerInput(Unit) { detectTapGestures { priority = p } },
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
                }

                Spacer(Modifier.height(12.dp))

                BasicTextField(
                    value = title,
                    onValueChange = { if (!it.contains("\n")) title = it },
                    textStyle = MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        Box {
                            if (title.isEmpty()) Text(
                                "What needs to be done?",
                                style = MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            inner()
                        }
                    }
                )

                if (items.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    val checkedCount = items.count { it.second }
                    Text(
                        "$checkedCount / ${items.size} done",
                        color = if (checkedCount == items.size && checkedCount > 0) Color(0xFF22C55E) else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(Modifier.height(8.dp))

                Box(modifier = Modifier.heightIn(max = 300.dp)) {
                    val listState = rememberLazyListState()
                    var focusedIndex by remember { mutableIntStateOf(-1) }
                    val focusRequesters = remember { mutableStateMapOf<Int, FocusRequester>() }

                    LaunchedEffect(items.size) {
                        if (items.isNotEmpty() && focusedIndex == -1) {
                            listState.animateScrollToItem(items.size - 1)
                        }
                    }

                    LaunchedEffect(focusedIndex) {
                        if (focusedIndex != -1) {
                            focusRequesters[focusedIndex]?.requestFocus()
                            focusedIndex = -1 // Reset after requesting
                        }
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        itemsIndexed(items) { index, item ->
                            val prevText = items.getOrNull(index - 1)?.first
                            val focusRequester = focusRequesters.getOrPut(index) { FocusRequester() }

                            ChecklistRow(
                                text = item.first,
                                isChecked = item.second,
                                showDivider = index > 0 && prevText?.isNotEmpty() == true,
                                focusRequester = focusRequester,
                                onTextChange = { newText ->
                                    val mutable = items.toMutableList()
                                    if (newText.contains("\n")) {
                                        val parts = newText.split("\n", limit = 2)
                                        mutable[index] = Pair(parts[0], item.second)
                                        mutable.add(index + 1, Pair(parts[1], false))
                                        items = mutable
                                        focusedIndex = index + 1
                                    } else {
                                        mutable[index] = Pair(newText, item.second)
                                        if (index == items.lastIndex && newText.isNotEmpty()) {
                                            mutable.add(Pair("", false))
                                        }
                                        items = mutable
                                    }
                                },
                                onCheck = {
                                    val mutable = items.toMutableList()
                                    mutable[index] = Pair(item.first, !item.second)
                                    items = mutable
                                },
                                onDelete = {
                                    val mutable = items.toMutableList()
                                    mutable.removeAt(index)
                                    if (mutable.isEmpty()) mutable.add(Pair("", false))
                                    items = mutable
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        val saved = TodoTask(
                            sequence = task.sequence,
                            title = title,
                            priority = priority,
                            items = items.filter { it.first.isNotEmpty() || it.second }
                                .map { com.quangthe.nhatky.models.TodoItem(it.first, it.second) }
                                .toMutableList(),
                            createdAt = task.createdAt
                        )
                        onSave(saved)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun ChecklistRow(
    text: String,
    isChecked: Boolean,
    showDivider: Boolean,
    focusRequester: FocusRequester,
    onTextChange: (String) -> Unit,
    onCheck: () -> Unit,
    onDelete: () -> Unit,
) {
    Column {
        if (showDivider) {
            Spacer(Modifier.height(2.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 44.dp)
                    .height(0.5.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .padding(horizontal = 6.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(if (isChecked) Color(0xFF22C55E) else MaterialTheme.colorScheme.surfaceVariant)
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
                    val circleColor = if (text.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outlineVariant
                    Canvas(Modifier.size(13.dp)) {
                        drawCircle(circleColor, size.width / 2)
                    }
                }
            }
            Spacer(Modifier.width(10.dp))
            Box(Modifier.weight(1f)) {
                BasicTextField(
                    value = text,
                    onValueChange = { onTextChange(it) },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = if (isChecked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (isChecked) TextDecoration.LineThrough else null
                    ),
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    decorationBox = { inner ->
                        Box {
                            if (text.isEmpty()) Text(
                                "Type something...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            inner()
                        }
                    }
                )
            }
            if (!isChecked && text.isNotEmpty()) {
                IconButton(onClick = onDelete, modifier = Modifier.size(26.dp)) {
                    Icon(Icons.Filled.Close, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}
