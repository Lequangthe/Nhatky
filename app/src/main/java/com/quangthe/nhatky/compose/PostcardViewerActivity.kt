package com.quangthe.nhatky.compose

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import java.io.File
import com.quangthe.nhatky.commons.utils.EasyDiaryUtils
import com.quangthe.nhatky.R
import com.quangthe.nhatky.helper.DIARY_POSTCARD_DIRECTORY
import com.quangthe.nhatky.helper.POSTCARD_SEQUENCE
import com.quangthe.nhatky.ui.theme.AppTheme

class PostcardViewerActivity : EasyDiaryComposeBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                PostcardViewerScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun PostcardViewerScreen() {
        val context = LocalContext.current
        EasyDiaryUtils.initWorkingDirectory(context)
        var postcards by remember { mutableStateOf(loadPostcards()) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(context.getString(R.string.diary_postcard)) },
                    navigationIcon = {
                        IconButton(onClick = { finish() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            if (postcards.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No postcards found")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(postcards, key = { it.absolutePath }) { file ->
                        PostcardThumbnail(file)
                    }
                }
            }
        }
    }

    @Composable
    private fun PostcardThumbnail(file: File) {
        AndroidView(
            modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable {
                val intent = Intent(this@PostcardViewerActivity, PostcardViewPagerActivity::class.java)
                intent.putExtra(POSTCARD_SEQUENCE, 0)
                startActivity(intent)
            },
            factory = { context ->
                ImageView(context).apply {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    Glide.with(context).load(file).centerCrop().into(this)
                }
            }
        )
    }

    private fun loadPostcards(): List<File> {
        val dir = File(EasyDiaryUtils.getApplicationDataDirectory(this) + DIARY_POSTCARD_DIRECTORY)
        if (!dir.exists()) return emptyList()
        return dir.listFiles()
            ?.filter { it.extension.equals("jpg", true) }
            ?.sortedDescending()
            ?: emptyList()
    }
}
