package com.wordpress.covid19caseslookup

import com.wordpress.covid19caseslookup.data.LookupRepo
import com.wordpress.covid19caseslookup.data.entities.Country
import com.wordpress.covid19caseslookup.data.entities.CountryStats

class FakeRepo(private var countries: List<Country>, private val countryStats: List<CountryStats>) :
    LookupRepo {
    var lastCountrySlugUsed = ""

    fun setCountries(countries: List<Country>) {
        this.countries = countries
    }

    override suspend fun getCountries(): List<Country> {
        return countries
    }

    override suspend fun getCountrySummary(countrySlug: String): List<CountryStats> {
        lastCountrySlugUsed = countrySlug
        return countryStats
    }
}
