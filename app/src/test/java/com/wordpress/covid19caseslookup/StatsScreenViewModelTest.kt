package com.wordpress.covid19caseslookup

import android.content.Context
import com.wordpress.covid19caseslookup.data.LookupRepo
import com.wordpress.covid19caseslookup.data.entities.Country
import com.wordpress.covid19caseslookup.data.entities.CountryStats
import com.wordpress.covid19caseslookup.presentation.*
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
        val listOfStats = listOf(
            CountryStats(1000, 5, 900, "2020-01-22T00:00:00Z"),
        )
        repo.block = {
            withContext(coroutineTestRule.testCoroutineDispatcher) {
                delay(1000)
                listOfStats
            }
        }

        viewModel.onSlugObtained("Mexico")
        advanceTimeBy(1000)

        assertEquals(Success(listOf("Jan"), viewModel.statsToDisplay), viewModel.stateOfStatsScreen.value)
    }

    @Test
    fun `months parsed correctly`() = coroutineTestRule.testCoroutineScope.runBlockingTest {
        val listOfStats = listOf(
            CountryStats(1000, 5, 900, "2020-01-22T00:00:00Z"),
            CountryStats(1100, 9, 950, "2020-01-25T00:00:00Z"),
            CountryStats(1000, 12, 956, "2020-02-25T00:00:00Z"),
            CountryStats(1500, 20, 1300, "2020-04-26T00:00:00Z")
        )
        repo.block = { listOfStats }

        viewModel.onSlugObtained("Mexico")

        assertEquals(Success(listOf("Jan", "Feb", "Apr"), viewModel.statsToDisplay), viewModel.stateOfStatsScreen.value)
    }

    @Test
    fun `all months parsed correctly`() = coroutineTestRule.testCoroutineScope.runBlockingTest {
        val listOfStats = listOf(
            CountryStats(1000, 5, 900, "2020-01-22T00:00:00Z"),
            CountryStats(1100, 9, 950, "2020-01-25T00:00:00Z"),
            CountryStats(1000, 12, 956, "2020-02-25T00:00:00Z"),
            CountryStats(1000, 12, 956, "2020-02-25T00:00:00Z"),
            CountryStats(1000, 12, 956, "2020-02-25T00:00:00Z"),
            CountryStats(1000, 12, 956, "2020-03-25T00:00:00Z"),
            CountryStats(1000, 12, 956, "2020-04-25T00:00:00Z"),
            CountryStats(1000, 12, 956, "2020-05-25T00:00:00Z"),
            CountryStats(1000, 12, 956, "2020-06-25T00:00:00Z"),
            CountryStats(1000, 12, 956, "2020-07-25T00:00:00Z"),
            CountryStats(1000, 12, 956, "2020-08-25T00:00:00Z"),
            CountryStats(1000, 12, 956, "2020-09-25T00:00:00Z"),
            CountryStats(1000, 12, 956, "2020-10-25T00:00:00Z"),
            CountryStats(1000, 12, 956, "2020-10-25T00:00:00Z"),
            CountryStats(1000, 12, 956, "2020-10-25T00:00:00Z"),
            CountryStats(1000, 12, 956, "2020-10-25T00:00:00Z"),
            CountryStats(1000, 12, 956, "2020-11-25T00:00:00Z"),
            CountryStats(1000, 12, 956, "2020-12-25T00:00:00Z"),
            CountryStats(1000, 12, 956, "2021-01-25T00:00:00Z"),
            CountryStats(1000, 12, 956, "2021-02-25T00:00:00Z"),
            CountryStats(1000, 12, 956, "2021-04-25T00:00:00Z"),
            CountryStats(1500, 20, 1300, "2021-10-26T00:00:00Z")
        )
        repo.block = { listOfStats }

        viewModel.onSlugObtained("Mexico")

        assertEquals(Success(listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
            "Sep", "Oct", "Nov", "Dec", "Jan", "Feb", "Apr", "Oct"), viewModel.statsToDisplay), viewModel.stateOfStatsScreen.value)
    }

    @Test
    fun `after parsing confirmed cases for last month are displayed`() = coroutineTestRule.testCoroutineScope.runBlockingTest {
        val listOfStats = listOf(
            CountryStats(1000, 5, 900, "2020-01-22T00:00:00Z"),
            CountryStats(1100, 9, 950, "2020-01-25T00:00:00Z"),
            CountryStats(1000, 12, 956, "2020-02-25T00:00:00Z"),
            CountryStats(1400, 18, 1200, "2020-04-16T00:00:00Z"),
            CountryStats(1500, 20, 1300, "2020-04-26T00:00:00Z")
        )
        repo.block = { listOfStats }

        viewModel.onSlugObtained("Mexico")

        assertEquals(listOf(RecordWithCases(1400, "16"),
            RecordWithCases(1500, "26")), viewModel.statsToDisplay.value)
    }

    @Test
    fun `after parsing confirmed cases for last month are displayed even for sinle record`() = coroutineTestRule.testCoroutineScope.runBlockingTest {
        val listOfStats = listOf(
            CountryStats(88, 1, 70, "2020-01-22T00:00:00Z"),
        )
        repo.block = { listOfStats }

        viewModel.onSlugObtained("Mexico")

        assertEquals(listOf(RecordWithCases(88, "22")), viewModel.statsToDisplay.value)
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