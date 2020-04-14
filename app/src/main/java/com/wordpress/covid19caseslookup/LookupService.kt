package com.wordpress.covid19caseslookup

import retrofit2.http.GET
import retrofit2.http.Path

interface LookupService {
    @GET("/countries")
    suspend fun getCountries(): List<Country>

    @GET("/total/country/{country}")
    suspend fun getStatForCountry(@Path("country") countrySlug: String): List<CountryStats>
}