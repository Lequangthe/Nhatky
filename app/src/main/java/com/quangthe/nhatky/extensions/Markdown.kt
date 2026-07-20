package com.quangthe.nhatky.extensions

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.StyleSpan
import android.text.util.Linkify
import android.graphics.Typeface
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import com.quangthe.nhatky.commons.utils.FontUtils
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tables.TableTheme
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import io.noties.markwon.movement.MovementMethodPlugin
import io.noties.markwon.utils.Dip

fun Context.applyMarkDownPolicy(
    contentsView: TextView,
    contents: String,
    isTimeline: Boolean = false,
    lineBreakStrings: ArrayList<String> = arrayListOf(),
    isRecyclerItem: Boolean = false,
) {
    when (config.enableMarkdown) {
        true -> {
            val transformLineBreak = contents.replace("\n", "  \n")
            val mergedContents =
                if (lineBreakStrings.size > 1 &&
                    lineBreakStrings[1].isNotBlank()
                ) {
                    "${lineBreakStrings[1]}  \n$transformLineBreak"
                } else {
                    transformLineBreak
                }
            val timelineTitle =
                when {
                    isTimeline && config.boldStyleEnable -> "**${lineBreakStrings[0]}**"
                    isTimeline -> lineBreakStrings[0]
                    else -> ""
                }
            val markdownContents = if (isTimeline) "$timelineTitle  \n$mergedContents" else mergedContents
            val codeBlockTheme =
                object : AbstractMarkwonPlugin() {
                    override fun configureTheme(builder: MarkwonTheme.Builder) {
                        builder
                            .headingTextSizeMultipliers(floatArrayOf(1.3F, 1.2F, 1.1F, 1.0F, .83F, .67F))
                            .headingBreakHeight(0)
                            .codeBlockTextSize(config.settingFontSize.times(0.8).toInt())
                            .codeBlockBackgroundColor(config.backgroundColor.darkenColor())
                            .codeBlockTextColor(config.textColor)
                            .codeBackgroundColor(0x9FFFCC80.toInt())
                            .codeTypeface(FontUtils.getCommonTypeface(this@applyMarkDownPolicy)!!)
                            .codeTextColor(Color.BLACK)
                            .codeTextSize(config.settingFontSize.times(0.8).toInt())
                    }
                }
            val tablePlugin =
                TablePlugin.create { builder: TableTheme.Builder ->
                    val dip: Dip = Dip.create(this)
                    builder
                        .tableBorderWidth(dip.toPx(1))
                        .tableBorderColor(Color.BLACK)
                        .tableCellPadding(dip.toPx(6))
                        .tableHeaderRowBackgroundColor(
                            io.noties.markwon.utils.ColorUtils.applyAlpha(
                                config.primaryColor,
                                50,
                            ),
                        ).tableEvenRowBackgroundColor(config.backgroundColor)
                        .tableOddRowBackgroundColor(config.backgroundColor)
                }
            val strikeoutPlugin = StrikethroughPlugin.create()

            when (isRecyclerItem) {
                true -> {
                    Markwon
                        .builder(this)
                        .usePlugin(MovementMethodPlugin.none())
                        .usePlugin(codeBlockTheme)
                        .usePlugin(ImagesPlugin.create())
                        .usePlugin(HtmlPlugin.create())
                        .usePlugin(tablePlugin)
                        .usePlugin(strikeoutPlugin)
                        .build()
                        .apply {
                            setMarkdown(contentsView, markdownContents)
                        }
                }

                false -> {
                    Markwon
                        .builder(this)
                        .usePlugin(codeBlockTheme)
                        .usePlugin(ImagesPlugin.create())
                        .usePlugin(HtmlPlugin.create())
                        .usePlugin(tablePlugin)
                        .usePlugin(strikeoutPlugin)
                        .usePlugin(MovementMethodPlugin.link())
                        .usePlugin(LinkifyPlugin.create(Linkify.WEB_URLS))
                        .usePlugin(
                            object : AbstractMarkwonPlugin() {
                                override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
                                    super.configureConfiguration(builder)
                                    builder.linkResolver { view, link ->
                                        val customTabsIntent =
                                            CustomTabsIntent
                                                .Builder()
                                                .setToolbarColor(config.primaryColor)
                                                .setUrlBarHidingEnabled(true)
                                                .setShowTitle(false)
                                                .build()
                                        customTabsIntent.launchUrl(
                                            this@applyMarkDownPolicy,
                                            Uri.parse(link),
                                        )
                                    }
                                }
                            },
                        ).build()
                        .apply {
                            setMarkdown(contentsView, markdownContents)
                        }
                }
            }
        }

        false -> {
            contentsView.text =
                when (isTimeline) {
                    true -> {
                        val mergedContents =
                            if (lineBreakStrings.size > 1 &&
                                lineBreakStrings[1].isNotBlank()
                            ) {
                                "${lineBreakStrings[1]}\n$contents"
                            } else {
                                contents
                            }
                        applyBoldToDate(lineBreakStrings[0], mergedContents)
                    }

                    false -> {
                        contents
                    }
                }
        }
    }
}

fun Context.applyBoldToDate(
    dateString: String,
    summary: String,
): SpannableString {
    val spannableString = SpannableString("$dateString\n$summary")
    if (config.boldStyleEnable) spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, dateString.length, 0)
    return spannableString
}

fun Context.parsedMarkdownString(markdownString: String): Spanned = Markwon.builder(this).build().toMarkdown(markdownString)
