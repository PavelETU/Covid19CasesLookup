package com.wordpress.covid19caseslookup

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<LookupViewModel> { object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LookupViewModel::class.java)) {
                return LookupViewModel(LookUpRepoImpl()) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class ")
        }

    } }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.start()
        viewModel.countries.observe(this,
            Observer<List<Country>> { t -> Toast.makeText(this@MainActivity, "size is ${t!!.size}", Toast.LENGTH_SHORT).show() })
    }
}
