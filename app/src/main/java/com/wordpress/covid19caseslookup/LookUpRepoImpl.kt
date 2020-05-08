package com.wordpress.covid19caseslookup

class LookUpRepoImpl(private val lookupService: LookupService): LookupRepo {
    override suspend fun getCountries(): List<Country> {
        return try {
            lookupService.getCountries()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getCountrySummary(countrySlug: String): List<CountryStats> {
        return try {
            lookupService.getStatForCountry(countrySlug)
        } catch (e: Exception) {
            emptyList()
        }
    }
}