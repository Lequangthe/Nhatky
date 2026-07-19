package com.quangthe.nhatky.compose

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import com.quangthe.nhatky.commons.utils.BitmapUtils
import com.quangthe.nhatky.commons.utils.DateUtils
import com.quangthe.nhatky.commons.utils.EasyDiaryUtils
import com.quangthe.nhatky.R
import com.quangthe.nhatky.adapters.PhotoAdapter
import com.quangthe.nhatky.databinding.ActivityPostcardBinding
import com.quangthe.nhatky.enums.Calculation
import com.quangthe.nhatky.extensions.*
import com.quangthe.nhatky.helper.*
import com.quangthe.nhatky.repositories.DiaryRepository
import com.quangthe.nhatky.ui.theme.AppTheme
import java.io.File

class PostcardActivity : EasyDiaryComposeBaseActivity() {
    private lateinit var mBinding: ActivityPostcardBinding
    private var mSequence: Int = 0
    private var mBgColor = POSTCARD_BG_COLOR_VALUE
    private var mTextColor = POSTCARD_TEXT_COLOR_VALUE
    private var mAddFontSize = 0
    private lateinit var mPhotoAdapter: PhotoAdapter
    private lateinit var mSavedDiaryCardPath: String

    private var mSavedState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSavedState = savedInstanceState
        mSequence = intent.getIntExtra(DIARY_SEQUENCE, 0)
        setContent {
            AppTheme { PostcardScreen() }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(POSTCARD_BG_COLOR, mBgColor)
        outState.putInt(POSTCARD_TEXT_COLOR, mTextColor)
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        if (::mBinding.isInitialized) {
            updateTextSize(mBinding.postContainer, this@PostcardActivity, mAddFontSize)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_EXTERNAL_STORAGE -> {
                if (checkPermission(EXTERNAL_STORAGE_PERMISSIONS)) exportDiaryCard(true)
                else makeSnackBar(findViewById(android.R.id.content), getString(R.string.guide_message_3))
            }
            REQUEST_CODE_EXTERNAL_STORAGE_WITH_SHARE_DIARY_CARD -> {
                if (checkPermission(EXTERNAL_STORAGE_PERMISSIONS)) exportDiaryCard(false)
                else makeSnackBar(findViewById(android.R.id.content), getString(R.string.guide_message_3))
            }
        }
    }

