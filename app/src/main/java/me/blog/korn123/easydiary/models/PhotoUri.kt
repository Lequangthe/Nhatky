package me.blog.korn123.easydiary.models

import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import me.blog.korn123.easydiary.helper.CONTENT_URI_PREFIX
import me.blog.korn123.easydiary.helper.FILE_URI_PREFIX
import me.blog.korn123.easydiary.helper.WORKING_DIRECTORY


/**
 * Created by hanjoong on 2017-06-08.
 */

open class PhotoUri : RealmObject {
    @LinkingObjects("photoUris")
    val diary: RealmResults<Diary>? = null
    var photoUri: String? = null
    var mimeType: String? = null

    constructor()

    constructor(photoUri: String) {
        this.photoUri = photoUri
    }

    constructor(photoUri: String, mimeType: String) {
        this.photoUri = photoUri
        this.mimeType = mimeType
    }

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
