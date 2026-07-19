package com.quangthe.nhatky.views

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.lifecycleScope
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import com.quangthe.nhatky.commons.utils.LocationUtil
import com.quangthe.nhatky.R
import com.quangthe.nhatky.extensions.config
import com.quangthe.nhatky.models.Location
import java.util.Locale

class LocationContainerView : FixedCardView {

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context, attrs)
    }

    enum class DisplayMode {
        EDITING,
        READ_ONLY,
        COMPACT,
    }

    private lateinit var contentView: View
    private lateinit var locationContent: LinearLayout
    private lateinit var locationSymbol: View
    private lateinit var locationLabel: FixedTextView
    private lateinit var latitudeLabel: FixedTextView
    private lateinit var longitudeLabel: FixedTextView
    private lateinit var searchAddress: View
    private lateinit var editLocation: View
    private lateinit var arrowIcon: View
    private lateinit var locationProgress: ContentLoadingProgressBar

    private var mLocation: Location? = null
    private var mDisplayMode: DisplayMode = DisplayMode.COMPACT
    private var mOnLocationChangeListener: ((Location) -> Unit)? = null
    var onRequestLocationListener: (() -> Unit)? = null

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private fun initView(context: Context, attrs: AttributeSet?) {
        fixedAppcompatPadding = true
        contentView = LayoutInflater.from(context).inflate(R.layout.view_location_container, this, true)

        locationContent = contentView.findViewById(R.id.locationContent)
        locationSymbol = contentView.findViewById(R.id.locationSymbol)
        locationLabel = contentView.findViewById(R.id.locationLabel)
        latitudeLabel = contentView.findViewById(R.id.latitudeLabel)
        longitudeLabel = contentView.findViewById(R.id.longitudeLabel)
        searchAddress = contentView.findViewById(R.id.searchAddress)
        editLocation = contentView.findViewById(R.id.editLocation)
        arrowIcon = contentView.findViewById(R.id.arrowIcon)
        locationProgress = contentView.findViewById(R.id.locationProgress)

        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LocationContainerView)
            val modeOrdinal = typedArray.getInt(R.styleable.LocationContainerView_displayMode, DisplayMode.EDITING.ordinal)
            mDisplayMode = DisplayMode.entries[modeOrdinal]
            typedArray.recycle()
        }

        applyMode()
        setupClickListeners()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        scope.cancel()
    }

    fun getLocation(): Location? = mLocation

    fun setLocation(location: Location?) {
        mLocation = location
        if (location != null) {
            visibility = View.VISIBLE
            locationLabel.text = location.address
            latitudeLabel.text = String.format(Locale.US, "Lat: %.4f", location.latitude)
            longitudeLabel.text = String.format(Locale.US, "Lng: %.4f", location.longitude)
        } else {
            visibility = View.GONE
        }
    }

    fun setProgressVisible(visible: Boolean) {
        locationProgress.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setOnLocationChangeListener(listener: (Location) -> Unit) {
        mOnLocationChangeListener = listener
    }

    private fun applyMode() {
        when (mDisplayMode) {
            DisplayMode.EDITING -> {
                locationContent.background = context.getDrawable(R.drawable.bg_card_thumbnail)
                locationContent.setPadding(
                    dp(8), dp(8), dp(8), dp(8)
                )
                locationLabel.setTextColor(Color.WHITE)
                locationLabel.textSize = 12f
                latitudeLabel.setTextColor(Color.WHITE)
                latitudeLabel.textSize = 10f
                longitudeLabel.setTextColor(Color.WHITE)
                longitudeLabel.textSize = 10f
                searchAddress.visibility = View.VISIBLE
                editLocation.visibility = View.VISIBLE
                arrowIcon.visibility = View.VISIBLE
                tintIcon(locationSymbol, Color.WHITE)
                tintIcon(searchAddress, Color.WHITE)
                tintIcon(editLocation, Color.WHITE)
                tintIcon(arrowIcon, Color.WHITE)
            }

            DisplayMode.READ_ONLY -> {
                locationContent.background = context.getDrawable(R.drawable.bg_card_thumbnail)
                locationContent.setPadding(
                    dp(8), dp(8), dp(8), dp(8)
                )
                locationLabel.setTextColor(Color.WHITE)
                locationLabel.textSize = 12f
                latitudeLabel.setTextColor(Color.WHITE)
                latitudeLabel.textSize = 10f
                longitudeLabel.setTextColor(Color.WHITE)
                longitudeLabel.textSize = 10f
                searchAddress.visibility = View.GONE
                editLocation.visibility = View.GONE
                arrowIcon.visibility = View.VISIBLE
                tintIcon(locationSymbol, Color.WHITE)
                tintIcon(arrowIcon, Color.WHITE)
            }

            DisplayMode.COMPACT -> {
                locationContent.background = null
                locationContent.setPadding(
                    dp(5), dp(5), dp(5), dp(5)
                )
                val primaryColor = context.config.primaryColor
                locationLabel.setTextColor(primaryColor)
                locationLabel.textSize = 10f
                latitudeLabel.setTextColor(primaryColor)
                latitudeLabel.textSize = 9f
                longitudeLabel.setTextColor(primaryColor)
                longitudeLabel.textSize = 9f
                searchAddress.visibility = View.GONE
                editLocation.visibility = View.GONE
                arrowIcon.visibility = View.GONE
                tintIcon(locationSymbol, primaryColor)
            }
        }
    }

    private fun setupClickListeners() {
        setOnClickListener {
            when {
                mLocation != null -> openGoogleMap(mLocation!!)
                onRequestLocationListener != null -> onRequestLocationListener!!.invoke()
            }
        }

        if (mDisplayMode == DisplayMode.EDITING) {
            searchAddress.setOnClickListener { showSearchAddressDialog() }
            editLocation.setOnClickListener { showEditLocationDialog() }
        }
    }

    private fun showSearchAddressDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Search Address")
        val input = android.widget.EditText(context)
        input.hint = "Enter address or place name"
        builder.setView(input)
        builder.setPositiveButton("Search") { _, _ ->
            val addressString = input.text.toString()
            if (addressString.isNotEmpty()) {
                scope.launch {
                    locationProgress.visibility = View.VISIBLE
                    val address = LocationUtil.getLatLngFromAddress(context, addressString)
                    if (address != null) {
                        val lat = address.latitude
                        val lng = address.longitude
                        var locationInfo: String? = null
                        LocationUtil.getAddressFromLatLng(context, lat, lng)?.let {
                            locationInfo = it
                        }
                        val location = Location(locationInfo ?: addressString, lat, lng)
                        setLocation(location)
                        mOnLocationChangeListener?.invoke(location)
                    } else {
                        android.widget.Toast.makeText(context, "Could not find location for: $addressString", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    locationProgress.visibility = View.GONE
                }
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun showEditLocationDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Edit Coordinates")
        val container = LinearLayout(context)
        container.orientation = LinearLayout.VERTICAL
        container.setPadding(40, 20, 40, 20)

        val latInput = android.widget.EditText(context)
        latInput.hint = "Latitude (e.g. 10.7626)"
        latInput.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
        mLocation?.let { latInput.setText(it.latitude.toString()) }
        container.addView(latInput)

        val lngInput = android.widget.EditText(context)
        lngInput.hint = "Longitude (e.g. 106.6601)"
        lngInput.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
        mLocation?.let { lngInput.setText(it.longitude.toString()) }
        container.addView(lngInput)

        builder.setView(container)
        builder.setPositiveButton("Update") { _, _ ->
            val lat = latInput.text.toString().toDoubleOrNull()
            val lng = lngInput.text.toString().toDoubleOrNull()
            if (lat != null && lng != null) {
                scope.launch {
                    locationProgress.visibility = View.VISIBLE
                    val address = LocationUtil.getAddressFromLatLng(context, lat, lng)
                    val location = Location(address ?: "Manual Input", lat, lng)
                    setLocation(location)
                    mOnLocationChangeListener?.invoke(location)
                    locationProgress.visibility = View.GONE
                }
            } else {
                android.widget.Toast.makeText(context, "Invalid coordinates", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun openGoogleMap(location: Location) {
        val lat = location.latitude
        val lng = location.longitude
        val label = location.address ?: "Vị trí của tôi"
        val uri = Uri.parse("geo:0,0?q=$lat,$lng(${Uri.encode(label)})")
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        try {
            context.startActivity(mapIntent)
        } catch (e: ActivityNotFoundException) {
            val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng")
            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
        }
    }

    private fun tintIcon(view: View, color: Int) {
        (view as? android.widget.ImageView)?.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN)
    }

    private fun dp(value: Int): Int {
        val density = context.resources.displayMetrics.density
        return (value * density).toInt()
    }
}
