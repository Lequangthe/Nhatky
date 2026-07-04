package me.blog.korn123.commons.utils

import android.content.Context
import android.view.View
import android.widget.ImageView
import me.blog.korn123.easydiary.R
import me.blog.korn123.easydiary.helper.*
import me.blog.korn123.easydiary.models.DiarySymbol

object FlavorUtils {
    fun sequenceToSymbolResourceId(sequence: Int) = when (sequence) {
        SYMBOL_SELECT_ALL -> R.drawable.ic_select_symbol
        WEATHER_SUNNY -> R.drawable.ic_sunny
        WEATHER_CLOUD_AND_SUN -> R.drawable.ic_clouds_and_sun
        else -> 0
    }

    fun initWeatherView(context: Context, imageView: ImageView?, weatherFlag: Int, isShowEmptyWeatherView: Boolean = false, applyWhiteFilter: Boolean = false) {
        imageView?.run {
            visibility = if (!isShowEmptyWeatherView && weatherFlag < 1) View.GONE else View.VISIBLE
            setImageResource(sequenceToSymbolResourceId(weatherFlag))
        }
    }

    fun getDiarySymbolMap(context: Context): HashMap<Int, String> {
        val symbolMap = hashMapOf<Int, String>()
        val symbolArray = arrayOf(
            *context.resources.getStringArray(R.array.weather_item_array),
        )
        symbolArray.map { item ->
            val symbolItem = DiarySymbol(item)
            symbolMap.put(symbolItem.sequence, symbolItem.description)
        }
        return symbolMap
    }
}
