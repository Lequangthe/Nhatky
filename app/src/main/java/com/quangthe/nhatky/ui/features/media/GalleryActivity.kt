package com.quangthe.nhatky.ui.features.media

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.quangthe.nhatky.commons.utils.EasyDiaryUtils
import com.quangthe.nhatky.R
import com.quangthe.nhatky.ui.base.EasyDiaryComposeBaseActivity
import com.quangthe.nhatky.ui.features.media.GalleryViewPagerActivity
import com.quangthe.nhatky.extensions.config
import com.quangthe.nhatky.core.config.DIARY_PHOTO_DIRECTORY
import com.quangthe.nhatky.models.Diary
import com.quangthe.nhatky.repositories.DiaryRepository
import com.quangthe.nhatky.ui.theme.AppTheme
import java.io.File

class GalleryActivity : EasyDiaryComposeBaseActivity() {
    private val diaryRepository = DiaryRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                GalleryScreen()
            }
        }
    }

    data class AttachedPhoto(val file: File, val diary: Diary?)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun GalleryScreen() {
        val context = LocalContext.current
        var photos by remember { mutableStateOf<List<AttachedPhoto>>(emptyList()) }
        var loading by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            loading = true
            photos = loadAttachedPhotos(context)
            loading = false
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Gallery") },
                    navigationIcon = {
                        IconButton(onClick = { finish() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            if (photos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (loading) "Loading..." else "No photos found")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 120.dp),
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(photos, key = { it.file.absolutePath }) { photo ->
                        PhotoThumbnail(photo)
                    }
                }
            }
        }
    }

    @Composable
    private fun PhotoThumbnail(photo: AttachedPhoto) {
        AndroidView(
            modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable {
                val intent = Intent(this@GalleryActivity, GalleryViewPagerActivity::class.java)
                startActivity(intent)
            },
            factory = { ctx ->
                ImageView(ctx).apply {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    Glide.with(ctx).load(photo.file).centerCrop().into(this)
                }
            }
        )
    }

    private suspend fun loadAttachedPhotos(context: Context): List<AttachedPhoto> = withContext(Dispatchers.IO) {
        getAttachedPhotos(context, diaryRepository)
    }

    companion object {
        suspend fun getAttachedPhotos(context: Context, repository: DiaryRepository): List<AttachedPhoto> {
            val dir = File(EasyDiaryUtils.getApplicationDataDirectory(context) + DIARY_PHOTO_DIRECTORY)
            if (!dir.exists()) return emptyList()
            return dir.listFiles()
                ?.map { file ->
                    val diary = repository.findDiaryByPhotoUri(file.name)
                    AttachedPhoto(file, diary)
                }
                ?.filter { it.diary != null || context.config.visibleUnlinkedPhotos }
                ?.sortedByDescending { it.diary?.currentTimeMillis ?: 0 }
                ?: emptyList()
        }
    }
}
