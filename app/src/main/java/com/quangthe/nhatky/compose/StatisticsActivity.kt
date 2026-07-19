package com.quangthe.nhatky.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentContainerView
import com.quangthe.nhatky.R
import com.quangthe.nhatky.extensions.config
import com.quangthe.nhatky.fragments.StockLineChartFragment
import com.quangthe.nhatky.fragments.WeightLineChartFragment
import com.quangthe.nhatky.fragments.WritingBarChartFragment
import com.quangthe.nhatky.helper.StatisticsConstants
import com.quangthe.nhatky.ui.theme.AppTheme

class StatisticsActivity : EasyDiaryComposeBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                StatisticsScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun StatisticsScreen() {
        val mode = intent.getStringExtra(StatisticsConstants.CHART_MODE)
        val title = when (mode) {
            StatisticsConstants.MODE_SINGLE_LINE_CHART_WEIGHT -> "Weight"
            StatisticsConstants.MODE_SINGLE_LINE_CHART_STOCK -> "Stock"
            else -> getString(R.string.statistics_creation_time)
        }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = { finish() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            AndroidView(
                modifier = Modifier.fillMaxSize().padding(padding),
                factory = { context ->
                    FragmentContainerView(context).also { container ->
                        container.id = android.view.View.generateViewId()
                        val fragment = when (mode) {
                            StatisticsConstants.MODE_SINGLE_LINE_CHART_WEIGHT -> WeightLineChartFragment()
                            StatisticsConstants.MODE_SINGLE_LINE_CHART_STOCK -> StockLineChartFragment()
                            else -> WritingBarChartFragment()
                        }
                        supportFragmentManager.beginTransaction()
                            .replace(container.id, fragment)
                            .commit()
                        supportFragmentManager.executePendingTransactions()
                    }
                }
            )
        }
    }
}
