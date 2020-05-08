package com.wordpress.covid19caseslookup

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class LookupViewModel(private val lookupRepo: LookupRepo, private val appForContext: Application) : AndroidViewModel(appForContext) {
    val countries = MutableLiveData<List<Country>>()
    val listToDisplay: LiveData<List<String>> = countries.map {
        val listTitle = appForContext.getString(R.string.choose_country)
        listOf(listTitle).plus(it.map { country -> country.country } )
    }
    val statToDisplay = MutableLiveData<String>()
    val showError = MutableLiveData<Boolean>()
    val displayedPositionInList = MediatorLiveData<Int>()
    private var country = MutableLiveData<String>()

    init{
        displayedPositionInList.addSource(countries) {
            if (it.isNotEmpty()) displayedPositionInList.removeSource(countries)
            if (country.value != null) {
                if (!it.isNullOrEmpty()) {
                    val index = it.indexOfFirst { elementToCheck -> elementToCheck.country.contentEquals(country.value!!) }
                    if (index != -1) displayedPositionInList.value = index + 1
                }
            }
        }
        displayedPositionInList.addSource(country) {
            displayedPositionInList.removeSource(country)
            if (!countries.value.isNullOrEmpty()) {
                val index = countries.value!!.indexOfFirst { elementToCheck -> elementToCheck.country.contentEquals(country.value!!) }
                if (index != -1) displayedPositionInList.value = index + 1
            }
        }
    }

    fun start() {
        viewModelScope.launch {
            val countries = lookupRepo.getCountries()
            this@LookupViewModel.countries.value = countries
            showError.value = countries.isEmpty()
        }
    }

    fun onItemSelected(position: Int) {
        if (position == 0) {
            statToDisplay.value = ""
            return
        }
        viewModelScope.launch {
            val stats = lookupRepo.getCountrySummary(countries.value!![position - 1].slug).lastOrNull() ?: return@launch
            statToDisplay.value = appForContext.getString(R.string.stats_to_display, stats.confirmed, stats.deaths, stats.recovered)
        }
    }

    fun onLocationObtained(countryName: String?) {
        countryName ?: return
        country.value = countryName
    }


}