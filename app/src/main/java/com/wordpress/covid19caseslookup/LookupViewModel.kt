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
}