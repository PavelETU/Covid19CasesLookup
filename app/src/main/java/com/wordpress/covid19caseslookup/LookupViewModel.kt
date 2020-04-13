package com.wordpress.covid19caseslookup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LookupViewModel(private val lookupRepo: LookupRepo) : ViewModel() {
    val countries = MutableLiveData<List<Country>>()
    val showError = MutableLiveData<Boolean>().apply { value = false }
    fun start() {
        viewModelScope.launch {
            val countries = lookupRepo.getCountries()
            this@LookupViewModel.countries.value = countries
            showError.value = countries.isEmpty()
        }
    }
}