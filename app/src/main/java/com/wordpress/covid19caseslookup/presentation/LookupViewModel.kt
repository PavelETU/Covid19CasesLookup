package com.wordpress.covid19caseslookup.presentation

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.wordpress.covid19caseslookup.R
import com.wordpress.covid19caseslookup.androidframework.SingleLiveEvent
import com.wordpress.covid19caseslookup.data.LookupRepo
import com.wordpress.covid19caseslookup.data.entities.Country
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch

class LookupViewModel @ViewModelInject constructor(var lookupRepo: LookupRepo, @ApplicationContext private val context: Context) : ViewModel() {
    val countries = MutableLiveData<List<Country>>()
    val listToDisplay: LiveData<List<String>> = countries.map {
        it.map { country -> country.country }
    }
    val showError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()
    val displayedPositionInList = MediatorLiveData<Int>()
    private var country = MutableLiveData<String>()
    val snackBarEvent = SingleLiveEvent<String>()
    val openStatsEventWithSlug = SingleLiveEvent<String>()

    init {
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
        loadCountries()
    }

    fun retry() {
        loadCountries()
    }

    private fun loadCountries() {
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
        val countrySlug: String = countries.value!![position].slug.takeUnless { it.isEmpty() } ?: run {
            snackBarEvent.setValue(context.getString(R.string.no_stats))
            return@onItemSelected
        }
        openStatsEventWithSlug.setValue(countrySlug)
    }

    fun onLocationObtained(countryName: String?) {
        countryName ?: return
        country.value = countryName
    }


}