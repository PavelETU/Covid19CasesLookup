package com.wordpress.covid19caseslookup

import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.wordpress.covid19caseslookup.presentation.MainActivity
import org.junit.Rule

open class CasesLookupRules {
    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION)
    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)
}