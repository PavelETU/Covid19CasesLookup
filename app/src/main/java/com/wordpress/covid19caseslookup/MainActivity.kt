package com.wordpress.covid19caseslookup

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<LookupViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(LookupViewModel::class.java)) {
                    return LookupViewModel(LookUpRepoImpl(), application) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class ")
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.start()
        viewModel.listToDisplay.observe(this, Observer<List<String>> { displayCountries(it) })
        viewModel.showError.observe(this, Observer { showError ->
            error_view.visible(showError)
            country_spinner.visible(!showError)
        })
        error_view.setOnClickListener { viewModel.start() }
    }

    private fun displayCountries(countries: List<String>) {
        country_spinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countries)
    }
}

fun View.visible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}
