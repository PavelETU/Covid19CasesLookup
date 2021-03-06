package com.wordpress.covid19caseslookup.data

import com.wordpress.covid19caseslookup.data.entities.Country
import com.wordpress.covid19caseslookup.data.entities.CountryStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LookUpRepoImpl @Inject constructor(private val lookupService: LookupService) : LookupRepo {
    override suspend fun getCountries(): List<Country> = withContext(Dispatchers.IO) {
        return@withContext try {
            lookupService.getCountries()
        } catch (e: Exception) {
            emptyList<Country>()
        }
    }

    override suspend fun getCountrySummary(countrySlug: String): List<CountryStats> =
        withContext(Dispatchers.IO) {
            return@withContext lookupService.getStatForCountry(countrySlug)
        }
}