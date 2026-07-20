package com.quangthe.nhatky.commons.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.net.Uri
import android.os.Build
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.core.graphics.ColorUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.quangthe.nhatky.R
import com.quangthe.nhatky.core.config.ATTACH_PHOTO_CARD_CONTENT_PADDING_DP
import com.quangthe.nhatky.core.config.ATTACH_PHOTO_CONTAINER_CARD_PADDING_DP
import com.quangthe.nhatky.core.config.ATTACH_PHOTO_MARGIN_DP
import com.quangthe.nhatky.core.config.MIME_TYPE_JPEG
import com.quangthe.nhatky.core.config.PHOTO_CORNER_RADIUS_SCALE_FACTOR_NORMAL
import com.quangthe.nhatky.core.config.PHOTO_CORNER_RADIUS_SCALE_FACTOR_SMALL
import com.quangthe.nhatky.core.config.THUMBNAIL_BACKGROUND_ALPHA
import com.quangthe.nhatky.enums.Calculation
import com.quangthe.nhatky.extensions.config
import com.quangthe.nhatky.extensions.dpToPixel
import com.quangthe.nhatky.extensions.getDefaultDisplay
import com.quangthe.nhatky.extensions.isLandScape
import com.quangthe.nhatky.extensions.updateDashboardInnerCard
import com.quangthe.nhatky.models.PhotoUri
import id.zelory.compressor.Compressor
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.UUID

fun createBackgroundGradientDrawable(
    color: Int,
    alpha: Int,
    cornerRadius: Float,
): Drawable {
    val gradientDrawable =
        GradientDrawable().apply {
            setColor(ColorUtils.setAlphaComponent(color, alpha))
            setCornerRadius(cornerRadius)
        }
    return gradientDrawable
}

fun createThumbnailGlideOptions(
    radius: Float,
    isEncrypt: Boolean = false,
): RequestOptions = createThumbnailGlideOptions(radius.toInt(), isEncrypt)

fun createThumbnailGlideOptions(
    radius: Int,
    isEncrypt: Boolean = false,
): RequestOptions =
    RequestOptions()
        .placeholder(if (isEncrypt) R.drawable.ic_padlock else R.drawable.ic_error_7)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .priority(Priority.HIGH)
        .transform(MultiTransformation(CenterCrop(), RoundedCorners(radius)))

fun downSamplingImage(
    context: Context,
    uri: Uri,
    destFile: File,
): String {
    val mimeType = context.contentResolver.getType(uri) ?: MIME_TYPE_JPEG
    val uriStream = context.contentResolver.openInputStream(uri)
    when (mimeType) {
        "image/gif" -> {
            val fos = FileOutputStream(destFile)
            IOUtils.copy(uriStream, fos)
            uriStream?.close()
            fos.close()
        }

        else -> {
            val tempFile = File.createTempFile(UUID.randomUUID().toString(), "tmp")
            val fos = FileOutputStream(tempFile)
            IOUtils.copy(uriStream, fos)
            val compressedFile = Compressor(context).setQuality(70).compressToFile(tempFile)
            compressedFile.copyTo(destFile, true)
            uriStream?.close()
            fos.close()
            tempFile.delete()
        }
    }
    return mimeType
}

fun downSamplingImage(
    context: Context,
    srcFile: File,
    destFile: File,
) {
    val compressedFile = Compressor(context).setQuality(70).compressToFile(srcFile)
    compressedFile.copyTo(destFile, true)
}

fun photoUriToDownSamplingBitmap(
    context: Context,
    photoUri: PhotoUri,
    requiredSize: Int = 50,
    fixedWidth: Int = 45,
    fixedHeight: Int = 45,
): Bitmap =
    try {
        when (photoUri.isContentUri()) {
            true -> {
                BitmapUtils.decodeFile(
                    context,
                    Uri.parse(photoUri.photoUri),
                    context.dpToPixel(fixedWidth.toFloat(), Calculation.FLOOR),
                    context.dpToPixel(fixedHeight.toFloat(), Calculation.FLOOR),
                )
            }

            false -> {
                when (fixedWidth == fixedHeight) {
                    true -> {
                        BitmapUtils.decodeFileCropCenter(
                            getApplicationDataDirectory(context) + photoUri.getFilePath(),
                            context.dpToPixel(fixedWidth.toFloat(), Calculation.FLOOR),
                        )
                    }

                    false -> {
                        BitmapUtils.decodeFile(
                            getApplicationDataDirectory(context) + photoUri.getFilePath(),
                            context.dpToPixel(fixedWidth.toFloat(), Calculation.FLOOR),
                            context.dpToPixel(fixedHeight.toFloat(), Calculation.FLOOR),
                        )
                    }
                }
            }
        }
    } catch (fe: FileNotFoundException) {
        fe.printStackTrace()
        BitmapFactory.decodeResource(context.resources, R.drawable.ic_error_7)
    } catch (se: SecurityException) {
        se.printStackTrace()
        BitmapFactory.decodeResource(context.resources, R.drawable.ic_error_7)
    } catch (e: Exception) {
        e.printStackTrace()
        BitmapFactory.decodeResource(context.resources, R.drawable.ic_error_7)
    }

