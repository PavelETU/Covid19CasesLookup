package com.wordpress.covid19caseslookup.presentation

import android.content.Context
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
    val statsToDisplay: StateFlow<List<RecordWithCases>> = _statsToDisplay
    private var confirmedCasesByMonth = SparseArrayCompat<List<RecordWithCases>>()
    private var lethalCasesByMonth = SparseArrayCompat<List<RecordWithCases>>()
    private var recoveredCasesByMonth = SparseArrayCompat<List<RecordWithCases>>()
    private var currentCases = SparseArrayCompat<List<RecordWithCases>>()
    private val _displayedMonth = MutableStateFlow("")
    val displayedMonth: StateFlow<String> = _displayedMonth
    var monthsToDisplay: List<String> = emptyList()
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
        currentCases = confirmedCasesByMonth
        updateScreen()
    }

    fun lethalClick() {
        currentCases = lethalCasesByMonth
        updateScreen()
    }

    fun recoveredClick() {
        currentCases = recoveredCasesByMonth
        updateScreen()
    }

    fun monthClick(month: String) {
        _displayedMonth.value = month
        updateScreen()
    }

    private fun updateScreen() {
        _statsToDisplay.value = currentCases.get(monthsToDisplay.indexOf(displayedMonth.value))!!
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
            val lethal = ArrayList<RecordWithCases>()
            val recovered = ArrayList<RecordWithCases>()
            statsToParse.forEachIndexed { index, countryStats ->
                val monthIndex = countryStats.date.substring(5, 7).toInt()
                if (monthIndex != lastMonth) {
                    if (indexOfPopulatedMonth != 0 || lastMonth != 0) {
                        confirmedCasesByMonth.put(indexOfPopulatedMonth, confirmed.toMutableList())
                        lethalCasesByMonth.put(indexOfPopulatedMonth, lethal.toMutableList())
                        recoveredCasesByMonth.put(indexOfPopulatedMonth, recovered.toMutableList())
                        confirmed.clear()
                        lethal.clear()
                        recovered.clear()
                        indexOfPopulatedMonth++
                    }
                    lastMonth = monthIndex
                    months.add(getMonthNameByIndex(monthIndex))
                }
                val day = countryStats.date.substring(8, 10)
                if (index != 0) {
                    confirmed.add(RecordWithCases(maxOf(countryStats.confirmed - statsToParse[index - 1].confirmed, 0), day))
                    lethal.add(RecordWithCases(maxOf(countryStats.deaths - statsToParse[index - 1].deaths, 0), day))
                    recovered.add(RecordWithCases(maxOf(countryStats.recovered - statsToParse[index - 1].recovered, 0), day))
                } else {
                    confirmed.add(RecordWithCases(countryStats.confirmed, day))
                    lethal.add(RecordWithCases(countryStats.deaths, day))
                    recovered.add(RecordWithCases(countryStats.recovered, day))
                }
            }
            if (confirmed.isNotEmpty()) {
                confirmedCasesByMonth.put(indexOfPopulatedMonth, confirmed.toMutableList())
                lethalCasesByMonth.put(indexOfPopulatedMonth, lethal.toMutableList())
                recoveredCasesByMonth.put(indexOfPopulatedMonth, recovered.toMutableList())
                indexOfPopulatedMonth++
            }
            currentCases = confirmedCasesByMonth
            _displayedMonth.value = months.last()
            monthsToDisplay = months
            updateScreen()
            Success
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