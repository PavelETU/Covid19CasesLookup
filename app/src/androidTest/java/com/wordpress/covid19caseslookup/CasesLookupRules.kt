package com.wordpress.covid19caseslookup

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.rule.GrantPermissionRule
import com.wordpress.covid19caseslookup.data.LookupRepo
import com.wordpress.covid19caseslookup.presentation.MainActivity
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.testing.HiltAndroidRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import javax.inject.Singleton

@ExperimentalCoroutinesApi
open class CasesLookupRules {
    private val hiltAndroidRule = HiltAndroidRule(this)
    val activityScenarioRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val hiltEmulatorTestRule: TestRule = RuleChain.outerRule(hiltAndroidRule)
        .around(GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION))
        .around(activityScenarioRule)

    @Before
    fun setUp() {
        hiltAndroidRule.inject()
    }

    @Module
    @InstallIn(ApplicationComponent::class)
    abstract class RepoRestModule {
        @Singleton
        @Binds
        abstract fun bindRepo(repoImpl: RepositoryImplForUITest): LookupRepo
    }
}