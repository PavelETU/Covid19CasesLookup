package com.wordpress.covid19caseslookup

import com.wordpress.covid19caseslookup.data.LookupRepo
import com.wordpress.covid19caseslookup.data.entities.Country
import com.wordpress.covid19caseslookup.data.entities.CountryStats
import javax.inject.Inject

class RepositoryImplForUITest @Inject constructor(): LookupRepo {
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
            "United Kingdom",
            "uk",
            "UK"
        ),
        Country(
            "Russian Federation",
            "russia",
            "RU"
        ))
    override suspend fun getCountrySummary(countrySlug: String): List<CountryStats> = listOf(
        CountryStats(1000, 5, 900, "2020-01-22T00:00:00Z"),
        CountryStats(1100, 9, 950, "2020-01-25T00:00:00Z"),
        CountryStats(1000, 12, 956, "2020-02-25T00:00:00Z"),
        CountryStats(1000, 12, 956, "2020-02-25T00:00:00Z"),
        CountryStats(1000, 12, 956, "2020-02-25T00:00:00Z"),
        CountryStats(1000, 12, 956, "2020-03-25T00:00:00Z"),
        CountryStats(1000, 12, 956, "2020-04-25T00:00:00Z"),
        CountryStats(1000, 12, 956, "2020-05-25T00:00:00Z"),
        CountryStats(1000, 12, 956, "2020-06-25T00:00:00Z"),
        CountryStats(1000, 12, 956, "2020-07-25T00:00:00Z"),
        CountryStats(1000, 12, 956, "2020-08-25T00:00:00Z"),
        CountryStats(1000, 12, 956, "2020-09-25T00:00:00Z"),
        CountryStats(1000, 12, 956, "2020-10-25T00:00:00Z"),
        CountryStats(1000, 12, 956, "2020-10-25T00:00:00Z"),
        CountryStats(1000, 12, 956, "2020-10-25T00:00:00Z"),
        CountryStats(1000, 12, 956, "2020-10-25T00:00:00Z"),
        CountryStats(1000, 12, 956, "2020-11-25T00:00:00Z"),
        CountryStats(1000, 12, 956, "2020-12-25T00:00:00Z"),
        CountryStats(1000, 12, 956, "2021-01-25T00:00:00Z"),
        CountryStats(1000, 12, 956, "2021-02-25T00:00:00Z"),
        CountryStats(1000, 12, 956, "2021-04-25T00:00:00Z"),
        CountryStats(1500, 20, 1300, "2021-10-26T00:00:00Z")
    )
}