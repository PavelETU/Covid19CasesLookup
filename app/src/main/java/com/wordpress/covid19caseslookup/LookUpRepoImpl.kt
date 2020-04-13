package com.wordpress.covid19caseslookup

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LookUpRepoImpl: LookupRepo {
    private val lookupService: LookupService = Retrofit.Builder()
        .baseUrl("https://api.covid19api.com/").addConverterFactory(GsonConverterFactory.create())
        .build().create(LookupService::class.java)

    override suspend fun getCountries(): List<Country> {
        return try {
            lookupService.getCountries()
        } catch (e: Exception) {
            emptyList()
        }
    }
}