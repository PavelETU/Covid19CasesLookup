package com.wordpress.covid19caseslookup.presentation

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordpress.covid19caseslookup.data.LookupRepo
import com.wordpress.covid19caseslookup.data.entities.CountryStats
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch

class CountryStatsViewModel @ViewModelInject constructor(var lookupRepo: LookupRepo,
                                                         @ApplicationContext private val context: Context) : ViewModel() {
    private val _countryStats = MutableLiveData<List<CountryStats>>()
    val countryStats: LiveData<List<CountryStats>> = _countryStats
    private val _showError = MutableLiveData<Boolean>()
    val showError: LiveData<Boolean> = _showError
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    private var slug: String? = null
    fun onSlugObtained(slug: String) {
        if (this.slug != slug) {
            this.slug = slug
            loadStats()
        }
    }

    fun retry() {
        loadStats()
    }

    private fun loadStats() {
        _showError.value = false
        _loading.value = true
        viewModelScope.launch {
            val stats = lookupRepo.getCountrySummary(slug!!)
            _loading.value = false
            if (stats.isEmpty()) _showError.value = true
            else this@CountryStatsViewModel._countryStats.value = stats
        }
    }
}