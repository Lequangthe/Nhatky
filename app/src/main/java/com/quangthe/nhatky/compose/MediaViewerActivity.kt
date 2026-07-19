package com.quangthe.nhatky.compose

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.quangthe.nhatky.R
import java.io.File

class MediaViewerActivity : EasyDiaryComposeBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val path = intent.getStringExtra("path") ?: ""
        val mimeType = intent.getStringExtra("mimeType") ?: ""

        val file = File(path)
        if (!file.exists()) {
            finish()
            return
        }

        window.statusBarColor = Color.BLACK

        setContent {
            MediaViewerScreen(path, mimeType)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MediaViewerScreen(path: String, mimeType: String) {
        val context = LocalContext.current
        val isAudio = mimeType.startsWith("audio")

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("") },
                    navigationIcon = {
                        IconButton(onClick = { finish() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = ComposeColor.White,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = ComposeColor.Transparent,
                    ),
                )
            },
            containerColor = ComposeColor.Black,
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(ComposeColor.Black),
                contentAlignment = Alignment.Center,
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        VideoView(ctx).apply {
                            setVideoPath(path)
                            val mediaController = MediaController(ctx)
                            mediaController.setAnchorView(this)
                            setMediaController(mediaController)
                            start()
                        }
                    },
                )

                if (isAudio) {
                    Icon(
                        modifier = Modifier.size(100.dp),
                        painter = androidx.compose.ui.res.painterResource(R.drawable.ic_mic),
                        contentDescription = "Audio",
                        tint = ComposeColor.White,
                    )
                }
            }
        }
    }
}
