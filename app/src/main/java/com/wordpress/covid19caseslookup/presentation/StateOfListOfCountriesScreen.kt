package com.wordpress.covid19caseslookup.presentation

import kotlinx.coroutines.flow.StateFlow

sealed class StateOfListOfCountriesScreen
object CountriesLoading: StateOfListOfCountriesScreen()
object CountriesFailedToLoad: StateOfListOfCountriesScreen()
class CountriesLoaded(val countries: StateFlow<List<String>>): StateOfListOfCountriesScreen()
