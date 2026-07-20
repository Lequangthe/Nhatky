package com.quangthe.nhatky.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.quangthe.nhatky.commons.utils.EasyDiaryUtils.summaryDiaryLabel
import com.quangthe.nhatky.models.Diary
import com.quangthe.nhatky.ui.models.DiaryMainItem
import kotlinx.coroutines.launch

@Composable
fun FastScroll(
    items: List<Any>,
    listState: LazyListState,
    containerHeightPx: Float,
    isDraggingThumb: Boolean,
    containerSize: IntSize,
    thumbVisible: Boolean,
    showDebugCard: Boolean = false,
    modifier: Modifier,
    updateThumbVisible: (thumbVisible: Boolean) -> Unit,
    updateDraggingThumb: (isDraggingThumb: Boolean) -> Unit,
    dragEndCallback: () -> Unit,
) {
    // --- Fast Scroll 트랙 + 썸 + 버블 ---
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    var thumbY by remember { mutableFloatStateOf(0f) } // 썸의 y-offset (픽셀)
    var dragY by remember { mutableFloatStateOf(0f) }
    var proportion by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableFloatStateOf(0f) }
    var bubbleText by remember { mutableStateOf<String?>(null) } // 버블 텍스트 (옵션)

    val layoutInfo = remember { derivedStateOf { listState.layoutInfo } }
    val totalItems = layoutInfo.value.totalItemsCount.coerceAtLeast(1)

    // 보이는 첫 아이템 높이로 평균 높이 추정
    val itemHeight =
        layoutInfo.value.visibleItemsInfo
            .firstOrNull()
            ?.size ?: 1

    val firstIndex = remember { derivedStateOf { listState.firstVisibleItemIndex } }
    val firstOffset = remember { derivedStateOf { listState.firstVisibleItemScrollOffset } }

    val totalContentHeightPx = (totalItems * itemHeight).toFloat()
    val scrollablePx = (totalContentHeightPx - containerHeightPx).coerceAtLeast(1f)

    val scrolledPx = (firstIndex.value.times(itemHeight) + firstOffset.value).toFloat()
    val progress = (scrolledPx / scrollablePx).coerceIn(0f, 1f)

    val thumbHeightPx = with(density) { 30.dp.toPx() }
    val baseThumbY = progress * (containerHeightPx - thumbHeightPx)
    thumbY = if (isDraggingThumb) thumbY else baseThumbY
    val drawThumbY =
        if (isDraggingThumb) thumbY.coerceIn(0f, containerHeightPx - thumbHeightPx) else baseThumbY

    // --- Fast Scroll 트랙 + 썸 ---
    fun parseBubbleText(item: Any): String =
        when (item) {
            is DiaryMainItem.DiaryEntry -> item.diary.dateString?.take(7) ?: ""
            is DiaryMainItem.NoteEntry -> item.note.title ?: ""
            is DiaryMainItem.TaskEntry -> item.task.title ?: ""
            is DiaryMainItem.NoteFolderEntry -> item.folder.name
            is DiaryMainItem.Header -> ""
            is Diary -> summaryDiaryLabel(item)
            else -> ""
        }
    Box(
        modifier =
            modifier
                .fillMaxHeight()
                .width(30.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { startOffset ->
                            updateDraggingThumb(true)
                            updateThumbVisible(true)
                            dragY = startOffset.y
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            thumbY += dragAmount.y
                            val safeThumbY =
                                thumbY.coerceIn(0f, containerHeightPx - thumbHeightPx)
                            proportion = safeThumbY / (containerHeightPx - thumbHeightPx)
                            offset = proportion * scrollablePx
                            val targetIndex = (offset / itemHeight).toInt()
                            coroutineScope.launch {
                                listState.scrollToItem(targetIndex)
                            }

                            // 버블 텍스트 업데이트
                            val itemIdx = targetIndex.coerceIn(0, items.size - 1)
                            if (items.isNotEmpty()) {
                                bubbleText = parseBubbleText(items[itemIdx])
                            }
                        },
                        onDragEnd = {
                            updateDraggingThumb(false)
                            dragEndCallback()
                        },
                    )
                },
    ) {
        // --- 썸 (Thumb) ---
        Box(
            modifier =
                Modifier
                    .offset { IntOffset(0, drawThumbY.toInt()) }
                    .align(Alignment.TopEnd)
                    .padding(end = 4.dp)
                    .width(8.dp)
                    .height(30.dp)
                    .background(
                        color =
                            if (thumbVisible) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            } else {
                                Color.Transparent
                            },
                        shape = CircleShape,
                    ),
        )

        // --- 버블 (Bubble / Tooltip) ---
        if (isDraggingThumb && bubbleText != null) {
            Card(
                modifier =
                    Modifier
                        .offset {
                            IntOffset(
                                x = with(density) { (-100).dp.toPx().toInt() },
                                y = drawThumbY.toInt() - with(density) { 15.dp.toPx().toInt() },
                            )
                        }
                        .padding(end = 16.dp),
                shape = RoundedCornerShape(8.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = bubbleText!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
    }
}

@Composable
fun Text(
    text: String,
    style: androidx.compose.ui.text.TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
) {
    androidx.compose.material3.Text(
        text = text,
        style = style,
        color = color,
        modifier = modifier,
    )
}
