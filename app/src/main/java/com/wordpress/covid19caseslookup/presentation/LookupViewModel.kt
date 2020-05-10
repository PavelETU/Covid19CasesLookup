package com.wordpress.covid19caseslookup.presentation

import android.app.Application
import androidx.lifecycle.*
import com.wordpress.covid19caseslookup.R
import com.wordpress.covid19caseslookup.data.LookupRepo
import com.wordpress.covid19caseslookup.data.entities.Country
import kotlinx.coroutines.launch

class LookupViewModel(private val lookupRepo: LookupRepo, private val appForContext: Application) : AndroidViewModel(appForContext) {
    val countries = MutableLiveData<List<Country>>()
    val listToDisplay: LiveData<List<String>> = countries.map {
        it.map { country -> country.country }
    }
    val statToDisplay = MutableLiveData<String>()
    val showError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()
    val displayedPositionInList = MediatorLiveData<Int>()
    private var country = MutableLiveData<String>()

    init{
        displayedPositionInList.addSource(countries) {
            if (it.isNotEmpty()) displayedPositionInList.removeSource(countries)
            if (country.value != null) {
                if (!it.isNullOrEmpty()) {
                    val index = it.indexOfFirst { elementToCheck -> elementToCheck.country.contentEquals(country.value!!) }
                    if (index != -1) displayedPositionInList.value = index
                }
            }
        }
        displayedPositionInList.addSource(country) {
            displayedPositionInList.removeSource(country)
            if (!countries.value.isNullOrEmpty()) {
                val index = countries.value!!.indexOfFirst { elementToCheck -> elementToCheck.country.contentEquals(country.value!!) }
                if (index != -1) displayedPositionInList.value = index
            }
        }
    }

    fun start() {
        showError.value = false
        loading.value = true
        viewModelScope.launch {
            val countries = lookupRepo.getCountries()
            loading.value = false
            if (countries.isEmpty()) showError.value = countries.isEmpty()
            else this@LookupViewModel.countries.value = countries
        }
    }

    fun onItemSelected(position: Int) {
        viewModelScope.launch {
            val stats = lookupRepo.getCountrySummary(countries.value!![position].slug).lastOrNull() ?: return@launch
            statToDisplay.value = appForContext.getString(R.string.stats_to_display, stats.confirmed, stats.deaths, stats.recovered)
        }
    }

    fun onLocationObtained(countryName: String?) {
        countryName ?: return
        country.value = countryName
    }


}