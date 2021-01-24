package com.wordpress.covid19caseslookup

import android.content.Context
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
    private lateinit var repo: FakeRepo
    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        repo = FakeRepo(emptyList(), emptyList())
        viewModel = CountryStatsViewModel(repo, context)
    }

    @Test
    fun `initial state is loading`() = coroutineTestRule.testCoroutineDispatcher.runBlockingTest {
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
    fun `error state after returning empty results`() = coroutineTestRule.testCoroutineDispatcher.runBlockingTest {
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
    fun `error state in exception is thrown`() = coroutineTestRule.testCoroutineDispatcher.runBlockingTest {
        val errorString = "Error"
        `when`(context.getString(R.string.something_went_wrong_tap_to_retry)).thenReturn(errorString)
        repo.block = {
            withContext(coroutineTestRule.testCoroutineDispatcher) {
                delay(1000)
                throw HttpException(Response.error<String>(404, ResponseBody.create(null, "Something is going on")))
            }
        }

        viewModel.onSlugObtained("Mexico")
        advanceTimeBy(1000)

        assertEquals(Error(errorString), viewModel.stateOfStatsScreen.value)
    }

    @Test
    fun `successful state after result is returned`() = coroutineTestRule.testCoroutineDispatcher.runBlockingTest {
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
    fun `months parsed correctly`() = coroutineTestRule.testCoroutineDispatcher.runBlockingTest {
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
    fun `all months parsed correctly`() = coroutineTestRule.testCoroutineDispatcher.runBlockingTest {
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
    fun `after parsing confirmed cases for last month are displayed`() = coroutineTestRule.testCoroutineDispatcher.runBlockingTest {
        val listOfStats = listOf(
            CountryStats(1000, 5, 900, "2020-01-22T00:00:00Z"),
            CountryStats(1100, 9, 950, "2020-01-25T00:00:00Z"),
            CountryStats(1000, 12, 956, "2020-02-25T00:00:00Z"),
            CountryStats(1400, 18, 1200, "2020-04-16T00:00:00Z"),
            CountryStats(1500, 20, 1300, "2020-04-26T00:00:00Z")
        )
        repo.block = { listOfStats }

        viewModel.onSlugObtained("Mexico")

        assertEquals(listOf(RecordWithCases(400, "16th"),
            RecordWithCases(100, "26th")), viewModel.statsToDisplay.value)
    }

    @Test
    fun `after parsing confirmed cases for last month are displayed even for single record`() = coroutineTestRule.testCoroutineDispatcher.runBlockingTest {
        val listOfStats = listOf(
            CountryStats(88, 1, 70, "2020-01-22T00:00:00Z"),
        )
        repo.block = { listOfStats }

        viewModel.onSlugObtained("Mexico")

        assertEquals(listOf(RecordWithCases(88, "22th")), viewModel.statsToDisplay.value)
    }

    @Test
    fun `confirm right data after switching types of cases for the last month`() = coroutineTestRule.testCoroutineDispatcher.runBlockingTest {
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
        assertEquals(listOf(RecordWithCases(244, "16th"),
            RecordWithCases(100, "26th")), viewModel.statsToDisplay.value)
        viewModel.lethalClick()
        assertEquals(listOf(RecordWithCases(6, "16th"),
            RecordWithCases(2, "26th")), viewModel.statsToDisplay.value)
        viewModel.confirmedClick()
        assertEquals(listOf(RecordWithCases(400, "16th"),
            RecordWithCases(100, "26th")), viewModel.statsToDisplay.value)
    }

    @Test
    fun `confirmed cases for different months updated properly`() = coroutineTestRule.testCoroutineDispatcher.runBlockingTest {
        val listOfStats = listOf(
            CountryStats(1000, 5, 900, "2020-01-22T00:00:00Z"),
            CountryStats(1050, 12, 956, "2020-02-25T00:00:00Z"),
            CountryStats(1300, 12, 956, "2020-03-05T00:00:00Z"),
            CountryStats(1500, 12, 956, "2020-04-10T00:00:00Z"),
            CountryStats(1600, 12, 956, "2020-05-20T00:00:00Z"),
            CountryStats(1700, 12, 956, "2021-10-18T00:00:00Z"),
            CountryStats(1802, 12, 956, "2021-11-25T00:00:00Z"),
        )
        repo.block = { listOfStats }

        viewModel.onSlugObtained("Mexico")

        viewModel.monthClick(0)
        assertEquals(listOf(RecordWithCases(1000, "22th")), viewModel.statsToDisplay.value)
        viewModel.monthClick(1)
        assertEquals(listOf(RecordWithCases(50, "25th")), viewModel.statsToDisplay.value)
        viewModel.monthClick(2)
        assertEquals(listOf(RecordWithCases(250, "5th")), viewModel.statsToDisplay.value)
        viewModel.monthClick(3)
        assertEquals(listOf(RecordWithCases(200, "10th")), viewModel.statsToDisplay.value)
        viewModel.monthClick(4)
        assertEquals(listOf(RecordWithCases(100, "20th")), viewModel.statsToDisplay.value)
        viewModel.monthClick(5)
        assertEquals(listOf(RecordWithCases(100, "18th")), viewModel.statsToDisplay.value)
        viewModel.monthClick(6)
        assertEquals(listOf(RecordWithCases(102, "25th")), viewModel.statsToDisplay.value)
    }

    @Test
    fun `lethal cases for different months updated properly`() = coroutineTestRule.testCoroutineDispatcher.runBlockingTest {
        val listOfStats = listOf(
            CountryStats(1000, 5, 900, "2020-01-22T00:00:00Z"),
            CountryStats(1059, 10, 956, "2020-02-25T00:00:00Z"),
            CountryStats(1300, 16, 956, "2020-03-05T00:00:00Z"),
            CountryStats(1500, 20, 956, "2020-04-10T00:00:00Z"),
            CountryStats(1600, 25, 956, "2020-05-20T00:00:00Z"),
            CountryStats(1700, 30, 956, "2021-10-18T00:00:00Z"),
            CountryStats(1800, 45, 956, "2021-11-25T00:00:00Z"),
        )
        repo.block = { listOfStats }

        viewModel.onSlugObtained("Mexico")

        viewModel.lethalClick()
        viewModel.monthClick(0)
        assertEquals(listOf(RecordWithCases(5, "22th")), viewModel.statsToDisplay.value)
        viewModel.monthClick(1)
        assertEquals(listOf(RecordWithCases(5, "25th")), viewModel.statsToDisplay.value)
        viewModel.monthClick(2)
        assertEquals(listOf(RecordWithCases(6, "5th")), viewModel.statsToDisplay.value)
        viewModel.monthClick(3)
        assertEquals(listOf(RecordWithCases(4, "10th")), viewModel.statsToDisplay.value)
        viewModel.monthClick(4)
        assertEquals(listOf(RecordWithCases(5, "20th")), viewModel.statsToDisplay.value)
        viewModel.monthClick(5)
        assertEquals(listOf(RecordWithCases(5, "18th")), viewModel.statsToDisplay.value)
        viewModel.monthClick(6)
        assertEquals(listOf(RecordWithCases(15, "25th")), viewModel.statsToDisplay.value)
    }

    @Test
    fun `recovered cases for different months updated properly`() = coroutineTestRule.testCoroutineDispatcher.runBlockingTest {
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
        viewModel.monthClick(0)
        assertEquals(listOf(RecordWithCases(900, "22th")), viewModel.statsToDisplay.value)
        viewModel.monthClick(1)
        assertEquals(listOf(RecordWithCases(56, "25th")), viewModel.statsToDisplay.value)
        viewModel.monthClick(2)
        assertEquals(listOf(RecordWithCases(44, "5th")), viewModel.statsToDisplay.value)
        viewModel.monthClick(3)
        assertEquals(listOf(RecordWithCases(10, "10th")), viewModel.statsToDisplay.value)
        viewModel.monthClick(4)
        assertEquals(listOf(RecordWithCases(10, "20th")), viewModel.statsToDisplay.value)
        viewModel.monthClick(5)
        assertEquals(listOf(RecordWithCases(10, "18th")), viewModel.statsToDisplay.value)
        viewModel.monthClick(6)
        assertEquals(listOf(RecordWithCases(70, "25th")), viewModel.statsToDisplay.value)
    }

    @Test
    fun `when data corrupted parse cases as 0`() = coroutineTestRule.testCoroutineDispatcher.runBlockingTest {
        val listOfStats = listOf(
            CountryStats(1000, 5, 900, "2020-01-22T00:00:00Z"),
            CountryStats(900, 2, 1, "2020-02-23T00:00:00Z")
        )
        repo.block = {
            withContext(coroutineTestRule.testCoroutineDispatcher) {
                delay(1000)
                listOfStats
            }
        }

        viewModel.onSlugObtained("Mexico")
        advanceTimeBy(1000)

        viewModel.monthClick(1)
        assertEquals(listOf(RecordWithCases(0, "23th")), viewModel.statsToDisplay.value)
        viewModel.lethalClick()
        assertEquals(listOf(RecordWithCases(0, "23th")), viewModel.statsToDisplay.value)
        viewModel.recoveredClick()
        assertEquals(listOf(RecordWithCases(0, "23th")), viewModel.statsToDisplay.value)
    }

    @Test
    fun `ordinal dates are correct`() = coroutineTestRule.testCoroutineDispatcher.runBlockingTest {
        val listOfStats = listOf(
            CountryStats(1000, 5, 900, "2020-01-01T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-02T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-03T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-04T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-05T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-06T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-07T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-08T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-09T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-10T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-11T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-12T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-13T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-14T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-15T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-16T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-17T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-18T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-19T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-20T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-21T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-22T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-23T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-24T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-25T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-26T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-27T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-28T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-29T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-30T00:00:00Z"),
            CountryStats(1000, 5, 900, "2020-01-31T00:00:00Z")
        )
        repo.block = { listOfStats }

        viewModel.onSlugObtained("Mexico")

        assertEquals(listOf(
            RecordWithCases(1000, "1st"), RecordWithCases(0, "2nd"), RecordWithCases(0, "3rd"),
            RecordWithCases(0, "4th"), RecordWithCases(0, "5th"), RecordWithCases(0, "6th"),
            RecordWithCases(0, "7th"), RecordWithCases(0, "8th"), RecordWithCases(0, "9th"),
            RecordWithCases(0, "10th"), RecordWithCases(0, "11th"), RecordWithCases(0, "12th"),
            RecordWithCases(0, "13th"), RecordWithCases(0, "14th"), RecordWithCases(0, "15th"),
            RecordWithCases(0, "16th"), RecordWithCases(0, "17th"), RecordWithCases(0, "18th"),
            RecordWithCases(0, "19th"), RecordWithCases(0, "20th"), RecordWithCases(0, "21th"),
            RecordWithCases(0, "22th"), RecordWithCases(0, "23th"), RecordWithCases(0, "24th"),
            RecordWithCases(0, "25th"), RecordWithCases(0, "26th"), RecordWithCases(0, "27th"),
            RecordWithCases(0, "28th"), RecordWithCases(0, "29th"), RecordWithCases(0, "30th"),
            RecordWithCases(0, "31th")), viewModel.statsToDisplay.value)
    }
}