package com.wordpress.covid19caseslookup.data

import com.wordpress.covid19caseslookup.data.entities.Country
import com.wordpress.covid19caseslookup.data.entities.CountryStats

interface LookupRepo {
    suspend fun getCountries(): List<Country>

    suspend fun getCountrySummary(countrySlug: String): List<CountryStats>
}