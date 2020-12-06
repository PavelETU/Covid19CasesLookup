package com.wordpress.covid19caseslookup

import androidx.test.core.app.ActivityScenario
import androidx.test.rule.GrantPermissionRule
import com.wordpress.covid19caseslookup.data.LookupRepo
import com.wordpress.covid19caseslookup.presentation.MainActivity
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Before
import org.junit.Rule
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import javax.inject.Singleton

open class CasesLookupRules {
    private val hiltAndroidRule = HiltAndroidRule(this)

    @get:Rule
    val hiltEmulatorTestRule: TestRule = RuleChain.outerRule(hiltAndroidRule)
        .around(GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION))
    private lateinit var activityScenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        hiltAndroidRule.inject()
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @Module
    @InstallIn(ApplicationComponent::class)
    abstract class RepoRestModule {
        @Singleton
        @Binds
        abstract fun bindRepo(repoImpl: RepositoryImplForUITest): LookupRepo
    }
}