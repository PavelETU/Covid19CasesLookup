package com.wordpress.covid19caseslookup.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.ui.tooling.preview.Preview
import com.wordpress.covid19caseslookup.R
import com.wordpress.covid19caseslookup.androidframework.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val SLUG = "slug"

@ExperimentalCoroutinesApi
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
        viewLifecycleOwner.lifecycleScope.run {
            launch {
                viewModel.stateOfStatsScreen.collect { status ->
                    when(status) {
                        is Loading -> {
                            requireView().findViewById<TextView>(R.id.error_view).visible(false)
                            requireView().findViewById<LinearLayout>(R.id.stats_view).visible(false)
                            requireView().findViewById<ProgressBar>(R.id.loading_indicator).visible(true)
                        }
                        is Error -> {
                            requireView().findViewById<ProgressBar>(R.id.loading_indicator).visible(false)
                            requireView().findViewById<LinearLayout>(R.id.stats_view).visible(false)
                            requireView().findViewById<TextView>(R.id.error_view).visible(true)
                            requireView().findViewById<TextView>(R.id.error_view).text = status.message
                        }
                        is Success -> {
                            requireView().findViewById<ProgressBar>(R.id.loading_indicator).visible(false)
                            requireView().findViewById<TextView>(R.id.error_view).visible(false)
                            requireView().findViewById<LinearLayout>(R.id.stats_view).visible(true)
                            displayStats(status.monthsToDisplay, status.statsToDisplay)
                        }
                    }
                }
            }
        }
        requireView().findViewById<TextView>(R.id.error_view).setOnClickListener { viewModel.retry() }
    }

    private fun displayStats(monthsToDisplay: List<String>, stats: StateFlow<List<RecordWithCases>>) {
        requireView().findViewById<ComposeView>(R.id.chart_for_stats).setContent {
            MaterialTheme {
                ViewForStats(monthsToDisplay, stats)
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

@ExperimentalCoroutinesApi
@Composable
fun ViewForStats(monthsToDisplay: List<String>, statsToDisplay: StateFlow<List<RecordWithCases>>) {
    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxHeight().fillMaxWidth()) {
        BasicText(text = "Hello World!\nCompose edition", style = TextStyle(textAlign = TextAlign.Center))
        BasicText(text = "There are ${statsToDisplay.value.size} stats to display")
    }
}

@ExperimentalCoroutinesApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        ViewForStats(listOf("Jan", "Feb", "Mar"), statsToDisplay = MutableStateFlow(emptyList()))
    }
}
