package com.wordpress.covid19caseslookup.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wordpress.covid19caseslookup.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ListOfCountriesFragment.OnCountryChosenListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = getString(R.string.choose_country)
        if (supportFragmentManager.fragments.size == 0)
            supportFragmentManager.beginTransaction().add(R.id.container, ListOfCountriesFragment(), "List").commit()
    }

    override fun onCountryChosen(slug: String) {
        supportFragmentManager.beginTransaction().replace(R.id.container, StatsFragment.newInstance(slug), "Stats").addToBackStack(null).commit()
    }
}
