package com.quangthe.nhatky.compose

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.github.chrisbanes.photoview.PhotoView
import com.quangthe.nhatky.commons.utils.EasyDiaryUtils
import com.quangthe.nhatky.extensions.shareFile
import com.quangthe.nhatky.helper.DIARY_POSTCARD_DIRECTORY
import com.quangthe.nhatky.helper.MIME_TYPE_JPEG
import com.quangthe.nhatky.helper.POSTCARD_SEQUENCE
import java.io.File

class PostcardViewPagerActivity : EasyDiaryComposeBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sequence = intent.getIntExtra(POSTCARD_SEQUENCE, 0)
        val postcardFiles = File(EasyDiaryUtils.getApplicationDataDirectory(this) + DIARY_POSTCARD_DIRECTORY)
            .listFiles()
            .filter { it.extension.equals("jpg", true) }
            .sortedDescending()

        setContent {
            PostcardPagerScreen(postcardFiles, sequence)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun PostcardPagerScreen(postcardFiles: List<File>, initialIndex: Int) {
        val pagerState = rememberPagerState(pageCount = { postcardFiles.size })

        LaunchedEffect(initialIndex) {
            if (initialIndex in postcardFiles.indices) {
                pagerState.scrollToPage(initialIndex)
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("${pagerState.currentPage + 1} / ${postcardFiles.size}") },
                    navigationIcon = {
                        IconButton(onClick = { finish() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close")
                        }
                    },
                )
            },
            containerColor = Color.Black,
        ) { innerPadding ->
            if (postcardFiles.isEmpty()) return@Scaffold

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                beyondViewportPageCount = 1,
            ) { page ->
                val file = postcardFiles[page]
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    val bitmap = remember(page) { BitmapFactory.decodeFile(file.path) }
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