fun photoUriToBitmap(
    context: Context,
    photoUri: PhotoUri,
): Bitmap? {
    val bitmap: Bitmap? =
        try {
            when (photoUri.isContentUri()) {
                true -> {
                    BitmapFactory.decodeStream(
                        context.contentResolver.openInputStream(
                            Uri.parse(
                                photoUri.photoUri,
                            ),
                        ),
                    )
                }

                false -> {
                    BitmapFactory.decodeFile(getApplicationDataDirectory(context) + photoUri.getFilePath())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    return bitmap
}

fun createAttachedPhotoView(
    context: Context,
    photoUri: PhotoUri,
    marginLeft: Float = 0F,
    marginTop: Float = 0F,
    marginRight: Float = 3F,
    marginBottom: Float = 0F,
): ImageView {
    val thumbnailSize = context.dpToPixel(context.config.settingThumbnailSize)
    val cornerRadius = thumbnailSize * PHOTO_CORNER_RADIUS_SCALE_FACTOR_NORMAL
    val imageView = ImageView(context)
    val layoutParams = LinearLayout.LayoutParams(thumbnailSize, thumbnailSize)
    layoutParams.setMargins(
        context.dpToPixel(marginLeft),
        context.dpToPixel(marginTop),
        context.dpToPixel(marginRight),
        context.dpToPixel(marginBottom),
    )
    imageView.layoutParams = layoutParams
    imageView.background =
        createBackgroundGradientDrawable(
            context.config.primaryColor,
            THUMBNAIL_BACKGROUND_ALPHA,
            cornerRadius,
        )
    imageView.scaleType = ImageView.ScaleType.CENTER
    val padding = (context.dpToPixel(2.5F, Calculation.FLOOR))
    imageView.setPadding(padding, padding, padding, padding)

    val mimeType = photoUri.mimeType ?: ""
    if (mimeType.startsWith("audio")) {
        imageView.background =
            createBackgroundGradientDrawable(
                context.config.primaryColor,
                220,
                cornerRadius,
            )
        imageView.setImageResource(R.drawable.ic_mic)
        imageView.setColorFilter(Color.WHITE)
    } else {
        Glide
            .with(context)
            .load(getApplicationDataDirectory(context) + photoUri.getFilePath())
            .apply(createThumbnailGlideOptions(cornerRadius, photoUri.isEncrypt()))
            .into(imageView)

        if (mimeType.startsWith("video") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val icon = androidx.core.content.ContextCompat.getDrawable(context, R.drawable.ic_videocam)?.mutate()
            icon?.setTint(Color.WHITE)
            val iconPadding = (thumbnailSize * 0.25f).toInt()
            imageView.foreground = InsetDrawable(icon, iconPadding)
        }
    }
    return imageView
}

fun createAttachedPhotoViewForFlexBox(
    activity: android.app.Activity,
    photoUri: PhotoUri,
    attachedCount: Int,
): CardView {
    val spanCount =
        when {
            !activity.isLandScape() && attachedCount == 1 -> 1
            !activity.isLandScape() && attachedCount == 2 -> 2
            !activity.isLandScape() && attachedCount > 2 -> 3
            activity.isLandScape() -> 5
            else -> 1
        }
    val attachCardContentPadding = ATTACH_PHOTO_CARD_CONTENT_PADDING_DP.div(spanCount)
    val thumbnailSize =
        (
            activity.getDefaultDisplay().x -
                activity.dpToPixel(ATTACH_PHOTO_CONTAINER_CARD_PADDING_DP) -
                (activity.dpToPixel(ATTACH_PHOTO_MARGIN_DP)).times(spanCount).times(2) -
                (activity.dpToPixel(attachCardContentPadding)).times(spanCount).times(2)
        ).div(spanCount)
    val cornerRadius = thumbnailSize.times(PHOTO_CORNER_RADIUS_SCALE_FACTOR_SMALL)
    val imageView = ImageView(activity)
    val layoutParams = LinearLayout.LayoutParams(thumbnailSize, thumbnailSize)
    imageView.layoutParams = layoutParams
    imageView.scaleType = ImageView.ScaleType.CENTER

    val mimeType = photoUri.mimeType ?: ""
    if (mimeType.startsWith("audio")) {
        imageView.background =
            createBackgroundGradientDrawable(
                activity.config.primaryColor,
                220,
                cornerRadius,
            )
        imageView.setImageResource(R.drawable.ic_mic)
        imageView.setColorFilter(Color.WHITE)
    } else {
        Glide
            .with(activity)
            .load(getApplicationDataDirectory(activity) + photoUri.getFilePath())
            .apply(createThumbnailGlideOptions(cornerRadius, photoUri.isEncrypt()))
            .into(imageView)

        if (mimeType.startsWith("video") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val icon = androidx.core.content.ContextCompat.getDrawable(activity, R.drawable.ic_videocam)?.mutate()
            icon?.setTint(Color.WHITE)
            val iconPadding = (thumbnailSize * 0.25f).toInt()
            imageView.foreground = InsetDrawable(icon, iconPadding)
        }
    }

    val margin = activity.dpToPixel(ATTACH_PHOTO_MARGIN_DP, Calculation.FLOOR)
    val contentPadding = activity.dpToPixel(attachCardContentPadding, Calculation.FLOOR)
    return com.quangthe.nhatky.views.FixedCardView(activity).apply {
        activity.updateDashboardInnerCard(this)
        setLayoutParams(
            ViewGroup
                .MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                ).apply {
                    setMargins(margin, margin, margin, margin)
                },
        )

        radius = cornerRadius
        fixedAppcompatPadding = false
        setContentPadding(contentPadding, contentPadding, contentPadding, contentPadding)
        addView(imageView)
    }
}
