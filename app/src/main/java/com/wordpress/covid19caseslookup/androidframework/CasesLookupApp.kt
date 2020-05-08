package com.wordpress.covid19caseslookup.androidframework

import android.app.Application
import com.wordpress.covid19caseslookup.data.LookUpRepoImpl
import com.wordpress.covid19caseslookup.data.LookupRepo
import com.wordpress.covid19caseslookup.data.LookupService
import com.wordpress.covid19caseslookup.presentation.LookupViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CasesLookupApp : Application() {
    private val appModule = module {
        single<Application> { this@CasesLookupApp }

        single<LookupService> {
            Retrofit.Builder()
                .baseUrl("https://api.covid19api.com/").addConverterFactory(GsonConverterFactory.create())
                .build().create(LookupService::class.java)
        }
        single<LookupRepo> {
            LookUpRepoImpl(get())
        }

        viewModel {
            LookupViewModel(
                get(),
                get()
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModule)
        }
    }
}