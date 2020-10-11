package com.wordpress.covid19caseslookup.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.wordpress.covid19caseslookup.R
import com.wordpress.covid19caseslookup.androidframework.visible
import com.wordpress.covid19caseslookup.data.entities.CountryStats
import dagger.hilt.android.AndroidEntryPoint

private const val SLUG = "slug"

@AndroidEntryPoint
class StatsFragment : Fragment() {
    private lateinit var slug: String
    private val viewModel: CountryStatsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            slug = it.getString(SLUG)!!
            viewModel.onSlugObtained(slug)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.showError.observe(viewLifecycleOwner, { showError ->
            requireView().findViewById<TextView>(R.id.error_view).visible(showError)
        })
        viewModel.loading.observe(viewLifecycleOwner, { loading ->
            requireView().findViewById<ProgressBar>(R.id.loading_indicator).visible(loading)
        })
        requireView().findViewById<TextView>(R.id.error_view).setOnClickListener { viewModel.retry() }
        viewModel.countryStats.observe(viewLifecycleOwner, { displayStats(it) })
    }

    private fun displayStats(stats: List<CountryStats>) {
        requireView().findViewById<LinearLayout>(R.id.stats_view).visible(true)

    }

    companion object {
        @JvmStatic
        fun newInstance(slug: String) =
            StatsFragment().apply {
                arguments = Bundle().apply {
                    putString(SLUG, slug)
                }
            }
    }
}