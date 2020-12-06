package com.wordpress.covid19caseslookup.presentation

sealed class StateOfStatsScreen
object Loading: StateOfStatsScreen()
data class Error(val message: String): StateOfStatsScreen()
object Success: StateOfStatsScreen()