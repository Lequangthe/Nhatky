package com.quangthe.nhatky.commons.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.graphics.ColorUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import id.zelory.compressor.Compressor
import com.quangthe.nhatky.R
import com.quangthe.nhatky.adapters.SecondItemAdapter
import com.quangthe.nhatky.enums.Calculation
import com.quangthe.nhatky.extensions.checkPermission
import com.quangthe.nhatky.extensions.config
import com.quangthe.nhatky.extensions.dpToPixel
import com.quangthe.nhatky.extensions.getDefaultDisplay
import com.quangthe.nhatky.extensions.isLandScape
import com.quangthe.nhatky.extensions.updateDashboardInnerCard
import com.quangthe.nhatky.core.config.ATTACH_PHOTO_CARD_CONTENT_PADDING_DP
import com.quangthe.nhatky.core.config.ATTACH_PHOTO_CONTAINER_CARD_PADDING_DP
import com.quangthe.nhatky.core.config.ATTACH_PHOTO_MARGIN_DP
import com.quangthe.nhatky.core.config.BACKUP_DB_DIRECTORY
import com.quangthe.nhatky.core.config.ColorConstants
import com.quangthe.nhatky.core.config.DIARY_AUDIO_DIRECTORY
import com.quangthe.nhatky.core.config.DIARY_PHOTO_DIRECTORY
import com.quangthe.nhatky.core.config.DIARY_VIDEO_DIRECTORY
import com.quangthe.nhatky.core.config.EXTERNAL_STORAGE_PERMISSIONS
import com.quangthe.nhatky.core.config.MARKDOWN_DIRECTORY
import com.quangthe.nhatky.core.config.MIME_TYPE_JPEG
import com.quangthe.nhatky.core.config.PHOTO_CORNER_RADIUS_SCALE_FACTOR_NORMAL
import com.quangthe.nhatky.core.config.PHOTO_CORNER_RADIUS_SCALE_FACTOR_SMALL
import com.quangthe.nhatky.core.config.THUMBNAIL_BACKGROUND_ALPHA
import com.quangthe.nhatky.core.config.USER_CUSTOM_FONTS_DIRECTORY
import com.quangthe.nhatky.models.Diary
import com.quangthe.nhatky.models.PhotoUri
import com.quangthe.nhatky.ui.models.DiaryUiModel
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.FileReader
import java.util.Calendar
import java.util.Locale
import java.util.UUID

/**
 * Created by hanjoong on 2017-04-30.
 *
 * Delegates to split utility files. Kept for backward compatibility.
 */
object EasyDiaryUtils {
    val easyDiaryMimeType: String get() = com.quangthe.nhatky.commons.utils.easyDiaryMimeType
    val easyDiaryMimeTypeAll: Array<String?> get() = com.quangthe.nhatky.commons.utils.easyDiaryMimeTypeAll

    fun summaryDiaryLabel(diary: Diary): String = com.quangthe.nhatky.commons.utils.summaryDiaryLabel(diary)
    fun summaryDiaryLabel(diary: DiaryUiModel): String = com.quangthe.nhatky.commons.utils.summaryDiaryLabel(diary)
    fun searchWordIndexes(contents: String, searchWord: String): List<Int> = com.quangthe.nhatky.commons.utils.searchWordIndexes(contents, searchWord)

    fun isNumberString(string: String?): Boolean = com.quangthe.nhatky.commons.utils.isNumberString(string)
    fun isContainNumber(string: String?): Boolean = com.quangthe.nhatky.commons.utils.isContainNumber(string)
    fun isStockNumber(string: String?): Boolean = com.quangthe.nhatky.commons.utils.isStockNumber(string)
    fun findNumber(string: String?): Float = com.quangthe.nhatky.commons.utils.findNumber(string)

    fun datePickerToTimeMillis(dayOfMonth: Int, month: Int, year: Int, isFullHour: Boolean = false, hour: Int = 0, minute: Int = 0, second: Int = 0): Long = com.quangthe.nhatky.commons.utils.datePickerToTimeMillis(dayOfMonth, month, year, isFullHour, hour, minute, second)
    fun convDateToTimeMillis(field: Int, amount: Int, isZeroHour: Boolean = true, isZeroMinute: Boolean = true, isZeroSecond: Boolean = true, isZeroMilliSecond: Boolean = true): Long = com.quangthe.nhatky.commons.utils.convDateToTimeMillis(field, amount, isZeroHour, isZeroMinute, isZeroSecond, isZeroMilliSecond)
    fun convDateToTimeMillis(isFullHour: Boolean = false, addYears: Int = 0): Long = com.quangthe.nhatky.commons.utils.convDateToTimeMillis(isFullHour, addYears)
    fun getCalendarInstance(isFullHour: Boolean = false, addYears: Int = 0): Calendar = com.quangthe.nhatky.commons.utils.getCalendarInstance(isFullHour, addYears)
    fun getCalendarInstance(isFullHour: Boolean = false, field: Int, amount: Int): Calendar = com.quangthe.nhatky.commons.utils.getCalendarInstance(isFullHour, field, amount)

