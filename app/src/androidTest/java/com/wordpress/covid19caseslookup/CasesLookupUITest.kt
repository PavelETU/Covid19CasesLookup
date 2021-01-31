package com.wordpress.covid19caseslookup

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wordpress.covid19caseslookup.androidframework.di.RepoModule
import com.wordpress.covid19caseslookup.androidframework.di.RetrofitModule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@UninstallModules(RepoModule::class, RetrofitModule::class)
@HiltAndroidTest
class CasesLookupUITest : CasesLookupRules() {
    @Test
    fun listOfAvailableCountriesIsPresentedWhenAppIsOpened() = bddTestCase {
        givenIOpenTheApp()
        theTitleRead("Choose a country")
        iSeeListOfAvailableCountries()
    }

    @Test
    fun theTitleCorrespondsToOpenedCountry() = bddTestCase {
        givenIOpenTheApp()
        andIClickOnTheCountryWithTitle("Russian Federation")
        theTitleRead("Russian Federation")
    }

    @Test
    fun iSeeThreeTypesOfStatsForAllMonths() = bddTestCase {
        givenIOpenTheApp()
        andIClickOnTheCountryWithTitle("Russian Federation")
        iSeeFollowingTypesOfStats(listOf("Confirmed", "Deaths", "Recovered"))
        iSeeFollowingMonths(
            listOf(
                "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
                "Sep", "Oct", "Nov", "Dec", "Jan", "Feb", "Apr", "Oct"
            )
        )
    }
}