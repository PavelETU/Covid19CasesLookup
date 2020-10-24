package com.wordpress.covid19caseslookup

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.wordpress.covid19caseslookup.data.entities.Country
import com.wordpress.covid19caseslookup.data.entities.CountryStats
import com.wordpress.covid19caseslookup.presentation.ListOfCountriesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.concurrent.Executors

@ExperimentalCoroutinesApi
class ListOfCountriesViewModelTest {
    @get:Rule
    var rule = InstantTaskExecutorRule()
    private val fakeMainThread = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    @Mock
    private lateinit var application: Application

    private lateinit var viewModel: ListOfCountriesViewModel
    private lateinit var repo: FakeRepo
    private val listOfCountries = listOf(
        Country(
            "Croatia",
            "croatia",
            "HR"
        ),
        Country(
            "Kiribati",
            "",
            "KI"
        ),
        Country(
            "Ireland",
            "ireland",
            "IE"
        ),
        Country(
            "Russian Federation",
            "russia",
            "RU"
        )
    )
    private val countryStatsList = listOf(
        CountryStats(
            65324,
            64,
            8756,
            "2020-04-03T00:00:00Z"
        ),
        CountryStats(
            100324,
            100,
            10000,
            "2020-04-04T00:00:00Z"
        )
    )

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(fakeMainThread)
        repo = FakeRepo(listOfCountries, countryStatsList)
        viewModel = ListOfCountriesViewModel(repo, application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        fakeMainThread.close()
    }

    @Test
    fun `after start call list is populated from fake repo`() {
        viewModel.countries.waitForValueToSet()

        val countriesFromViewModel = viewModel.countries.value!!
        assertEquals(listOfCountries.size, countriesFromViewModel.size)
        assertEquals(listOfCountries, countriesFromViewModel)
    }

    @Test
    fun `getting empty list country shows error`() {
        repo.setCountries(emptyList())

        viewModel.countries.waitForValueToSet()
        viewModel.showError.waitForValueToSet()

        assertTrue(viewModel.showError.value!!)
    }

    @Test
    fun `empty slug cause snackbar to show up`() {
        val noStatsString = "No Stats for country"
        `when`(application.getString(R.string.no_stats)).thenReturn(noStatsString)
        viewModel.countries.waitForValueToSet()

        viewModel.onItemSelected(1)
        viewModel.snackBarEvent.waitForValueToSet()

        assertEquals(noStatsString, viewModel.snackBarEvent.value)
    }

    @Test
    fun `right slug used`() {
        viewModel.countries.waitForValueToSet()

        viewModel.onItemSelected(2)
        viewModel.openStatsEventWithSlug.waitForValueToSet()

        assertEquals(listOfCountries[2].slug, viewModel.openStatsEventWithSlug.value)
    }

    @Test
    fun `after call list is populated from fake repo`() {
        val testTitle = "Test Title"
        `when`(application.getString(R.string.choose_country)).thenReturn(testTitle)

        viewModel.listToDisplay.waitForValueToSet()

        val values = viewModel.listToDisplay.value!!
        assertEquals(listOfCountries.size, values.size)
        assertEquals(listOfCountries[0].country, values[0])
        assertEquals(listOfCountries[1].country, values[1])
        assertEquals(listOfCountries[2].country, values[2])
        assertEquals(listOfCountries[3].country, values[3])
    }

    @Test
    fun `when location is obtained right country preselected`() {
        viewModel.countries.waitForValueToSet()

        viewModel.onLocationObtained("Ireland")
        viewModel.displayedPositionInList.waitForValueToSet()

        assertEquals(2, viewModel.displayedPositionInList.value)
    }

    @Test
    fun `loading hiding on error`() {
        repo.setCountries(emptyList())

        viewModel.countries.waitForValueToSet()
        viewModel.loading.waitForValueToSet()

        assertEquals(false, viewModel.loading.value)
    }
}