package com.quangthe.nhatky.compose

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentContainerView
import com.quangthe.nhatky.R
import com.quangthe.nhatky.extensions.config
import com.quangthe.nhatky.extensions.isLandScape
import com.quangthe.nhatky.fragments.CalendarFragment
import com.quangthe.nhatky.helper.CALENDAR_SORTING_ASC
import com.quangthe.nhatky.helper.DEFAULT_CALENDAR_FONT_SCALE
import com.quangthe.nhatky.helper.DIARY_SEQUENCE
import com.quangthe.nhatky.helper.DateUtilConstants
import com.quangthe.nhatky.repositories.DiaryRepository
import com.quangthe.nhatky.helper.SettingConstants
import com.quangthe.nhatky.helper.TransitionHelper
import com.quangthe.nhatky.models.Diary
import com.quangthe.nhatky.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarActivity : EasyDiaryComposeBaseActivity() {
    private val mCalendar = Calendar.getInstance(Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val savedState = savedInstanceState
        setContent {
            AppTheme {
                CalendarScreen(savedState)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun CalendarScreen(savedState: Bundle?) {
        val context = LocalContext.current
        var selectedDate by remember { mutableStateOf(mCalendar.time) }
        var diaryList by remember { mutableStateOf<List<Diary>>(emptyList()) }
        var calendarFragmentRef by remember { mutableStateOf<CalendarFragment?>(null) }

        val diaryRepo = remember { DiaryRepository() }

        LaunchedEffect(selectedDate) {
            val formatter = SimpleDateFormat(DateUtilConstants.DATE_PATTERN_DASH, Locale.getDefault())
            val ascending = config.calendarSorting == CALENDAR_SORTING_ASC
            diaryList = diaryRepo.findDiaryByDateString(formatter.format(selectedDate), ascending)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(context.getString(R.string.calendar_title)) },
                    navigationIcon = {
                        IconButton(onClick = { finish() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { calendarFragmentRef?.prevMonth() }) {
                            Icon(Icons.Filled.NavigateBefore, contentDescription = "Previous")
                        }
                        IconButton(onClick = { calendarFragmentRef?.nextMonth() }) {
                            Icon(Icons.Filled.NavigateNext, contentDescription = "Next")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    TransitionHelper.startActivityWithTransition(
                        this@CalendarActivity,
                        Intent(this@CalendarActivity, DiaryWritingActivity::class.java).apply {
                            putExtra(SettingConstants.INITIALIZE_TIME_MILLIS, selectedDate.time)
                        }
                    )
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add")
                }
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxWidth(),
                        factory = { ctx ->
                            FragmentContainerView(ctx).also { container ->
                                container.id = android.view.View.generateViewId()
                                val cf = CalendarFragment()
                                calendarFragmentRef = cf

                                if (savedState != null) {
                                    cf.restoreStatesFromKey(savedState, "CALDROID_SAVED_STATE")
                                } else {
                                    val cal = Calendar.getInstance()
                                    val args = Bundle()
                                    args.putInt("month", cal.get(Calendar.MONTH) + 1)
                                    args.putInt("year", cal.get(Calendar.YEAR))
                                    args.putInt("startDayOfWeek", config.calendarStartDay)
                                    args.putBoolean("enableSwipe", true)
                                    args.putBoolean("sixWeeksInCalendar", true)
                                    cf.arguments = args
                                }

                                cf.setSelectedDate(selectedDate)
                                cf.caldroidListener = object : com.roomorama.caldroid.CaldroidListener() {
                                    override fun onSelectDate(date: Date, view: android.view.View) {
                                        selectedDate = date
                                        mCalendar.time = date
                                    }
                                    override fun onChangeMonth(month: Int, year: Int) {}
                                    override fun onLongClickDate(date: Date?, view: android.view.View?) {}
                                    override fun onCaldroidViewCreated() {}
                                }

                                supportFragmentManager.beginTransaction()
                                    .replace(container.id, cf)
                                    .commit()
                            }
                        }
                    )
                }

                if (diaryList.isEmpty()) {
                    Text(
                        context.getString(R.string.no_diary_message),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(diaryList, key = { it.sequence }) { diary ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp).clickable {
                                    val intent = Intent(this@CalendarActivity, DiaryReadingActivity::class.java)
                                    intent.putExtra(DIARY_SEQUENCE, diary.sequence)
                                    TransitionHelper.startActivityWithTransition(this@CalendarActivity, intent)
                                },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Text(
                                    "${diary.title ?: ""}",
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
