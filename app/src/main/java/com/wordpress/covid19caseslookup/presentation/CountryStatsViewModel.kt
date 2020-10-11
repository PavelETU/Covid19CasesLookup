package com.wordpress.covid19caseslookup.presentation

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordpress.covid19caseslookup.data.LookupRepo
import com.wordpress.covid19caseslookup.data.entities.CountryStats
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class CountryStatsViewModel @ViewModelInject constructor(var lookupRepo: LookupRepo,
                                                         @ApplicationContext private val context: Context) : ViewModel() {
    private val _countryStats = MutableStateFlow<List<CountryStats>>(emptyList())
    val countryStats: StateFlow<List<CountryStats>> = _countryStats
    private val _showError = MutableStateFlow(false)
    val showError: StateFlow<Boolean> = _showError
    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading
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