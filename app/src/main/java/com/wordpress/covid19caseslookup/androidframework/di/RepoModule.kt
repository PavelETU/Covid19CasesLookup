package com.wordpress.covid19caseslookup.androidframework.di

import com.wordpress.covid19caseslookup.data.LookUpRepoImpl
import com.wordpress.covid19caseslookup.data.LookupRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class RepoModule {
    @Singleton
    @Binds
    abstract fun bindRepo(repoImpl: LookUpRepoImpl): LookupRepo
}