package com.quangthe.nhatky.compose

import android.content.Intent
import android.graphics.BitmapFactory
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.github.chrisbanes.photoview.PhotoView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import com.quangthe.nhatky.repositories.DiaryRepository
import com.quangthe.nhatky.compose.GalleryActivity
import com.quangthe.nhatky.extensions.makeToast
import com.quangthe.nhatky.extensions.shareFile
import com.quangthe.nhatky.helper.DIARY_SEQUENCE
import com.quangthe.nhatky.helper.MIME_TYPE_JPEG
import com.quangthe.nhatky.helper.POSTCARD_SEQUENCE
import com.quangthe.nhatky.helper.TransitionHelper
import java.io.File

class GalleryViewPagerActivity : EasyDiaryComposeBaseActivity() {
    private val diaryRepository = DiaryRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sequence = intent.getIntExtra(POSTCARD_SEQUENCE, 0)
        val attachedPhotos = runBlocking { GalleryActivity.getAttachedPhotos(this@GalleryViewPagerActivity, diaryRepository) }

        setContent {
            GalleryPagerScreen(attachedPhotos, sequence)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun GalleryPagerScreen(
        attachedPhotos: List<GalleryActivity.AttachedPhoto>,
        initialIndex: Int,
    ) {
        val pagerState = rememberPagerState(pageCount = { attachedPhotos.size })

        LaunchedEffect(initialIndex) {
            if (initialIndex in attachedPhotos.indices) {
                pagerState.scrollToPage(initialIndex)
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("${pagerState.currentPage + 1} / ${attachedPhotos.size}") },
                    navigationIcon = {
                        IconButton(onClick = { finish() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close")
                        }
                    },
                )
            },
            containerColor = Color.Black,
        ) { innerPadding ->
            if (attachedPhotos.isEmpty()) return@Scaffold

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                beyondViewportPageCount = 1,
            ) { page ->
                val attachedPhoto = attachedPhotos[page]
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (attachedPhoto.diary?.isEncrypt == true) {
                        Text(
                            text = "The diary is encrypted. You will need to decrypt the diary to see the attached photos.",
                            color = Color.White,
                        )
                    } else {
                        val bitmap = remember(page) { BitmapFactory.decodeFile(attachedPhoto.file.path) }
                        if (bitmap != null) {
                            AndroidView(
                                modifier = Modifier.fillMaxSize(),
                                factory = { ctx ->
                                    PhotoView(ctx).also { it.setImageBitmap(bitmap) }
                                },
                            )
                        } else {
                            Text("Image file not found.", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
