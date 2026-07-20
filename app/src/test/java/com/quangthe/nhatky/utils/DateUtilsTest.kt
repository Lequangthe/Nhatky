package com.quangthe.nhatky.utils

import com.quangthe.nhatky.commons.utils.DateUtils
import com.quangthe.nhatky.core.config.DateUtilConstants
import org.junit.Assert
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by CHO HANJOONG on 2018-03-04.
 */

class DateUtilsTest {
    val timeMillis: Long = 1520149040913

    @Test
    fun timeMillisToDateTime() {
        Assert.assertEquals(
            "2018-03-04",
            DateUtils.timeMillisToDateTime(
                timeMillis,
                DateUtilConstants.DATE_PATTERN_DASH,
            ),
        )
    }

    @Test
    fun timeMillisToHour() {
        Assert.assertEquals("16", DateUtils.timeMillisToDateTime(timeMillis, "HH"))
    }

//    @Test
//    fun getCurrentDateTime() {
//        Assert.assertEquals("20180304_170749", DateUtils.getCurrentDateTime("yyyyMMdd_HHmmss"))
//    }

    @Test
    fun getFullPatternDateWithTimeAndSeconds01() {
        Assert.assertEquals(
            "2018년 3월 4일 일요일 16:37 20",
            DateUtils.getDateTimeStringFromTimeMillis(timeMillis),
        )
    }

    @Test
    fun getFullPatternDateWithTimeAndSeconds02() {
        Assert.assertEquals(
            "dimanche 4 mars 2018 16:37 20",
            DateUtils.getDateTimeStringFromTimeMillis(
                timeMillis,
                SimpleDateFormat.FULL,
                SimpleDateFormat.FULL,
                null,
                Locale.FRANCE,
            ),
        )
    }

    @Test
    fun getDateStringFromTimeMillis01() {
        Assert.assertEquals(
            "2018년 3월 4일 일요일 16:37 20",
            DateUtils.getDateStringFromTimeMillis(timeMillis, SimpleDateFormat.FULL),
        )
    }
}
