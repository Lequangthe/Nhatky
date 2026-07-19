package com.quangthe.nhatky.ui.components

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.compose.AndroidFragment
import com.bumptech.glide.Glide
import com.zhpan.bannerview.constants.PageStyle
import com.quangthe.nhatky.commons.utils.DateUtils
import com.quangthe.nhatky.commons.utils.EasyDiaryUtils
import com.quangthe.nhatky.R
import com.quangthe.nhatky.extensions.config
import com.quangthe.nhatky.extensions.darkenColor
import com.quangthe.nhatky.extensions.dpToPixel
import com.quangthe.nhatky.extensions.innerCardDarkenFactor
import com.quangthe.nhatky.extensions.updateDashboardInnerCard
import com.quangthe.nhatky.fragments.PhotoHighlightFragment
import com.quangthe.nhatky.helper.PhotoHighlightConstants
import com.quangthe.nhatky.models.Diary
import com.quangthe.nhatky.models.NoteFolder
import com.quangthe.nhatky.models.TodoTask
import com.quangthe.nhatky.models.SimpleNote
import com.quangthe.nhatky.ui.models.DiaryUiModel
import com.quangthe.nhatky.views.LocationContainerView
import org.apache.commons.lang3.StringUtils

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryItemCard(
    diary: Diary,
    itemClickCallback: (diary: Diary) -> Unit,
    itemLongClickCallback: () -> Unit,
    onDeleteClick: (Diary) -> Unit = {},
) {
    val context = LocalContext.current
    val config = context.config

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { itemClickCallback(diary) },
                onLongClick = itemLongClickCallback,
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (diary.currentTimeMillis > System.currentTimeMillis()) {
                        FutureDiaryBadge(diary.currentTimeMillis, MaterialTheme.colorScheme.onSurface)
                    }
                }
                IconButton(
                    onClick = { onDeleteClick(diary) },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = when (diary.isAllDay) {
                        true -> DateUtils.getDateStringFromTimeMillis(diary.currentTimeMillis)
                        false -> DateUtils.getDateTimeStringForceFormatting(
                            diary.currentTimeMillis, context
                        )
                    },
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                )

                if (StringUtils.isNotEmpty(diary.title)) {
                    Text(
                        text = diary.title!!,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = diary.contents.orEmpty(),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    fontSize = 15.sp,
                    maxLines = if (config.enableContentsSummary) config.summaryMaxLines else Int.MAX_VALUE,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if ((diary.photoUris?.size ?: 0) > 0) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    factory = { ctx ->
                        LinearLayout(ctx).apply {
                            orientation = LinearLayout.HORIZONTAL
                            val photoUris = diary.photoUrisWithEncryptionPolicy()
                            photoUris?.forEach { photoUri ->
                                val imageXY = ctx.dpToPixel(40F)
                                val imageView = ImageView(ctx).apply {
                                    layoutParams = ViewGroup.LayoutParams(imageXY, imageXY)
                                    scaleType = ImageView.ScaleType.CENTER_CROP
                                }
                                Glide.with(ctx)
                                    .load(EasyDiaryUtils.getApplicationDataDirectory(ctx) + photoUri.getFilePath())
                                    .apply(
                                        EasyDiaryUtils.createThumbnailGlideOptions(
                                            ctx.dpToPixel(8f).toFloat(),
                                            photoUri.isEncrypt(),
                                        )
                                    )
                                    .into(imageView)
                                val contentPadding = ctx.dpToPixel(1F)
                                val cardView = com.quangthe.nhatky.views.FixedCardView(ctx).apply {
                                    ctx.updateDashboardInnerCard(this)
                                    layoutParams = ViewGroup.MarginLayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                    )
                                    radius = ctx.dpToPixel(8f).toFloat()
                                    fixedAppcompatPadding = true
                                    setContentPadding(contentPadding, contentPadding, contentPadding, contentPadding)
                                    addView(imageView)
                                }
                                addView(cardView)
                                val margin = ctx.dpToPixel(4F)
                                (cardView.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(0, 0, margin.toInt(), 0)
                            }
                        }
                    },
                )
            }

            if (config.enableLocationInfo && (diary.location != null)) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    factory = { ctx ->
                        LocationContainerView(ctx).apply {
                            setLocation(diary.location)
                        }
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItemCard(
    note: SimpleNote,
    itemClickCallback: (SimpleNote) -> Unit,
    itemLongClickCallback: () -> Unit,
    onDeleteClick: (SimpleNote) -> Unit = {},
) {
    val context = LocalContext.current
    val bgColor = if (note.color != 0) Color(note.color) else MaterialTheme.colorScheme.surface
    val textColor = if (note.color != 0) Color.White else MaterialTheme.colorScheme.onSurface
    val contentColor = if (note.color != 0) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { itemClickCallback(note) },
                onLongClick = itemLongClickCallback,
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (StringUtils.isNotEmpty(note.title)) {
                    Text(
                        text = note.title!!,
                        color = textColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (note.isPinned) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFA000))
                    )
                }
                IconButton(
                    onClick = { onDeleteClick(note) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = textColor.copy(alpha = 0.3f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = note.contents.orEmpty(),
                color = contentColor,
                fontSize = 13.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp,
                modifier = Modifier.fillMaxWidth()
            )
            
            if (note.updatedAt > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = DateUtils.getDateTimeStringForceFormatting(note.updatedAt, context),
                    color = contentColor.copy(alpha = 0.5f),
                    fontSize = 10.sp,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteFolderCard(
    folder: NoteFolder,
    itemClickCallback: (NoteFolder) -> Unit,
    itemLongClickCallback: () -> Unit,
) {
    val folderColor = if (folder.color != 0) Color(folder.color) else Color(0xFF2196F3)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { itemClickCallback(folder) },
                onLongClick = itemLongClickCallback,
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(folderColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "📁",
                    fontSize = 18.sp,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = folder.name,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "›",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                fontSize = 20.sp,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskItemCard(
    task: TodoTask,
    itemClickCallback: (TodoTask) -> Unit,
    itemLongClickCallback: () -> Unit,
    toggleTaskCallback: (TodoTask) -> Unit = {},
    onDeleteClick: (TodoTask) -> Unit = {},
) {
    val totalItems = task.items.size
    val completedItems = task.items.count { it.isChecked }
    val progress = if (totalItems > 0) completedItems.toFloat() / totalItems else 0f
    val isCompleted = task.isCompleted

    val priorityColor = when (task.priority) {
        3 -> Color(0xFFEF4444)
        2 -> Color(0xFFF97316)
        1 -> Color(0xFF22C55E)
        else -> Color(0xFF9CA3AF)
    }

    val surfaceTonal = if (isCompleted) Color(0xFFF1F5F9) else Color.White
    val titleColor = if (isCompleted) Color(0xFF94A3B8) else Color(0xFF1E293B)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { itemClickCallback(task) },
                onLongClick = itemLongClickCallback,
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceTonal),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCompleted) 0.dp else 1.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Priority bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(
                        if (task.priority > 0) priorityColor else Color(0xFFE2E8F0),
                        RoundedCornerShape(topEnd = 0.dp, bottomEnd = 0.dp, topStart = 4.dp, bottomStart = 4.dp)
                    )
            )
            Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 14.dp, bottom = 14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Check circle
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(if (isCompleted) Color(0xFF22C55E) else Color(0xFFF1F5F9))
                            .clickable { toggleTaskCallback(task) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Canvas(Modifier.size(11.dp)) {
                                val p = Path().apply {
                                    moveTo(size.width * 0.2f, size.height * 0.52f)
                                    lineTo(size.width * 0.42f, size.height * 0.75f)
                                    lineTo(size.width * 0.8f, size.height * 0.28f)
                                }
                                drawPath(p, Color.White, style = Stroke(width = 2.2f, cap = StrokeCap.Round))
                            }
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = task.title ?: "",
                        color = titleColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { onDeleteClick(task) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Delete", tint = Color(0xFFCBD5E1), modifier = Modifier.size(14.dp))
                    }
                }

                if (totalItems > 0) {
                    Spacer(Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        task.items.take(3).forEachIndexed { idx, item ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(if (item.isChecked) Color(0xFF22C55E) else Color(0xFFE2E8F0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (item.isChecked) {
                                        Canvas(Modifier.size(8.dp)) {
                                            val p = Path().apply {
                                                moveTo(size.width * 0.2f, size.height * 0.5f)
                                                lineTo(size.width * 0.42f, size.height * 0.75f)
                                                lineTo(size.width * 0.8f, size.height * 0.3f)
                                            }
                                            drawPath(p, Color.White, style = Stroke(width = 1.5f, cap = StrokeCap.Round))
                                        }
                                    }
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = item.text,
                                    color = if (item.isChecked) Color(0xFF94A3B8) else Color(0xFF475569),
                                    fontSize = 13.sp,
                                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    // Progress bar
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                        color = if (progress >= 1f) Color(0xFF22C55E) else priorityColor,
                        trackColor = Color(0xFFE2E8F0),
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Text(
                            "$completedItems/$totalItems",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FutureDiaryBadge(currentTimeMillis: Long, primaryColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(primaryColor.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(4.dp)
                .background(primaryColor, RoundedCornerShape(2.dp)),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = DateUtils.getOnlyDayRemaining(currentTimeMillis),
            color = primaryColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiarySubItemCard(
    diary: DiaryUiModel,
    itemClickCallback: (diary: DiaryUiModel) -> Unit,
    itemLongClickCallback: () -> Unit,
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .combinedClickable(
                onClick = { itemClickCallback(diary) },
                onLongClick = itemLongClickCallback,
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (diary.currentTimeMillis > System.currentTimeMillis()) {
                Text(
                    text = DateUtils.getOnlyDayRemaining(diary.currentTimeMillis),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            Text(
                text = diary.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = when (diary.isAllDay) {
                    true -> DateUtils.getDateStringFromTimeMillis(diary.currentTimeMillis)
                    false -> DateUtils.getDateTimeStringForceFormatting(
                        diary.currentTimeMillis, context
                    )
                },
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontSize = 11.sp,
            )
        }
    }
}

@Composable
fun PhotoHighlightCard(
    modifier: Modifier = Modifier,
    height: Dp? = null,
    isVisible: Boolean = false,
    onVisibilityChanged: (isVisible: Boolean) -> Unit,
) {
    AndroidFragment<PhotoHighlightFragment>(
        modifier =
            if (isVisible) {
                height?.let {
                    modifier
                        .height(it)
                        .fillMaxWidth()
                } ?: run { modifier.fillMaxSize() }
            } else {
                modifier
                    .size(0.dp)
                    .graphicsLayer(alpha = 0f)
            },
        arguments =
            Bundle().apply {
                putInt(PhotoHighlightConstants.PAGE_STYLE, PageStyle.MULTI_PAGE_SCALE)
                putFloat(PhotoHighlightConstants.REVEAL_WIDTH, 20f)
                putFloat(PhotoHighlightConstants.PAGE_MARGIN, 5f)
                putBoolean(PhotoHighlightConstants.AUTO_PLAY, true)
            },
        onUpdate = { fragment ->
            fragment.togglePhotoHighlightCallback = { isVisible ->
                onVisibilityChanged(isVisible)
            }
        },
    )
}
