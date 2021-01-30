package com.wordpress.covid19caseslookup

import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.isNotHidden
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.wordpress.covid19caseslookup.presentation.MainActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.allOf

@ExperimentalCoroutinesApi
fun CasesLookupRules.bddTestCase(func: BDDTestCase.() -> Unit): BDDTestCase {
    val bddTestCase = BDDTestCase(activityScenarioRule)
    bddTestCase.func()
    return bddTestCase
}

class BDDTestCase(private val composeScenarioRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) {
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

    fun iSeeFollowingTypesOfStats(types: List<String>) {
        types.forEach {
            onView(withText(it)).check(matches(isDisplayed()))
        }
    }

    fun iSeeFollowingMonths(months: List<String>) {
        val monthToMonthsCount = HashMap<String, Int>()
        months.forEach {
            monthToMonthsCount[it] = monthToMonthsCount[it]?.plus(1) ?: 1
        }
        monthToMonthsCount.forEach {
            val onAllNodesWithText = composeScenarioRule.onAllNodesWithText(it.key, useUnmergedTree = true)
            onAllNodesWithText.assertCountEquals(it.value)
            onAllNodesWithText.assertAll(isNotHidden())

        }
    }
}