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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.wordpress.covid19caseslookup.R
import com.wordpress.covid19caseslookup.androidframework.visible
import com.wordpress.covid19caseslookup.databinding.FragmentListOfCountriesBinding
import dagger.hilt.android.AndroidEntryPoint

private const val LOCATION_PERMISSION_REQUEST_CODE = 1

@AndroidEntryPoint
class ListOfCountriesFragment : Fragment() {

    private var _binding: FragmentListOfCountriesBinding? = null
    // Non nullable variable to be accessed during view active lifecycle
    private val binding get() = _binding!!
    private val viewModel: ListOfCountriesViewModel by viewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var listener: OnCountryChosenListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnCountryChosenListener ?: throw ClassCastException("Activity should implement OnCountryChosenListener")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adjustToolbar()
        viewModel.openStatsEventWithSlug.observe(viewLifecycleOwner, { listener.onCountryChosen(it) })
        viewModel.listToDisplay.observe(viewLifecycleOwner, { displayCountries(it) })
        viewModel.showError.observe(viewLifecycleOwner, { showError ->
            binding.errorView.visible(showError)
        })
        viewModel.loading.observe(viewLifecycleOwner, { loading ->
            binding.loadingIndicator.visible(loading)
        })
        binding.errorView.setOnClickListener { viewModel.retry() }
        checkPermissionAndTryToGetLocation()
        viewModel.displayedPositionInList.observe(viewLifecycleOwner, { highlightPosition(it) })
        viewModel.snackBarEvent.observe(viewLifecycleOwner, { Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show() })
    }

    private fun adjustToolbar() {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.choose_country)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListOfCountriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkPermissionAndTryToGetLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        else tryToGetLocation()
    }

    private fun displayCountries(countries: List<String>) {
        binding.listOfCountries.visibility = View.VISIBLE
        binding.listOfCountries.adapter = CountriesAdapter(countries, object: CountriesAdapter.ClickListener {
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
        (binding.listOfCountries.layoutManager as? LinearLayoutManager?)?.scrollToPosition(position)
        (binding.listOfCountries.adapter as CountriesAdapter).animateItem(position)
    }

    interface OnCountryChosenListener {
        fun onCountryChosen(slug: String)
    }
}