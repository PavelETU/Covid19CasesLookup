package com.wordpress.covid19caseslookup

import android.content.Context
import com.wordpress.covid19caseslookup.data.LookupRepo
import com.wordpress.covid19caseslookup.data.entities.Country
import com.wordpress.covid19caseslookup.data.entities.CountryStats
import com.wordpress.covid19caseslookup.presentation.CountryStatsViewModel
import com.wordpress.covid19caseslookup.presentation.Error
import com.wordpress.covid19caseslookup.presentation.Loading
import com.wordpress.covid19caseslookup.presentation.Success
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.HttpException
import retrofit2.Response

@ExperimentalCoroutinesApi
class StatsScreenViewModelTest {
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()
    private lateinit var viewModel: CountryStatsViewModel
    @Mock
    private lateinit var context: Context
    private lateinit var repo: FakeRepository
    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        repo = FakeRepository()
        viewModel = CountryStatsViewModel(repo, context)
    }

    @Test
    fun `initial state is loading`() = coroutineTestRule.testCoroutineScope.runBlockingTest {
        `when`(context.getString(R.string.no_stats)).thenReturn("")
        repo.block = {
            withContext(coroutineTestRule.testCoroutineDispatcher) {
                delay(1000)
                return@withContext emptyList<CountryStats>()
            }
        }

        viewModel.onSlugObtained("Mexico")

        assertEquals(Loading, viewModel.stateOfStatsScreen.value)
    }

    @Test
    fun `error state after returning empty results`() = coroutineTestRule.testCoroutineScope.runBlockingTest {
        val emptyResults = "Empty"
        repo.block = {
            withContext(coroutineTestRule.testCoroutineDispatcher) {
                delay(1000)
                return@withContext emptyList<CountryStats>()
            }
        }
        `when`(context.getString(R.string.no_stats)).thenReturn(emptyResults)

        viewModel.onSlugObtained("Mexico")
        advanceTimeBy(1000)

        assertEquals(Error(emptyResults), viewModel.stateOfStatsScreen.value)
    }

    @Test
    fun `error state in exception is thrown`() = coroutineTestRule.testCoroutineScope.runBlockingTest {
        val errorString = "Error"
        repo.block = {
            withContext(coroutineTestRule.testCoroutineDispatcher) {
                delay(1000)
                throw HttpException(Response.error<String>(404, ResponseBody.create(null, "Something is going on")))
            }
        }
        `when`(context.getString(R.string.something_went_wrong_tap_to_retry)).thenReturn(errorString)

        viewModel.onSlugObtained("Mexico")
        advanceTimeBy(1000)

        assertEquals(Error(errorString), viewModel.stateOfStatsScreen.value)
    }

    @Test
    fun `successful state after result is returned`() = coroutineTestRule.testCoroutineScope.runBlockingTest {
        val errorString = "Error"
        val listOfStats = listOf(CountryStats(1000, 5, 900, "15-10-20"))
        repo.block = {
            withContext(coroutineTestRule.testCoroutineDispatcher) {
                delay(1000)
                listOfStats
            }
        }
        `when`(context.getString(R.string.something_went_wrong_tap_to_retry)).thenReturn(errorString)

        viewModel.onSlugObtained("Mexico")
        advanceTimeBy(1000)

        assertEquals(Success(emptyList(), viewModel.statsToDisplay), viewModel.stateOfStatsScreen.value)
        assertEquals(listOfStats, viewModel.statsToDisplay.value)
    }
}

private class FakeRepository @ExperimentalCoroutinesApi constructor(): LookupRepo {
    lateinit var block: suspend () -> List<CountryStats>

    override suspend fun getCountries(): List<Country> {
        throw UnsupportedOperationException()
    }

    override suspend fun getCountrySummary(countrySlug: String): List<CountryStats> {
        return block()
    }

}