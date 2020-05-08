package com.wordpress.covid19caseslookup

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText

fun bddTestCase(func: BDDTestCase.() -> Unit): BDDTestCase {
    val bddTestCase = BDDTestCase()
    bddTestCase.func()
    return bddTestCase
}

class BDDTestCase {
    fun givenIOpenTheApp() {
        // Already handled by CasesLookupRules
    }

    fun iSeeListOfAvailableCountries() {
        onView(withText("Croatia")).check(matches(isCompletelyDisplayed()))
        onView(withText("Kiribati")).check(matches(isCompletelyDisplayed()))
        onView(withText("Ireland")).check(matches(isCompletelyDisplayed()))
        onView(withText("Russian Federation")).check(matches(isCompletelyDisplayed()))
    }
}