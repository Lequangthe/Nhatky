package com.quangthe.nhatky.ui.features.media

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView
import com.quangthe.nhatky.ui.base.EasyDiaryComposeBaseActivity
import com.quangthe.nhatky.commons.utils.EasyDiaryUtils
import com.quangthe.nhatky.R
import com.quangthe.nhatky.extensions.shareFile
import com.quangthe.nhatky.core.config.DIARY_ATTACH_PHOTO_INDEX
import com.quangthe.nhatky.core.config.DIARY_SEQUENCE
import com.quangthe.nhatky.core.config.MIME_TYPE_JPEG
import com.quangthe.nhatky.repositories.DiaryRepository
import kotlinx.coroutines.runBlocking
import com.quangthe.nhatky.models.Diary
import java.io.File

class PhotoViewPagerActivity : EasyDiaryComposeBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sequence = intent.getIntExtra(DIARY_SEQUENCE, 0)
        val photoIndex = intent.getIntExtra(DIARY_ATTACH_PHOTO_INDEX, 0)
        val diary = runBlocking { DiaryRepository().findDiaryBy(sequence) }!!

        setContent {
            PhotoPagerScreen(diary, photoIndex)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun PhotoPagerScreen(diary: Diary, initialIndex: Int) {
        val photoUris = diary.photoUris ?: emptyList()
        val pagerState = rememberPagerState(pageCount = { photoUris.size })

        LaunchedEffect(initialIndex) {
            if (initialIndex > 0 && initialIndex < photoUris.size) {
                pagerState.scrollToPage(initialIndex)
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("${pagerState.currentPage + 1} / ${photoUris.size}") },
                    navigationIcon = {
                        IconButton(onClick = { finish() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close")
                        }
                    },
                )
            },
            containerColor = Color.Black,
        ) { innerPadding ->
            if (photoUris.isEmpty()) return@Scaffold

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                beyondViewportPageCount = 1,
            ) { page ->
                PhotoViewPage(diary, page)
            }
        }
    }

    @Composable
    private fun PhotoViewPage(diary: Diary, page: Int) {
        val context = LocalContext.current
        val photoUris = diary.photoUris ?: return
        if (page >= photoUris.size) return
        val photoUri = photoUris[page]

        val imageFilePath = EasyDiaryUtils.getApplicationDataDirectory(context) +
            diary.photoUrisWithEncryptionPolicy()!![page].getFilePath()
        val file = File(imageFilePath)

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (file.isFile) {
                val requestOptions = remember {
                    RequestOptions()
                        .error(R.drawable.ic_error_7)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.HIGH)
                }
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        PhotoView(ctx).also { photoView ->
                            Glide.with(ctx)
                                .load(imageFilePath)
                                .apply(requestOptions)
                                .into(photoView)
                        }
                    },
                )
            } else {
                Text(
                    text = if (diary.isEncrypt) "The diary is encrypted. You will need to decrypt the diary to see the attached photos."
                           else "Image file not found.",
                    color = Color.White,
                )
            }
        }
    }
}
