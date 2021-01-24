package com.wordpress.covid19caseslookup

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wordpress.covid19caseslookup.androidframework.di.RepoModule
import com.wordpress.covid19caseslookup.androidframework.di.RetrofitModule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@UninstallModules(RepoModule::class, RetrofitModule::class)
@HiltAndroidTest
class CasesLookupUITest: CasesLookupRules() {
    @Test
    fun listOfAvailableCountriesIsPresentedWhenAppIsOpened() {
        bddTestCase {
            givenIOpenTheApp()
            theTitleRead("Choose a country")
            iSeeListOfAvailableCountries()
        }
    }

    @Test
    fun theTitleCorrespondsToOpenedCountry() {
        bddTestCase {
            givenIOpenTheApp()
            andIClickOnTheCountryWithTitle("Russian Federation")
            theTitleRead("Russian Federation")
        }
    }
}