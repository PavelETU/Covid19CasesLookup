package com.wordpress.covid19caseslookup

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class LookupViewModel(private val lookupRepo: LookupRepo, private val appForContext: Application) : AndroidViewModel(appForContext) {
    val countries = MutableLiveData<List<Country>>()
    val listToDisplay: LiveData<List<String>> = countries.map {
        val listTitle = appForContext.getString(R.string.choose_country)
        listOf(listTitle).plus(it.map { country -> country.Country } )
    }
    val showError = MutableLiveData<Boolean>()

    fun start() {
        viewModelScope.launch {
            val countries = lookupRepo.getCountries()
            this@LookupViewModel.countries.value = countries
            showError.value = countries.isEmpty()
        }
    }
}