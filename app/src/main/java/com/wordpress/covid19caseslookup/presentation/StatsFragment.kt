package com.wordpress.covid19caseslookup.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradient
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.ui.tooling.preview.Preview
import com.wordpress.covid19caseslookup.androidframework.visible
import com.wordpress.covid19caseslookup.databinding.FragmentStatsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val SLUG = "slug"

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class StatsFragment : Fragment() {
    private var _binding: FragmentStatsBinding? = null
    // Non nullable variable to be accessed during view active lifecycle
    private val binding get() = _binding!!
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
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = slug.capitalize()
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.confirmedCases.setOnClickListener { viewModel.confirmedClick() }
        binding.lethalCases.setOnClickListener { viewModel.lethalClick() }
        binding.recoveredCases.setOnClickListener { viewModel.recoveredClick() }
        viewLifecycleOwner.lifecycleScope.run {
            launch {
                viewModel.stateOfStatsScreen.collect { status ->
                    when(status) {
                        is Loading -> {
                            binding.errorView.visible(false)
                            binding.statsView.visible(false)
                            binding.loadingIndicator.visible(true)
                        }
                        is Error -> {
                            binding.loadingIndicator.visible(false)
                            binding.statsView.visible(false)
                            binding.errorView.visible(true)
                            binding.errorView.text = status.message
                        }
                        is Success -> {
                            binding.loadingIndicator.visible(false)
                            binding.errorView.visible(false)
                            binding.statsView.visible(true)
                            displayStats()
                        }
                    }
                }
            }
        }
        binding.errorView.setOnClickListener { viewModel.retry() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun displayStats() {
        binding.chartForStats.setContent {
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
        ScrollableColumn(Modifier.fillMaxHeight().fillMaxWidth().padding(10.dp)) {
            val statsCount = stats.value.size
            val oneBarHeight = 50
            Canvas(Modifier.fillMaxWidth().height((statsCount*oneBarHeight).dp)) {
                val heightInPx = oneBarHeight.dp.toPx()
                val padding = 5.dp.toPx()
                val heightOfOneBar = heightInPx - padding * 2
                val maxCases = stats.value.maxOf { it.cases }
                val widthForOneCase = size.width / maxCases
                val paint = android.graphics.Paint()
                paint.textSize = 50F
                val distanceToTheBase = (paint.descent() + paint.ascent())/2F
                paint.textSize = 40F
                val distanceToTheBaseSmallText = (paint.descent() + paint.ascent())/2F
                val middleOfTheBar = heightInPx / 2
                stats.value.forEachIndexed { index, recordWithCases ->
                    val y = heightInPx * index
                    val rightOfTheBar = recordWithCases.cases * widthForOneCase
                    val endColorForGradient = when {
                        rightOfTheBar < size.width/2 -> {
                            Color.Cyan
                        }
                        rightOfTheBar < size.width/1.5 -> {
                            Color.Magenta
                        }
                        else -> {
                            Color.Red
                        }
                    }
                    if (recordWithCases.cases != 0)
                        drawRect(LinearGradient(listOf(Color.Green, endColorForGradient), 0F, y + middleOfTheBar, rightOfTheBar, y + middleOfTheBar),
                            Offset(0F, y + padding), Size(rightOfTheBar, heightOfOneBar))
                    drawIntoCanvas {
                        paint.textSize = 50F
                        drawContext.canvas.nativeCanvas.drawText(recordWithCases.day, 0F, y + middleOfTheBar - distanceToTheBase, paint)
                        paint.textSize = 40F
                        val cases = recordWithCases.cases.toString()
                        val widthOfText = paint.measureText(cases)
                        drawContext.canvas.nativeCanvas.drawText(cases, size.width - widthOfText, y + middleOfTheBar - distanceToTheBaseSmallText, paint)
                    }
                }
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
