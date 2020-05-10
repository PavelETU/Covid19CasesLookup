package com.wordpress.covid19caseslookup

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CasesLookupUITest: CasesLookupRules() {
    @Test
    fun listOfAvailableCountriesIsPresentedWhenAppIsOpened() {
        bddTestCase {
            givenIOpenTheApp()
            iSeeATitle("Choose a country")
            iSeeListOfAvailableCountries()
        }
    }
}