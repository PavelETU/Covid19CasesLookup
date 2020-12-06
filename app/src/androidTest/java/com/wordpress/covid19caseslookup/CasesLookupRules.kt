package com.wordpress.covid19caseslookup

import androidx.test.rule.ActivityTestRule
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
import javax.inject.Singleton

open class CasesLookupRules {
    private val hiltAndroidRule = HiltAndroidRule(this)

    @get:Rule
    val hiltEmulatorTestRule = RuleChain.outerRule(hiltAndroidRule)
        .around(GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION))
        .around(ActivityTestRule(MainActivity::class.java))

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