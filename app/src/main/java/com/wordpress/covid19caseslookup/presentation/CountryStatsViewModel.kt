package com.wordpress.covid19caseslookup.presentation

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.collection.SparseArrayCompat
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
    private val _statsToDisplay = MutableStateFlow<List<RecordWithCases>>(emptyList())
    @VisibleForTesting
    val statsToDisplay: StateFlow<List<RecordWithCases>> = _statsToDisplay
    private var confirmedCasesByMonth = SparseArrayCompat<List<RecordWithCases>>()
    private var amountOfDeathsByMonth = SparseArrayCompat<List<RecordWithCases>>()
    private var recoveredCasesByMonth = SparseArrayCompat<List<RecordWithCases>>()
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

    fun confirmedClick() {

    }

    fun recoveredClick() {

    }

    fun lethalClick() {

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
            val months = ArrayList<String>()
            var lastMonth = 0
            var indexOfPopulatedMonth = 0
            val confirmed = ArrayList<RecordWithCases>()
            statsToParse.forEach {
                val monthIndex = it.date.substring(5, 7).toInt()
                if (monthIndex != lastMonth) {
                    if (indexOfPopulatedMonth != 0 || lastMonth != 0) {
                        confirmedCasesByMonth.put(indexOfPopulatedMonth, confirmed)
                        confirmed.clear()
                        indexOfPopulatedMonth++
                    }
                    lastMonth = monthIndex
                    months.add(getMonthNameByIndex(monthIndex))
                }
                val day = it.date.substring(8, 10)
                confirmed.add(RecordWithCases(it.confirmed, day))
            }
            if (confirmed.isNotEmpty()) {
                confirmedCasesByMonth.put(indexOfPopulatedMonth, confirmed)
                indexOfPopulatedMonth++
            }
            _statsToDisplay.value = confirmedCasesByMonth.get(--indexOfPopulatedMonth)!!
            Success(months, statsToDisplay)
        }
    }

    private fun getMonthNameByIndex(monthIndex: Int) = when(monthIndex) {
        1 -> "Jan"
        2 -> "Feb"
        3 -> "Mar"
        4 -> "Apr"
        5 -> "May"
        6 -> "Jun"
        7 -> "Jul"
        8 -> "Aug"
        9 -> "Sep"
        10 -> "Oct"
        11 -> "Nov"
        12 -> "Dec"
        else -> ""
    }
}