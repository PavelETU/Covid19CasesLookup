package com.wordpress.covid19caseslookup

interface LookupRepo {
    suspend fun getCountries(): List<Country>

    suspend fun getCountrySummary(countrySlug: String): List<CountryStats>
}