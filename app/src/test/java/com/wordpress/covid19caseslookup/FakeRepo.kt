package com.wordpress.covid19caseslookup

class FakeRepo(private var countries: List<Country>) : LookupRepo {
    override suspend fun getCountries(): List<Country> {
        return countries
    }

    fun setCountries(countries: List<Country>) {
        this.countries = countries
    }
}
