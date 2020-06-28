package com.wordpress.covid19caseslookup.presentation

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordpress.covid19caseslookup.data.LookupRepo
import com.wordpress.covid19caseslookup.data.entities.CountryStats
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch

class CountryStatsViewModel @ViewModelInject constructor(var lookupRepo: LookupRepo,
                                                         @ApplicationContext private val context: Context) : ViewModel() {
    val countryStats = MutableLiveData<List<CountryStats>>()
    val showError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()
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
        showError.value = false
        loading.value = true
        viewModelScope.launch {
            val stats = lookupRepo.getCountrySummary(slug!!)
            loading.value = false
            if (stats.isEmpty()) showError.value = true
            else this@CountryStatsViewModel.countryStats.value = stats
        }
    }
}