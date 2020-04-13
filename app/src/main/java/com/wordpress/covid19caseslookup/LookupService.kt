package com.wordpress.covid19caseslookup

import retrofit2.http.GET

interface LookupService {
    @GET("/countries")
    suspend fun getCountries(): List<Country>
}