package com.wordpress.covid19caseslookup.data

import com.wordpress.covid19caseslookup.data.entities.Country
import com.wordpress.covid19caseslookup.data.entities.CountryStats
import retrofit2.http.GET
import retrofit2.http.Path

interface LookupService {
    @GET("/countries")
    suspend fun getCountries(): List<Country>

    @GET("/total/country/{country}")
    suspend fun getStatForCountry(@Path("country") countrySlug: String): List<CountryStats>
}