package com.wordpress.covid19caseslookup

interface LookupRepo {
    suspend fun getCountries(): List<Country>
}