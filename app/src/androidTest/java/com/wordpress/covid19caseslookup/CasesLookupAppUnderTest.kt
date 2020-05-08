package com.wordpress.covid19caseslookup

import android.app.Application
import com.wordpress.covid19caseslookup.data.LookupRepo
import com.wordpress.covid19caseslookup.presentation.LookupViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class CasesLookupAppUnderTest: Application() {
    private val appUnderTestModule = module {
        single<Application> { this@CasesLookupAppUnderTest }
        single<LookupRepo> {
            RepositoryImplForUITest()
        }
        viewModel {
            LookupViewModel(get(), get())
        }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appUnderTestModule)
        }
    }
}