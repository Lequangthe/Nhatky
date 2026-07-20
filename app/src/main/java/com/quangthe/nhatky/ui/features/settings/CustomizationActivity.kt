package com.quangthe.nhatky.ui.features.settings

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.quangthe.nhatky.R
import com.quangthe.nhatky.ui.base.EasyDiaryComposeBaseActivity
import com.quangthe.nhatky.extensions.config
import com.quangthe.nhatky.extensions.toggleLauncher
import com.quangthe.nhatky.core.config.DARK_THEME_BACKGROUND_COLOR
import com.quangthe.nhatky.core.config.DARK_THEME_PRIMARY_COLOR
import com.quangthe.nhatky.core.config.DARK_THEME_SCREEN_BACKGROUND_COLOR
import com.quangthe.nhatky.core.config.DARK_THEME_TEXT_COLOR
import com.quangthe.nhatky.core.config.EASYDIARY_THEME_BACKGROUND_COLOR
import com.quangthe.nhatky.core.config.EASYDIARY_THEME_PRIMARY_COLOR
import com.quangthe.nhatky.core.config.EASYDIARY_THEME_SCREEN_BACKGROUND_COLOR
import com.quangthe.nhatky.core.config.EASYDIARY_THEME_TEXT_COLOR
import com.quangthe.nhatky.core.config.GREEN_THEME_BACKGROUND_COLOR
import com.quangthe.nhatky.core.config.GREEN_THEME_PRIMARY_COLOR
import com.quangthe.nhatky.core.config.GREEN_THEME_SCREEN_BACKGROUND_COLOR
import com.quangthe.nhatky.core.config.GREEN_THEME_TEXT_COLOR
import com.quangthe.nhatky.ui.theme.AppTheme

class CustomizationActivity : EasyDiaryComposeBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                CustomizationScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun CustomizationScreen() {
        val context = LocalContext.current
        var primaryColor by remember { mutableStateOf(Color(context.config.primaryColor)) }
        var textColor by remember { mutableStateOf(Color(context.config.textColor)) }
        var backgroundColor by remember { mutableStateOf(Color(context.config.backgroundColor)) }
        var screenBgColor by remember { mutableStateOf(Color(context.config.screenBackgroundColor)) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Customization") },
                    navigationIcon = {
                        IconButton(onClick = { finish() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Preset Themes", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeChip("Easy Diary", Color(AndroidColor.parseColor(EASYDIARY_THEME_PRIMARY_COLOR))) {
                        primaryColor = Color(AndroidColor.parseColor(EASYDIARY_THEME_PRIMARY_COLOR))
                        backgroundColor = Color(AndroidColor.parseColor(EASYDIARY_THEME_BACKGROUND_COLOR))
                        screenBgColor = Color(AndroidColor.parseColor(EASYDIARY_THEME_SCREEN_BACKGROUND_COLOR))
                        textColor = Color(AndroidColor.parseColor(EASYDIARY_THEME_TEXT_COLOR))
                    }
                    ThemeChip("Dark", Color(AndroidColor.parseColor(DARK_THEME_PRIMARY_COLOR))) {
                        primaryColor = Color(AndroidColor.parseColor(DARK_THEME_PRIMARY_COLOR))
                        backgroundColor = Color(AndroidColor.parseColor(DARK_THEME_BACKGROUND_COLOR))
                        screenBgColor = Color(AndroidColor.parseColor(DARK_THEME_SCREEN_BACKGROUND_COLOR))
                        textColor = Color(AndroidColor.parseColor(DARK_THEME_TEXT_COLOR))
                    }
                    ThemeChip("Green", Color(AndroidColor.parseColor(GREEN_THEME_PRIMARY_COLOR))) {
                        primaryColor = Color(AndroidColor.parseColor(GREEN_THEME_PRIMARY_COLOR))
                        backgroundColor = Color(AndroidColor.parseColor(GREEN_THEME_BACKGROUND_COLOR))
                        screenBgColor = Color(AndroidColor.parseColor(GREEN_THEME_SCREEN_BACKGROUND_COLOR))
                        textColor = Color(AndroidColor.parseColor(GREEN_THEME_TEXT_COLOR))
                    }
                }

                HsvColorSection("Primary Color", primaryColor) { primaryColor = it }
                HsvColorSection("Text Color", textColor) { textColor = it }
                HsvColorSection("Background Color", backgroundColor) { backgroundColor = it }
                HsvColorSection("Screen Background Color", screenBgColor) { screenBgColor = it }

                Button(
                    onClick = {
                        context.config.textColor = textColor.toArgb()
                        context.config.backgroundColor = backgroundColor.toArgb()
                        context.config.screenBackgroundColor = screenBgColor.toArgb()
                        context.config.primaryColor = primaryColor.toArgb()
                        context.config.isThemeChanged = true
                        finish()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }
            }
        }
    }

    @Composable
    private fun ThemeChip(name: String, color: Color, onClick: () -> Unit) {
        Card(
            modifier = Modifier.clickable(onClick = onClick),
            colors = CardDefaults.cardColors(containerColor = color)
        ) {
            Text(name, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = Color.White)
        }
    }

    @Composable
    private fun HsvColorSection(label: String, currentColor: Color, onColorChanged: (Color) -> Unit) {
        val arr = floatArrayOf(0f, 0f, 0f)
        AndroidColor.colorToHSV(currentColor.toArgb(), arr)
        var hue by remember(currentColor) { mutableFloatStateOf(arr[0]) }
        var sat by remember(currentColor) { mutableFloatStateOf(arr[1]) }
        var bri by remember(currentColor) { mutableFloatStateOf(arr[2]) }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(label, style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Color.hsv(hue, sat.coerceIn(0.01f, 1f), bri.coerceIn(0.01f, 1f))
                        )
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text("Hue", style = MaterialTheme.typography.labelSmall)
                Slider(
                    value = hue, onValueChange = { hue = it; onColorChanged(Color.hsv(hue, sat, bri)) },
                    valueRange = 0f..360f,
                    colors = SliderDefaults.colors(thumbColor = Color.hsv(hue, 1f, 1f), activeTrackColor = Color.hsv(hue, 1f, 1f))
                )

                Text("Saturation", style = MaterialTheme.typography.labelSmall)
                Slider(
                    value = sat, onValueChange = { sat = it; onColorChanged(Color.hsv(hue, sat, bri)) },
                    valueRange = 0f..1f
                )

                Text("Brightness", style = MaterialTheme.typography.labelSmall)
                Slider(
                    value = bri, onValueChange = { bri = it; onColorChanged(Color.hsv(hue, sat, bri)) },
                    valueRange = 0f..1f
                )
            }
        }
    }
}
