package com.wordpress.covid19caseslookup

import com.wordpress.covid19caseslookup.data.LookupRepo
import com.wordpress.covid19caseslookup.data.entities.Country
import com.wordpress.covid19caseslookup.data.entities.CountryStats

class RepositoryImplForUITest: LookupRepo {
    override suspend fun getCountries(): List<Country> = listOf(Country(
        "Croatia",
        "croatia",
        "HR"
    ),
        Country(
            "Kiribati",
            "kiribati",
            "KI"
        ),
        Country(
            "Ireland",
            "ireland",
            "IE"
        ),
        Country(
            "Russian Federation",
            "russia",
            "RU"
        ))
    override suspend fun getCountrySummary(countrySlug: String): List<CountryStats> = emptyList()
}