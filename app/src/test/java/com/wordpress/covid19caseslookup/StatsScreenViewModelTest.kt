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

        assertEquals(listOf("Jan"), viewModel.monthsToDisplay)
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

        assertEquals(listOf("Jan", "Feb", "Apr"), viewModel.monthsToDisplay)
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

        assertEquals(listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
            "Sep", "Oct", "Nov", "Dec", "Jan", "Feb", "Apr", "Oct"), viewModel.monthsToDisplay)
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

    @Test
    fun `confirm right data after switching types of cases for the last month`() = coroutineTestRule.testCoroutineScope.runBlockingTest {
        val listOfStats = listOf(
            CountryStats(1000, 5, 900, "2020-01-22T00:00:00Z"),
            CountryStats(1100, 9, 950, "2020-01-25T00:00:00Z"),
            CountryStats(1000, 12, 956, "2020-02-25T00:00:00Z"),
            CountryStats(1400, 18, 1200, "2020-04-16T00:00:00Z"),
            CountryStats(1500, 20, 1300, "2020-04-26T00:00:00Z")
        )
        repo.block = { listOfStats }

        viewModel.onSlugObtained("Mexico")

        viewModel.recoveredClick()
        assertEquals(listOf(RecordWithCases(1200, "16"),
            RecordWithCases(1300, "26")), viewModel.statsToDisplay.value)
        viewModel.lethalClick()
        assertEquals(listOf(RecordWithCases(18, "16"),
            RecordWithCases(20, "26")), viewModel.statsToDisplay.value)
        viewModel.confirmedClick()
        assertEquals(listOf(RecordWithCases(1400, "16"),
            RecordWithCases(1500, "26")), viewModel.statsToDisplay.value)
    }

    @Test
    fun `confirmed cases for different months updated properly`() = coroutineTestRule.testCoroutineScope.runBlockingTest {
        val listOfStats = listOf(
            CountryStats(1000, 5, 900, "2020-01-22T00:00:00Z"),
            CountryStats(1059, 12, 956, "2020-02-25T00:00:00Z"),
            CountryStats(1300, 12, 956, "2020-03-05T00:00:00Z"),
            CountryStats(1500, 12, 956, "2020-04-10T00:00:00Z"),
            CountryStats(1600, 12, 956, "2020-05-20T00:00:00Z"),
            CountryStats(1700, 12, 956, "2021-10-18T00:00:00Z"),
            CountryStats(1800, 12, 956, "2021-11-25T00:00:00Z"),
        )
        repo.block = { listOfStats }

        viewModel.onSlugObtained("Mexico")

        viewModel.monthClick("Jan")
        assertEquals(listOf(RecordWithCases(1000, "22")), viewModel.statsToDisplay.value)
        viewModel.monthClick("Feb")
        assertEquals(listOf(RecordWithCases(1059, "25")), viewModel.statsToDisplay.value)
        viewModel.monthClick("Mar")
        assertEquals(listOf(RecordWithCases(1300, "05")), viewModel.statsToDisplay.value)
        viewModel.monthClick("Apr")
        assertEquals(listOf(RecordWithCases(1500, "10")), viewModel.statsToDisplay.value)
        viewModel.monthClick("May")
        assertEquals(listOf(RecordWithCases(1600, "20")), viewModel.statsToDisplay.value)
        viewModel.monthClick("Oct")
        assertEquals(listOf(RecordWithCases(1700, "18")), viewModel.statsToDisplay.value)
        viewModel.monthClick("Nov")
        assertEquals(listOf(RecordWithCases(1800, "25")), viewModel.statsToDisplay.value)
    }

    @Test
    fun `lethal cases for different months updated properly`() = coroutineTestRule.testCoroutineScope.runBlockingTest {
        val listOfStats = listOf(
            CountryStats(1000, 5, 900, "2020-01-22T00:00:00Z"),
            CountryStats(1059, 10, 956, "2020-02-25T00:00:00Z"),
            CountryStats(1300, 15, 956, "2020-03-05T00:00:00Z"),
            CountryStats(1500, 20, 956, "2020-04-10T00:00:00Z"),
            CountryStats(1600, 25, 956, "2020-05-20T00:00:00Z"),
            CountryStats(1700, 30, 956, "2021-10-18T00:00:00Z"),
            CountryStats(1800, 35, 956, "2021-11-25T00:00:00Z"),
        )
        repo.block = { listOfStats }

        viewModel.onSlugObtained("Mexico")

        viewModel.lethalClick()
        viewModel.monthClick("Jan")
        assertEquals(listOf(RecordWithCases(5, "22")), viewModel.statsToDisplay.value)
        viewModel.monthClick("Feb")
        assertEquals(listOf(RecordWithCases(10, "25")), viewModel.statsToDisplay.value)
        viewModel.monthClick("Mar")
        assertEquals(listOf(RecordWithCases(15, "05")), viewModel.statsToDisplay.value)
        viewModel.monthClick("Apr")
        assertEquals(listOf(RecordWithCases(20, "10")), viewModel.statsToDisplay.value)
        viewModel.monthClick("May")
        assertEquals(listOf(RecordWithCases(25, "20")), viewModel.statsToDisplay.value)
        viewModel.monthClick("Oct")
        assertEquals(listOf(RecordWithCases(30, "18")), viewModel.statsToDisplay.value)
        viewModel.monthClick("Nov")
        assertEquals(listOf(RecordWithCases(35, "25")), viewModel.statsToDisplay.value)
    }

    @Test
    fun `recovered cases for different months updated properly`() = coroutineTestRule.testCoroutineScope.runBlockingTest {
        val listOfStats = listOf(
            CountryStats(1000, 5, 900, "2020-01-22T00:00:00Z"),
            CountryStats(1059, 12, 956, "2020-02-25T00:00:00Z"),
            CountryStats(1300, 12, 1000, "2020-03-05T00:00:00Z"),
            CountryStats(1500, 12, 1010, "2020-04-10T00:00:00Z"),
            CountryStats(1600, 12, 1020, "2020-05-20T00:00:00Z"),
            CountryStats(1700, 12, 1030, "2021-10-18T00:00:00Z"),
            CountryStats(1800, 12, 1100, "2021-11-25T00:00:00Z"),
        )
        repo.block = { listOfStats }

        viewModel.onSlugObtained("Mexico")

        viewModel.recoveredClick()
        viewModel.monthClick("Jan")
        assertEquals(listOf(RecordWithCases(900, "22")), viewModel.statsToDisplay.value)
        viewModel.monthClick("Feb")
        assertEquals(listOf(RecordWithCases(956, "25")), viewModel.statsToDisplay.value)
        viewModel.monthClick("Mar")
        assertEquals(listOf(RecordWithCases(1000, "05")), viewModel.statsToDisplay.value)
        viewModel.monthClick("Apr")
        assertEquals(listOf(RecordWithCases(1010, "10")), viewModel.statsToDisplay.value)
        viewModel.monthClick("May")
        assertEquals(listOf(RecordWithCases(1020, "20")), viewModel.statsToDisplay.value)
        viewModel.monthClick("Oct")
        assertEquals(listOf(RecordWithCases(1030, "18")), viewModel.statsToDisplay.value)
        viewModel.monthClick("Nov")
        assertEquals(listOf(RecordWithCases(1100, "25")), viewModel.statsToDisplay.value)
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