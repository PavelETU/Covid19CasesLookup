package com.wordpress.covid19caseslookup.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextAlign
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.ui.tooling.preview.Preview
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.showError.observe(viewLifecycleOwner, { showError ->
            requireView().findViewById<TextView>(R.id.error_view).visible(showError)
        })
        viewModel.loading.observe(viewLifecycleOwner, { loading ->
            requireView().findViewById<ProgressBar>(R.id.loading_indicator).visible(loading)
        })
        requireView().findViewById<TextView>(R.id.error_view)
            .setOnClickListener { viewModel.retry() }
        viewModel.countryStats.observe(viewLifecycleOwner, { displayStats(it) })
    }

    private fun displayStats(stats: List<CountryStats>) {
        requireView().findViewById<LinearLayout>(R.id.stats_view).visible(true)
        requireView().findViewById<ComposeView>(R.id.chart_for_stats).setContent {
            MaterialTheme {
                ViewForStats(statsToDisplay = stats)
            }
        }
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

@Composable
fun ViewForStats(statsToDisplay: List<CountryStats>) {
    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxHeight().fillMaxWidth()) {
        Text(text = "Hello World!\nCompose edition", textAlign = TextAlign.Center)
        Text(text = "There are ${statsToDisplay.size} stats to display")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        ViewForStats(statsToDisplay = emptyList())
    }
}
