package com.wordpress.covid19caseslookup

class FakeRepo(private val countries: List<Country>) : LookupRepo {
    override suspend fun getCountries(): List<Country> {
        return countries
    }
}
