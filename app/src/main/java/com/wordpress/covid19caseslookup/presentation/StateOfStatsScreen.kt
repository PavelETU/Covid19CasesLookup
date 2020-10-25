package com.wordpress.covid19caseslookup.presentation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

sealed class StateOfStatsScreen
object Loading: StateOfStatsScreen()
data class Error(val message: String): StateOfStatsScreen()
@ExperimentalCoroutinesApi
data class Success(val monthsToDisplay: List<String>, val statsToDisplay: StateFlow<List<RecordWithCases>>): StateOfStatsScreen()