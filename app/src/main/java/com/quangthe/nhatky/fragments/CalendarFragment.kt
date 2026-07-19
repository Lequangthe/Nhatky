package com.quangthe.nhatky.fragments

import com.roomorama.caldroid.CaldroidFragmentEx
import com.roomorama.caldroid.CaldroidGridAdapter
import com.quangthe.nhatky.R
import com.quangthe.nhatky.adapters.CaldroidItemAdapter
import com.quangthe.nhatky.adapters.WeekdayArrayAdapter
import com.quangthe.nhatky.extensions.config

class CalendarFragment : CaldroidFragmentEx() {
    override fun getBackgroundColor(): Int {
        return context?.config?.backgroundColor ?: 0
    }

    override fun getNewDatesGridAdapter(month: Int, year: Int): CaldroidGridAdapter {
        return CaldroidItemAdapter(requireActivity(), month, year,
                getCaldroidData(), extraData)
    }

    override fun getNewWeekdayAdapter(themeResource: Int): WeekdayArrayAdapter {
        return WeekdayArrayAdapter(
                requireActivity(), R.layout.item_weekday,
                daysOfWeek, themeResource)
    }
}
