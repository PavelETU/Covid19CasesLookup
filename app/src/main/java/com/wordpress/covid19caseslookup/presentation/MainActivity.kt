package com.wordpress.covid19caseslookup.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.wordpress.covid19caseslookup.R
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

const val LOCATION_PERMISSION_REQUEST_CODE = 1

class MainActivity : AppCompatActivity() {
    private val viewModel: LookupViewModel by viewModel()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.start()
        viewModel.listToDisplay.observe(this, Observer { displayCountries(it) })
        viewModel.showError.observe(this, Observer { showError ->
            error_view.visible(showError)
        })
        viewModel.loading.observe(this, Observer { loading ->
            loading_indicator.visible(loading)
        })
        error_view.setOnClickListener { viewModel.start() }
        checkPermissionAndTryToGetLocation()
        viewModel.displayedPositionInList.observe(this, Observer { highlightPosition(it) })
        supportActionBar?.title = "Choose a country"
    }

    private fun checkPermissionAndTryToGetLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        else tryToGetLocation()
    }

    private fun displayCountries(countries: List<String>) {
        list_of_countries.visibility = View.VISIBLE
        list_of_countries.adapter = CountriesAdapter(countries)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.getOrNull(0) == PackageManager.PERMISSION_GRANTED) {
            tryToGetLocation()
        }
    }

    private fun tryToGetLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            it ?: return@addOnSuccessListener
            processLocation(it)
        }
    }

    private fun processLocation(location: Location) {
        if (Geocoder.isPresent()) {
            viewModel.onLocationObtained(
                try {
                    Geocoder(this)
                        .getFromLocation(location.latitude, location.longitude, 1).getOrNull(0)
                        ?.countryName
                } catch (exception: Exception) {
                    null
                }
            )
        }
    }

    private fun highlightPosition(position: Int) {
        list_of_countries.scrollToPosition(position)
    }
}


fun View.visible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}
