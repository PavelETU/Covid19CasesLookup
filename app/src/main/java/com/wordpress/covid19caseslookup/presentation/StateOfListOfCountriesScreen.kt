package com.wordpress.covid19caseslookup.presentation

sealed class StateOfListOfCountriesScreen
object CountriesLoading: StateOfListOfCountriesScreen()
object CountriesFailedToLoad: StateOfListOfCountriesScreen()
class CountriesLoaded(val countries: List<String>): StateOfListOfCountriesScreen()