    fun createBackgroundGradientDrawable(color: Int, alpha: Int, cornerRadius: Float): Drawable = com.quangthe.nhatky.commons.utils.createBackgroundGradientDrawable(color, alpha, cornerRadius)
    fun createThumbnailGlideOptions(radius: Float, isEncrypt: Boolean = false): RequestOptions = com.quangthe.nhatky.commons.utils.createThumbnailGlideOptions(radius, isEncrypt)
    fun createThumbnailGlideOptions(radius: Int, isEncrypt: Boolean = false): RequestOptions = com.quangthe.nhatky.commons.utils.createThumbnailGlideOptions(radius, isEncrypt)
    fun downSamplingImage(context: Context, uri: Uri, destFile: File): String = com.quangthe.nhatky.commons.utils.downSamplingImage(context, uri, destFile)
    fun downSamplingImage(context: Context, srcFile: File, destFile: File) = com.quangthe.nhatky.commons.utils.downSamplingImage(context, srcFile, destFile)
    fun photoUriToDownSamplingBitmap(context: Context, photoUri: PhotoUri, requiredSize: Int = 50, fixedWidth: Int = 45, fixedHeight: Int = 45): Bitmap = com.quangthe.nhatky.commons.utils.photoUriToDownSamplingBitmap(context, photoUri, requiredSize, fixedWidth, fixedHeight)
    fun photoUriToBitmap(context: Context, photoUri: PhotoUri): Bitmap? = com.quangthe.nhatky.commons.utils.photoUriToBitmap(context, photoUri)
    fun createAttachedPhotoView(context: Context, photoUri: PhotoUri, marginLeft: Float = 0F, marginTop: Float = 0F, marginRight: Float = 3F, marginBottom: Float = 0F): ImageView = com.quangthe.nhatky.commons.utils.createAttachedPhotoView(context, photoUri, marginLeft, marginTop, marginRight, marginBottom)
    fun createAttachedPhotoViewForFlexBox(activity: Activity, photoUri: PhotoUri, attachedCount: Int): CardView = com.quangthe.nhatky.commons.utils.createAttachedPhotoViewForFlexBox(activity, photoUri, attachedCount)

    fun initWorkingDirectory(context: Context) = com.quangthe.nhatky.commons.utils.initWorkingDirectory(context)
    fun getExternalStorageDirectory(): File = com.quangthe.nhatky.commons.utils.getExternalStorageDirectory()
    fun initLegacyWorkingDirectory(context: Context) = com.quangthe.nhatky.commons.utils.initLegacyWorkingDirectory(context)
    fun getApplicationDataDirectory(context: Context): String = com.quangthe.nhatky.commons.utils.getApplicationDataDirectory(context)
    fun readFileWithSAF(mimeType: String, activityResultLauncher: ActivityResultLauncher<Intent>) = com.quangthe.nhatky.commons.utils.readFileWithSAF(mimeType, activityResultLauncher)
    fun writeFileWithSAF(fileName: String, mimeType: String, activityResultLauncher: ActivityResultLauncher<Intent>) = com.quangthe.nhatky.commons.utils.writeFileWithSAF(fileName, mimeType, activityResultLauncher)
    fun queryName(resolver: ContentResolver, uri: Uri): String = com.quangthe.nhatky.commons.utils.queryName(resolver, uri)

    fun boldString(context: Context, textView: TextView?) = com.quangthe.nhatky.commons.utils.boldString(context, textView)
    fun boldStringForce(textView: TextView?) = com.quangthe.nhatky.commons.utils.boldStringForce(textView)
    fun warningString(textView: TextView) = com.quangthe.nhatky.commons.utils.warningString(textView)
    fun highlightString(textView: TextView) = com.quangthe.nhatky.commons.utils.highlightString(textView)
    fun highlightStringIgnoreCase(textView: TextView?, input: String?, highlightColor: Int = ColorConstants.HIGHLIGHT_COLOR) = com.quangthe.nhatky.commons.utils.highlightStringIgnoreCase(textView, input, highlightColor)
    fun highlightString(textView: TextView?, input: String?, highlightColor: Int = ColorConstants.HIGHLIGHT_COLOR) = com.quangthe.nhatky.commons.utils.highlightString(textView, input, highlightColor)
    fun removeSpans(spannableString: SpannableString) = com.quangthe.nhatky.commons.utils.removeSpans(spannableString)
    fun disableTouchEvent(view: View) = com.quangthe.nhatky.commons.utils.disableTouchEvent(view)
    fun applyMarkDownEllipsize(textContents: TextView, sequence: Int, delayMillis: Long = 0) = com.quangthe.nhatky.commons.utils.applyMarkDownEllipsize(textContents, sequence, delayMillis)

    fun createSecondsPickerBuilder(context: Context, itemClickListener: AdapterView.OnItemClickListener, second: Int): AlertDialog.Builder = com.quangthe.nhatky.commons.utils.createSecondsPickerBuilder(context, itemClickListener, second)
    fun openCustomOptionMenu(content: View, parent: View): PopupWindow = com.quangthe.nhatky.commons.utils.openCustomOptionMenu(content, parent)

    fun sequenceToPageIndex(diaryList: List<Diary>, sequence: Int): Int = com.quangthe.nhatky.commons.utils.sequenceToPageIndex(diaryList, sequence)
    fun jsonStringToHashMap(jsonString: String): HashMap<String, Any> = com.quangthe.nhatky.commons.utils.jsonStringToHashMap(jsonString)
    fun jsonFileToHashMap(filename: String): HashMap<String, Any> = com.quangthe.nhatky.commons.utils.jsonFileToHashMap(filename)
    fun hashMapToJsonString(map: HashMap<String, Any>): String = com.quangthe.nhatky.commons.utils.hashMapToJsonString(map)
    fun fromHtml(target: String): Spanned = com.quangthe.nhatky.commons.utils.fromHtml(target)
}
