package com.wordpress.covid19caseslookup.presentation

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.wordpress.covid19caseslookup.R
import com.wordpress.covid19caseslookup.data.LookupRepo
import com.wordpress.covid19caseslookup.data.entities.Country
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

const val INVALID_COUNTRY_POSITION = -1
class ListOfCountriesViewModel @ViewModelInject constructor(var lookupRepo: LookupRepo, @ApplicationContext private val context: Context) : ViewModel() {
    private val _stateOfCountriesList: MutableStateFlow<StateOfListOfCountriesScreen> = MutableStateFlow(CountriesLoading)
    val stateOfCountriesList: StateFlow<StateOfListOfCountriesScreen> = _stateOfCountriesList
    private var countriesList: List<Country>? = null
    private var countriesListPresentation: List<String>? = null
    private var countriesListToDisplay: MutableStateFlow<List<String>>? = null
    var displayedPositionInList: Flow<Int>
    private var usersCountry = MutableStateFlow("")
    private val _snackBarEvent = MutableSharedFlow<String>()
    val snackBarEvent: Flow<String> = _snackBarEvent
    private val _openStatsEventWithSlug = MutableSharedFlow<Pair<String, String>>(0)
    val openStatsEventWithSlugForCountry: Flow<Pair<String, String>> = _openStatsEventWithSlug

    init {
        displayedPositionInList = usersCountry.combine(stateOfCountriesList) { country, state ->
            if (country.isNotEmpty() && (state is CountriesLoaded)) {
                state.countries.value.indexOf(country)
            } else {
                INVALID_COUNTRY_POSITION
            }
        }
        loadCountries()
    }

    fun retry() {
        loadCountries()
    }

    private fun loadCountries() {
        _stateOfCountriesList.value = CountriesLoading
        viewModelScope.launch {
            countriesList = lookupRepo.getCountries()
            _stateOfCountriesList.value =
                if (countriesList.isNullOrEmpty()) {
                    CountriesFailedToLoad
                } else {
                    countriesListPresentation = countriesList!!.map { it.country }
                    countriesListToDisplay = MutableStateFlow(countriesListPresentation!!)
                    CountriesLoaded(countriesListToDisplay!!)
                }
        }
    }

    fun onItemSelected(position: Int) {
        val country = countriesList!!.find { it.country == countriesListToDisplay!!.value[position] }
        val countrySlug: String = country!!.slug.takeUnless { it.isEmpty() } ?: run {
            viewModelScope.launch {
                _snackBarEvent.emit(context.getString(R.string.no_stats))
            }
            return@onItemSelected
        }
        viewModelScope.launch {
            _openStatsEventWithSlug.emit(Pair(countrySlug, country.country))
        }
    }

    fun onLocationObtained(countryName: String?) {
        countryName ?: return
        usersCountry.value = countryName
    }

    fun onQueryChanged(searchTerm: String?) {
        countriesListToDisplay ?: return
        if (searchTerm.isNullOrBlank()) {
            countriesListToDisplay?.value = countriesListPresentation!!
            return
        }
        countriesListToDisplay!!.value = countriesListPresentation!!.filter { it.contains(searchTerm, true) }
    }
}