package com.quangthe.nhatky.models

import com.quangthe.nhatky.helper.CONTENT_URI_PREFIX
import com.quangthe.nhatky.helper.FILE_URI_PREFIX
import com.quangthe.nhatky.helper.WORKING_DIRECTORY

/**
 * Created by hanjoong on 2017-06-08.
 */

data class PhotoUri(
    var photoUri: String? = null,
    var mimeType: String? = null
) {
    fun isContentUri(): Boolean = photoUri?.startsWith(CONTENT_URI_PREFIX) ?: false

    fun getFilePath(): String {
        val raw = photoUri ?: return ""
        val path = raw.removePrefix(FILE_URI_PREFIX)
        val idx = path.indexOf(WORKING_DIRECTORY)
        return if (idx >= 0) path.substring(idx) else raw
    }

    fun isEncrypt(): Boolean = photoUri?.isEmpty() ?: false

    fun isAudio(): Boolean = mimeType?.startsWith("audio") ?: false

    fun isVideo(): Boolean = mimeType?.startsWith("video") ?: false
}
