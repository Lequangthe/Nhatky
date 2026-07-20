package com.quangthe.nhatky.extensions

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.quangthe.nhatky.commons.utils.BitmapUtils
import com.quangthe.nhatky.commons.utils.DateUtils
import com.quangthe.nhatky.commons.utils.EasyDiaryUtils
import com.quangthe.nhatky.models.Diary
import id.zelory.compressor.Compressor
import org.apache.commons.codec.binary.Base64
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat

fun Activity.exportHtmlBook(
    uri: Uri?,
    diaryList: List<Diary>,
) {
    uri?.let {
        val os = contentResolver.openOutputStream(it)
        IOUtils.write(createHtmlString(diaryList), os, "UTF-8")
        os?.close()
    }
}

fun Activity.photoToBase64(photoPath: String): String {
    var image64 = ""
    val bos = ByteArrayOutputStream()
    try {
        val bitmap = BitmapUtils.cropCenter(BitmapFactory.decodeFile(photoPath))
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, bos)
        image64 = Base64.encodeBase64String(bos.toByteArray())
    } catch (e: Exception) {
        bos.close()
    }
    return image64
}

fun Activity.createHtmlString(diaryList: List<Diary>): String {
    val diaryDivision = StringBuilder()
    diaryList.forEach {
        val html = StringBuilder()
        html.append("<div class='title'> <div class='title-right'>${it.title}</div> </div>")
        html.append(
            "<div class='datetime'>${DateUtils.getDateTimeStringFromTimeMillis(
                it.currentTimeMillis,
                SimpleDateFormat.FULL,
                SimpleDateFormat.FULL,
            )}</div>",
        )
        html.append("<pre class='contents'>")
        html.append(it.contents)
        html.append("</pre>")
        html.append("<div class='photo-container'>")

        it.photoUris?.let { photoUriList ->
            val imageColumn =
                when (photoUriList.size) {
                    1 -> 1
                    else -> 2
                }
            photoUriList.forEach { photoUriDto ->
                html.append(
                    "<div class='photo col$imageColumn'><img src='data:image/png;base64, ${photoToBase64(
                        EasyDiaryUtils.getApplicationDataDirectory(this) + photoUriDto.getFilePath(),
                    )}' /></div>",
                )
            }
        }
        html.append("</div>")
        html.append("<hr>")
        diaryDivision.append(html.toString())
    }

    val template = StringBuilder()
    template.append("<!DOCTYPE html>")
    template.append("<html>")
    template.append("<head>")
    template.append("   <meta charset='UTF-8'>")
    template.append("   <meta name='viewport' content='width=device-width, initial-scale=1.0'>")
    template.append("   <title>Insert title here</title>")
    template.append("   <style type='text/css'>")
    template.append("       body { margin: 1rem; font-family: ë‚˜ëˆ”ê³ ë”•, monospace; }")
    template.append("       hr { margin: 1.5rem 0 }")
    template.append("       .title { margin-top: 1rem; font-size: 1.3rem; display: flex; }")
    template.append("       .title img { width: 30px; margin-right: 1rem; display: block; }")
    template.append("       .title-left { display:inline-block; }")
    template.append("       .title-right { display:inline-block; white-space: pre-wrap; word-break: break-all; }")
    template.append("       .datetime { font-size: 0.8rem; text-align: right; }")
    template.append(
        "       .contents { margin-top: 1rem; font-size: 0.9rem; font-family: ë‚˜ëˆ”ê³ ë”•, monospace; white-space: pre-wrap; word-break: break-all; }",
    )
    template.append("       .photo-container { display: flex; flex-wrap: wrap; }")
    template.append(
        "       .photo-container .photo { background: rgb(240 239 240); padding: 0.3rem; border-radius: 5px; margin: 0.25rem; box-sizing: border-box; }",
    )
    template.append("       .photo.col1 { width: calc(100% - 0.5rem); }")
    template.append("       .photo.col2 { width: calc(50% - 0.5rem); }")
    template.append("       .photo img { width: 100%; display: block; border-radius: 5px; }")
    template.append("   </style>")
    template.append("<body>")
    template.append(diaryDivision.toString())
    template.append("</body>")
    template.append("</html>")

    return template.toString()
}

fun Activity.uriToFile(
    uri: Uri,
    photoPath: String,
): Boolean {
    var result = false
    try {
        val tempFile = File.createTempFile("TEMP_PHOTO", "AAF").apply { deleteOnExit() }
        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(tempFile)
        IOUtils.copy(inputStream, outputStream)
        IOUtils.closeQuietly(inputStream)
        IOUtils.closeQuietly(outputStream)

        val compressedFile = Compressor(this).setQuality(70).compressToFile(tempFile)
        FileUtils.copyFile(compressedFile, File(photoPath))
        result = true
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return result
}
