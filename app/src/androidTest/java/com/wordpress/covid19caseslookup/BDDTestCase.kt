package com.wordpress.covid19caseslookup

import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.CoreMatchers.allOf

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

    fun theTitleRead(title: String) {
        onView(allOf(isAssignableFrom(TextView::class.java), withParent(isAssignableFrom(Toolbar::class.java))))
            .check(matches(withText(title)))
    }

    fun andIClickOnTheCountryWithTitle(title: String) {
        onView(withText(title)).perform(ViewActions.click())
    }
}