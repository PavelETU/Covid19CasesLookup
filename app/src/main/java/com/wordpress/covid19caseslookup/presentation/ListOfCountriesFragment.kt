package com.wordpress.covid19caseslookup.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.wordpress.covid19caseslookup.R
import com.wordpress.covid19caseslookup.androidframework.visible
import kotlinx.android.synthetic.main.fragment_list_of_countries.*
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val LOCATION_PERMISSION_REQUEST_CODE = 1

class ListOfCountriesFragment : Fragment() {

    private val viewModel: LookupViewModel by viewModel()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var listener: OnCountryChosenListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnCountryChosenListener ?: throw ClassCastException("Activity should implement OnCountryChosenListener")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.listToDisplay.observe(viewLifecycleOwner, Observer { displayCountries(it) })
        viewModel.showError.observe(viewLifecycleOwner, Observer { showError ->
            error_view.visible(showError)
        })
        viewModel.loading.observe(viewLifecycleOwner, Observer { loading ->
            loading_indicator.visible(loading)
        })
        error_view.setOnClickListener { viewModel.retry() }
        checkPermissionAndTryToGetLocation()
        viewModel.displayedPositionInList.observe(viewLifecycleOwner, Observer { highlightPosition(it) })
        viewModel.snackBarEvent.observe(viewLifecycleOwner, Observer { Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show() })
        viewModel.openStatsEventWithSlug.observe(viewLifecycleOwner, Observer { listener.onCountryChosen(it) })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_of_countries, container, false)
    }

    private fun checkPermissionAndTryToGetLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        else tryToGetLocation()
    }

    private fun displayCountries(countries: List<String>) {
        list_of_countries.visibility = View.VISIBLE
        list_of_countries.adapter = CountriesAdapter(countries, object: CountriesAdapter.ClickListener {
            override fun onItemClick(position: Int) {
                viewModel.onItemSelected(position)
            }
        })
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

    @SuppressLint("MissingPermission")
    private fun tryToGetLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation.addOnSuccessListener {
            it ?: return@addOnSuccessListener
            processLocation(it)
        }
    }

    private fun processLocation(location: Location) {
        if (Geocoder.isPresent()) {
            viewModel.onLocationObtained(
                try {
                    Geocoder(requireContext())
                        .getFromLocation(location.latitude, location.longitude, 1).getOrNull(0)
                        ?.countryName
                } catch (exception: Exception) {
                    null
                }
            )
        }
    }

    private fun highlightPosition(position: Int) {
        (list_of_countries.layoutManager as? LinearLayoutManager?)?.scrollToPosition(position)
        (list_of_countries.adapter as CountriesAdapter).animateItem(position)
    }

    interface OnCountryChosenListener {
        fun onCountryChosen(slug: String)
    }
}