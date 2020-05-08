package com.wordpress.covid19caseslookup

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class TestRunnerForUsingTestApplicationClass : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, CasesLookupAppUnderTest::class.java.name, context)
    }
}