package com.quangthe.nhatky.commons.utils

fun isNumberString(string: String?): Boolean = string?.toFloatOrNull() != null

fun isContainNumber(string: String?): Boolean = string?.contains("\\d+\\.?\\d+".toRegex()) ?: false

fun isStockNumber(string: String?): Boolean = "$string,".matches("^(\\d+,)+$".toRegex())

fun findNumber(string: String?): Float {
    var number = 0f
    string?.let {
        val intRange = "\\d+\\.?\\d+".toRegex().find(it)?.range ?: IntRange(0, 0)
        number = string.substring(intRange).toFloat()
    }
    return number
}
