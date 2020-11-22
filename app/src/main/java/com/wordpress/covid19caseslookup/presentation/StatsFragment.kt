package com.wordpress.covid19caseslookup.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.TextView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.ui.tooling.preview.Preview
import com.wordpress.covid19caseslookup.R
import com.wordpress.covid19caseslookup.androidframework.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
        requireView().findViewById<RadioButton>(R.id.confirmed_cases).setOnClickListener { viewModel.confirmedClick() }
        requireView().findViewById<RadioButton>(R.id.lethal_cases).setOnClickListener { viewModel.lethalClick() }
        requireView().findViewById<RadioButton>(R.id.recovered_cases).setOnClickListener { viewModel.recoveredClick() }
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
                            displayStats()
                        }
                    }
                }
            }
        }
        requireView().findViewById<TextView>(R.id.error_view).setOnClickListener { viewModel.retry() }
    }

    private fun displayStats() {
        requireView().findViewById<ComposeView>(R.id.chart_for_stats).setContent {
            MaterialTheme {
                ViewForStats(viewModel)
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
fun ViewForStats(viewModel: CountryStatsViewModel = viewModel()) {
    Row(Modifier.fillMaxHeight().fillMaxHeight()) {
        ScrollableColumn(
            verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxHeight()
        ) {
            viewModel.monthsToDisplay.forEach { month ->
                val value = viewModel.displayedMonth.collectAsState()
                Row(
                    modifier = Modifier.padding(2.dp).selectable(
                        selected = (value.value == month),
                        onClick = { viewModel.monthClick(month) }
                    ).width(60.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (value.value == month),
                        onClick = { viewModel.monthClick(month) })
                    Spacer(modifier = Modifier.width(2.dp))
                    BasicText(text = month)
                }
            }
        }
        val stats = viewModel.statsToDisplay.collectAsState()
        Canvas(Modifier.fillMaxWidth().fillMaxHeight().padding(20.dp)) {
            val maxCases = stats.value.maxOf { it.cases }
            val heightForOneCase = size.height / maxCases
            val widthForOneDay = size.width / stats.value.size
            stats.value.forEachIndexed { index, recordWithCases ->
                val x = widthForOneDay * index
                drawLine(Color.Cyan, Offset(x, size.height), Offset(x, size.height - recordWithCases.cases * heightForOneCase))
            }
        }
    }

}

@ExperimentalCoroutinesApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        ViewForStats()
    }
}