    private fun exportDiaryCard(showInfoDialog: Boolean) {
        mBinding.progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val diaryCardPath = "$DIARY_POSTCARD_DIRECTORY${DateUtils.getCurrentDateTime(DateUtilConstants.DATE_TIME_PATTERN_WITHOUT_DASH)}_$mSequence.jpg"
                mSavedDiaryCardPath = EasyDiaryUtils.getApplicationDataDirectory(this@PostcardActivity) + diaryCardPath
                EasyDiaryUtils.initWorkingDirectory(this@PostcardActivity)
                BitmapUtils.saveBitmapToFileCache(createBitmap(), mSavedDiaryCardPath)
                withContext(Dispatchers.Main) {
                    mBinding.progressBar.visibility = View.GONE
                    if (showInfoDialog) {
                        startActivity(Intent(this@PostcardActivity, PostcardViewerActivity::class.java))
                    } else {
                        shareDiary()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mBinding.progressBar.visibility = View.GONE
                    showAlertDialog(String.format("%s\n\n[ERROR: %s]", getString(R.string.diary_card_export_error_message), e.message), DialogInterface.OnClickListener { _, _ -> })
                }
            }
        }
    }

    private fun shareDiary() {
        val file = File(mSavedDiaryCardPath)
        startActivity(Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, getUriForFile(file))
            type = "image/jpeg"
        }, getString(R.string.diary_card_share_info)))
    }

    private fun createBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(mBinding.scrollPostcard.width, mBinding.scrollPostcard.getChildAt(0).height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        mBinding.scrollPostcard.draw(canvas)
        return bitmap
    }

    private fun setBackgroundColor(selectedColor: Int) {
        mBgColor = selectedColor
        mBinding.postContainer.setBackgroundColor(mBgColor)
    }

    private fun setTextColor(selectedColor: Int) {
        mTextColor = selectedColor
        mBinding.run { diaryTitle.setTextColor(mTextColor); date.setTextColor(mTextColor); contents.setTextColor(mTextColor) }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun PostcardScreen() {
        var showMenu by remember { mutableStateOf(false) }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { finish() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showMenu = true }) {
                            Text("...")
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(text = { Text("Text Color") }, onClick = {
                                showMenu = false
                                ColorPickerDialogBuilder.with(this@PostcardActivity)
                                    .initialColor(mTextColor).wheelType(ColorPickerView.WHEEL_TYPE.FLOWER).density(12)
                                    .setPositiveButton("ok") { _, selectedColor, _ -> setTextColor(selectedColor) }
                                    .setNegativeButton("cancel") { _, _ -> }.build().show()
                            })
                            DropdownMenuItem(text = { Text("Bg Color") }, onClick = {
                                showMenu = false
                                ColorPickerDialogBuilder.with(this@PostcardActivity)
                                    .initialColor(mBgColor).wheelType(ColorPickerView.WHEEL_TYPE.FLOWER).density(12)
                                    .setPositiveButton("ok") { _, selectedColor, _ -> setBackgroundColor(selectedColor) }
                                    .setNegativeButton("cancel") { _, _ -> }.build().show()
                            })
                            DropdownMenuItem(text = { Text("Save") }, onClick = {
                                showMenu = false
                                if (checkPermission(EXTERNAL_STORAGE_PERMISSIONS)) exportDiaryCard(true)
                                else confirmPermission(EXTERNAL_STORAGE_PERMISSIONS, REQUEST_CODE_EXTERNAL_STORAGE)
                            })
                            DropdownMenuItem(text = { Text("Share") }, onClick = {
                                showMenu = false
                                if (checkPermission(EXTERNAL_STORAGE_PERMISSIONS)) exportDiaryCard(false)
                                else confirmPermission(EXTERNAL_STORAGE_PERMISSIONS, REQUEST_CODE_EXTERNAL_STORAGE_WITH_SHARE_DIARY_CARD)
                            })
                        }
                    }
                )
            }
        ) { padding ->
            AndroidView(
                modifier = Modifier.fillMaxSize().padding(padding),
                factory = { ctx ->
                    ActivityPostcardBinding.inflate(layoutInflater).also { binding ->
                        mBinding = binding
                        val diaryRepo = DiaryRepository()
                        val diaryDto = runBlocking { diaryRepo.findDiaryBy(mSequence) }!!
                        binding.run {
                            weather.visibility = View.GONE
                            if (diaryDto.title.isNullOrEmpty()) diaryTitle.visibility = View.GONE
                            else diaryTitle.text = diaryDto.title
                            applyMarkDownPolicy(contents, diaryDto.contents!!)
                            date.text = if (diaryDto.isAllDay) DateUtils.getDateStringFromTimeMillis(diaryDto.currentTimeMillis)
                            else DateUtils.getDateTimeStringForceFormatting(diaryDto.currentTimeMillis, this@PostcardActivity)
                            EasyDiaryUtils.boldString(applicationContext, diaryTitle)
                            mSavedState?.let { ss ->
                                setBackgroundColor(ss.getInt(POSTCARD_BG_COLOR, POSTCARD_BG_COLOR_VALUE))
                                setTextColor(ss.getInt(POSTCARD_TEXT_COLOR, POSTCARD_TEXT_COLOR_VALUE))
                            }
                            diaryDto.photoUris?.let { uris ->
                                if (uris.size > 0) {
                                    photoContainer.visibility = View.VISIBLE
                                    val items = uris.mapIndexed { index, uri ->
                                        PhotoAdapter.PostCardPhotoItem(EasyDiaryUtils.getApplicationDataDirectory(this@PostcardActivity) + uri.getFilePath(), index, 2, 0)
                                    }.toCollection(arrayListOf())
                                    mPhotoAdapter = PhotoAdapter(this@PostcardActivity, items) { resizePhotoGrid() }
                                    photoGrid.run {
                                        layoutManager = FlexboxLayoutManager(this@PostcardActivity).apply {
                                            flexWrap = FlexWrap.WRAP; flexDirection = FlexDirection.ROW
                                        }
                                        adapter = mPhotoAdapter
                                    }
                                    resizePhotoGrid()
                                }
                            }
                            fontSizeSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                                    mAddFontSize = progress - 20
                                    updateTextSize(postContainer, this@PostcardActivity, mAddFontSize)
                                }
                                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                            })
                        }
                    }.root
                }
            )
        }
    }

    private fun resizePhotoGrid() {
        mBinding.photoGrid.run {
            if (resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
                if (mPhotoAdapter.postCardPhotoItems.none { it.forceSinglePhotoPosition }) {
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                } else {
                    layoutParams.height = getDefaultDisplay().x
                }
            } else {
                layoutParams.width = calcPhotoGridHeight(this@PostcardActivity)
            }
        }
    }

    companion object {
        fun calcPhotoGridHeight(activity: android.app.Activity): Int {
            val point = activity.getDefaultDisplay()
            return point.y - activity.actionBarHeight() - activity.statusBarHeight() - activity.dpToPixel(30F, Calculation.CEIL)
        }
    }
}
