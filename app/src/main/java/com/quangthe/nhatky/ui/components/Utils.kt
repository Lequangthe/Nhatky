package com.quangthe.nhatky.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quangthe.nhatky.extensions.config
import androidx.compose.ui.platform.LocalContext

@Composable
fun SimpleText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: Float = LocalContext.current.config.settingFontSize,
    fontWeight: FontWeight? = null,
    fontColor: Color = Color(LocalContext.current.config.textColor),
    fontFamily: FontFamily? = null,
    alpha: Float = 1f,
    lineSpacingScaleFactor: Float = LocalContext.current.config.lineSpacingScaleFactor
) {
    Text(
        text = text,
        modifier = modifier.padding(vertical = (2 * lineSpacingScaleFactor).dp),
        fontSize = fontSize.sp,
        fontWeight = fontWeight,
        color = fontColor.copy(alpha = alpha),
        fontFamily = fontFamily,
        lineHeight = (fontSize * 1.2 * lineSpacingScaleFactor).sp
    )
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
