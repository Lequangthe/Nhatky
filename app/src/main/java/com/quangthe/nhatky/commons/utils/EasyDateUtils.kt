package com.quangthe.nhatky.commons.utils

import java.util.Calendar
import java.util.Locale

fun datePickerToTimeMillis(
    dayOfMonth: Int,
    month: Int,
    year: Int,
    isFullHour: Boolean = false,
    hour: Int = 0,
    minute: Int = 0,
    second: Int = 0,
): Long {
    val cal = Calendar.getInstance(Locale.getDefault())
    cal.set(Calendar.YEAR, year)
    cal.set(Calendar.MONTH, month)
    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
    cal.set(Calendar.HOUR_OF_DAY, if (isFullHour) 23 else hour)
    cal.set(Calendar.MINUTE, if (isFullHour) 59 else minute)
    cal.set(Calendar.SECOND, if (isFullHour) 59 else second)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

fun convDateToTimeMillis(
    field: Int,
    amount: Int,
    isZeroHour: Boolean = true,
    isZeroMinute: Boolean = true,
    isZeroSecond: Boolean = true,
    isZeroMilliSecond: Boolean = true,
): Long {
    val calendar = Calendar.getInstance(Locale.getDefault())
    if (isZeroHour) calendar.set(Calendar.HOUR_OF_DAY, 0)
    if (isZeroMinute) calendar.set(Calendar.MINUTE, 0)
    if (isZeroSecond) calendar.set(Calendar.SECOND, 0)
    if (isZeroMilliSecond) calendar.set(Calendar.MILLISECOND, 0)
    if (amount != 0) {
        calendar.add(field, amount)
    }
    return calendar.timeInMillis
}

fun convDateToTimeMillis(
    isFullHour: Boolean = false,
    addYears: Int = 0,
): Long {
    val cal = Calendar.getInstance(Locale.getDefault())
    cal.set(Calendar.HOUR_OF_DAY, if (isFullHour) 23 else 0)
    cal.set(Calendar.MINUTE, if (isFullHour) 59 else 0)
    cal.set(Calendar.SECOND, if (isFullHour) 59 else 0)
    if (addYears != 0) cal.add(Calendar.YEAR, addYears)
    return cal.timeInMillis
}

fun getCalendarInstance(
    isFullHour: Boolean = false,
    addYears: Int = 0,
): Calendar = getCalendarInstance(isFullHour, Calendar.YEAR, addYears)

fun getCalendarInstance(
    isFullHour: Boolean = false,
    field: Int,
    amount: Int,
): Calendar {
    val cal = Calendar.getInstance(Locale.getDefault())
    cal.set(Calendar.HOUR_OF_DAY, if (isFullHour) 23 else 0)
    cal.set(Calendar.MINUTE, if (isFullHour) 59 else 0)
    cal.set(Calendar.SECOND, if (isFullHour) 59 else 0)
    cal.set(Calendar.MILLISECOND, if (isFullHour) 999 else 0)
    if (amount != 0) cal.add(field, amount)
    return cal
}
