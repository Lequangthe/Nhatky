package com.quangthe.nhatky.compose

import android.graphics.Color
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.Markwon
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tables.TableTheme
import io.noties.markwon.syntax.Prism4jThemeDefault
import io.noties.markwon.syntax.SyntaxHighlightPlugin
import io.noties.markwon.utils.ColorUtils
import io.noties.markwon.utils.Dip
import io.noties.prism4j.Prism4j
import io.noties.prism4j.annotations.PrismBundle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.quangthe.nhatky.commons.utils.EasyDiaryUtils
import com.quangthe.nhatky.extensions.checkPermission
import com.quangthe.nhatky.extensions.confirmPermission
import com.quangthe.nhatky.extensions.isConnectedOrConnecting
import com.quangthe.nhatky.extensions.makeSnackBar
import com.quangthe.nhatky.extensions.makeToast
import com.quangthe.nhatky.extensions.pauseLock
import com.quangthe.nhatky.helper.EXTERNAL_STORAGE_PERMISSIONS
import com.quangthe.nhatky.helper.MARKDOWN_DIRECTORY
import com.quangthe.nhatky.helper.MarkdownConstants
import com.quangthe.nhatky.helper.REQUEST_CODE_EXTERNAL_STORAGE_WITH_MARKDOWN
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.net.HttpURLConnection
import java.net.URL

@PrismBundle(include = ["java", "kotlin", "javascript"], grammarLocatorClassName = ".GrammarLocatorSourceCode")
class MarkDownViewerActivity : EasyDiaryComposeBaseActivity() {
    private val mPrism4j = Prism4j(GrammarLocatorSourceCode())
    private lateinit var savedFilePath: String
    private var mForceAppendCodeBlock = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pageTitle = intent.getStringExtra(MarkdownConstants.OPEN_URL_DESCRIPTION) ?: ""
        mForceAppendCodeBlock = intent.getBooleanExtra(MarkdownConstants.FORCE_APPEND_CODE_BLOCK, true)
        savedFilePath = "${EasyDiaryUtils.getApplicationDataDirectory(this) + MARKDOWN_DIRECTORY + pageTitle}.md"
        val markdownUrl = intent.getStringExtra(MarkdownConstants.OPEN_URL_INFO)!!

        if (checkPermission(EXTERNAL_STORAGE_PERMISSIONS)) {
            if (!File(savedFilePath).exists()) {
                downloadMarkdown(markdownUrl)
            }
        } else {
            confirmPermission(EXTERNAL_STORAGE_PERMISSIONS, REQUEST_CODE_EXTERNAL_STORAGE_WITH_MARKDOWN)
        }

        setContent {
            MarkdownScreen(pageTitle, markdownUrl)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        pauseLock()
        when (requestCode) {
            REQUEST_CODE_EXTERNAL_STORAGE_WITH_MARKDOWN -> {
                if (checkPermission(EXTERNAL_STORAGE_PERMISSIONS)) {
                    val markdownUrl = intent.getStringExtra(MarkdownConstants.OPEN_URL_INFO)!!
                    if (!File(savedFilePath).exists()) {
                        downloadMarkdown(markdownUrl)
                    }
                } else {
                    makeSnackBar("Permission denied")
                }
            }
        }
    }

    private fun downloadMarkdown(markdownUrl: String) {
        Thread {
            if (isConnectedOrConnecting()) {
                try {
                    val url = URL(markdownUrl)
                    val httpConn = url.openConnection() as HttpURLConnection
                    if (httpConn.responseCode == HttpURLConnection.HTTP_OK) {
                        val inputStream = httpConn.inputStream
                        val lines = IOUtils.readLines(inputStream, "UTF-8").toMutableList()
                        if (mForceAppendCodeBlock) {
                            lines.add(0, "```java")
                            lines.add("```")
                        }
                        FileUtils.writeLines(File(savedFilePath), "UTF-8", lines)
                        inputStream.close()
                    }
                    httpConn.disconnect()
                } catch (_: Exception) {}
            } else {
                runOnUiThread { makeToast("Network is not available.") }
            }
        }.start()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MarkdownScreen(pageTitle: String, markdownUrl: String) {
        var markdownContent by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            markdownContent = withContext(Dispatchers.IO) { readSavedFile() }
            isLoading = false
        }

        val markwon = remember(Unit) {
            Markwon.builder(this@MarkDownViewerActivity)
                .usePlugin(TablePlugin.create { builder: TableTheme.Builder ->
                    val dip = Dip.create(this@MarkDownViewerActivity)
                    builder.tableBorderWidth(dip.toPx(2))
                        .tableBorderColor(Color.BLACK)
                        .tableCellPadding(dip.toPx(4))
                        .tableHeaderRowBackgroundColor(ColorUtils.applyAlpha(Color.BLUE, 80))
                })
                .usePlugin(SyntaxHighlightPlugin.create(mPrism4j, Prism4jThemeDefault.create(0)))
                .build()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(pageTitle) },
                    navigationIcon = {
                        IconButton(onClick = { finish() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close")
                        }
                    },
                )
            },
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                if (isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        val textView = android.widget.TextView(ctx)
                        textView
                    },
                    update = { textView ->
                        if (markdownContent.isNotEmpty()) {
                            markwon.setMarkdown(textView, markdownContent)
                        }
                    },
                )
            }
        }
    }

    private fun readSavedFile(): String {
        return try {
            FileInputStream(File(savedFilePath)).use { inputStream ->
                IOUtils.readLines(inputStream, "UTF-8").joinToString(System.lineSeparator())
            }
        } catch (e: FileNotFoundException) {
            ""
        }
    }
}
