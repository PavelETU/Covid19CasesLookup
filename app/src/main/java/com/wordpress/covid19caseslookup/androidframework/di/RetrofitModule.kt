package com.wordpress.covid19caseslookup.androidframework.di

import com.wordpress.covid19caseslookup.data.LookupService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    @Singleton
    @Provides
    fun provideRetrofit(): LookupService = Retrofit.Builder()
        .baseUrl("https://api.covid19api.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(LookupService::class.java)
}