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
import androidx.compose.foundation.layout.IntrinsicSize
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
import com.quangthe.nhatky.commons.utils.DateUtils
import com.quangthe.nhatky.commons.utils.EasyDiaryUtils
import com.quangthe.nhatky.R
import com.quangthe.nhatky.extensions.config
import com.quangthe.nhatky.extensions.darkenColor
import com.quangthe.nhatky.extensions.dpToPixel
import com.quangthe.nhatky.extensions.innerCardDarkenFactor
import com.quangthe.nhatky.extensions.updateDashboardInnerCard

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
    isTimelineMode: Boolean = false,
    isLast: Boolean = false
) {
    val context = LocalContext.current
    val config = context.config

    if (isTimelineMode) {
        DiaryTimelineItem(
            diary = diary,
            isLast = isLast,
            onClick = { itemClickCallback(diary) }
        )
    } else {
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
                        style = MaterialTheme.typography.bodySmall,
                    )

                    if (StringUtils.isNotEmpty(diary.title)) {
                        Text(
                            text = diary.title!!,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = diary.contents.orEmpty(),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyMedium,
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
                                    val photoPath = if (photoUri.isContentUri()) {
                                        photoUri.photoUri
                                    } else {
                                        EasyDiaryUtils.getApplicationDataDirectory(ctx) + photoUri.getFilePath()
                                    }
                                    Glide.with(ctx)
                                        .load(photoPath)
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
                                    (cardView.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(0, 0, margin, 0)
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
}

@Composable
fun DiaryTimelineItem(
    diary: Diary,
    isLast: Boolean,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    val primaryColor = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier.height(IntrinsicSize.Min).padding(vertical = 4.dp)
    ) {
        // reuse TimelineIndicator logic
        Box(modifier = Modifier.width(36.dp).fillMaxHeight()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerX = size.width / 2f
                val topY = 0f
                val bottomY = size.height
                val circleCenterY = 16.dp.toPx()

                if (!isLast) {
                    drawLine(
                        color = primaryColor,
                        start = androidx.compose.ui.geometry.Offset(centerX, topY),
                        end = androidx.compose.ui.geometry.Offset(centerX, bottomY),
                        strokeWidth = 2.dp.toPx()
                    )
                } else {
                    drawLine(
                        color = primaryColor,
                        start = androidx.compose.ui.geometry.Offset(centerX, topY),
                        end = androidx.compose.ui.geometry.Offset(centerX, circleCenterY + 6.dp.toPx()),
                        strokeWidth = 2.dp.toPx()
                    )
                }

                drawCircle(
                    color = primaryColor,
                    radius = 6.dp.toPx(),
                    center = androidx.compose.ui.geometry.Offset(centerX, circleCenterY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = androidx.compose.ui.geometry.Offset(centerX, circleCenterY)
                )
            }
        }

        Card(
            modifier = Modifier.weight(1f).padding(end = 8.dp).clickable { onClick() },
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = when (diary.isAllDay) {
                        true -> DateUtils.getDateStringFromTimeMillis(diary.currentTimeMillis)
                        false -> DateUtils.getDateTimeStringForceFormatting(
                            diary.currentTimeMillis, context
                        )
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!diary.title.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = diary.title!!,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (!diary.contents.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = diary.contents!!,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
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

@Composable
fun SectionHeader(title: String, count: Int = 0) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
        Text(
            text = if (count > 0) "$title ($count)" else title,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
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
    val isCompleted = task.isCompleted
    val titleColor = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) 
                    else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = { itemClickCallback(task) },
                onLongClick = itemLongClickCallback,
            )
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Google Style Checkbox (Circular)
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(24.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = if (isCompleted) MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                    shape = CircleShape
                )
                .background(if (isCompleted) MaterialTheme.colorScheme.primary else Color.Transparent)
                .clickable { toggleTaskCallback(task) },
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title ?: "",
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                    fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.Medium
                ),
                color = titleColor,
            )
            
            if (task.priority > 0) {
                val (label, color) = when (task.priority) {
                    3 -> "High" to Color(0xFFEF4444)
                    2 -> "Medium" to Color(0xFFF97316)
                    else -> "Low" to Color(0xFF22C55E)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                    Icon(
                        imageVector = Icons.Default.PriorityHigh,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = color,
                        modifier = Modifier.padding(start = 2.dp, end = 8.dp)
                    )
                }
            }
        }

        IconButton(onClick = { onDeleteClick(task) }, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp)
            )
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


