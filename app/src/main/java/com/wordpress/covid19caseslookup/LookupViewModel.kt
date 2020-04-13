package com.wordpress.covid19caseslookup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LookupViewModel(private val lookupRepo: LookupRepo): ViewModel() {
    val countries = MutableLiveData<List<Country>>()
    fun start() {
        viewModelScope.launch {
            countries.value = lookupRepo.getCountries()
        }
    }
}