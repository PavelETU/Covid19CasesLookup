package com.wordpress.covid19caseslookup.presentation

import android.content.Context
import android.util.SparseArray
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordpress.covid19caseslookup.R
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
    private val _stateOfStatsScreen = MutableStateFlow<StateOfStatsScreen>(Loading)
    val stateOfStatsScreen: StateFlow<StateOfStatsScreen> = _stateOfStatsScreen
    private val _statsToDisplay = MutableStateFlow<List<CountryStats>>(emptyList())
    private val statsToDisplay: StateFlow<List<CountryStats>> = _statsToDisplay
    private lateinit var confirmedCasesByMonth: SparseArray<List<CountryStats>>
    private lateinit var amountOfDeathsByMonth: SparseArray<List<CountryStats>>
    private lateinit var recoveredCasesByMonth: SparseArray<List<CountryStats>>
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
        _stateOfStatsScreen.value = Loading
        viewModelScope.launch {
            _stateOfStatsScreen.value = Loading
            _stateOfStatsScreen.value = runCatching {
                lookupRepo.getCountrySummary(slug!!)
            }.getOrNull()?.let {
                parseStatsIntoMonthsAndDisplayLastStats(it)
            } ?: Error(context.getString(R.string.something_went_wrong_tap_to_retry))
        }
    }

    private fun parseStatsIntoMonthsAndDisplayLastStats(statsToParse: List<CountryStats>): StateOfStatsScreen {
        return if (statsToParse.isEmpty()) {
            Error(context.getString(R.string.no_stats))
        } else {
            _statsToDisplay.value = statsToParse
            Success(listOf("Jan", "Feb"), statsToDisplay)
        }
    }
}