package com.wordpress.covid19caseslookup

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
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

class ViewModelTest {
    @get:Rule
    var rule = InstantTaskExecutorRule()
    private val fakeMainThread = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    @Mock
    private lateinit var application: Application

    private lateinit var viewModel: LookupViewModel
    private lateinit var repo: FakeRepo
    private val listOfCountries = listOf(
        Country("Croatia", "croatia", "HR"),
        Country("Kiribati", "kiribati", "KI"),
        Country("Ireland","ireland","IE"),
        Country("Russian Federation","russia","RU")
    )
    private val countryStatsList = listOf(
        CountryStats(65324, 64, 8756, "2020-04-03T00:00:00Z"),
        CountryStats(100324, 100, 10000, "2020-04-04T00:00:00Z")
    )

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(fakeMainThread)
        repo = FakeRepo(listOfCountries, countryStatsList)
        viewModel = LookupViewModel(repo, application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        fakeMainThread.close()
    }

    @Test
    fun `after start call list is populated from fake repo`() {
        viewModel.start()
        viewModel.countries.waitForValueToSet()

        val countriesFromViewModel = viewModel.countries.value!!
        assertEquals(listOfCountries.size, countriesFromViewModel.size)
        assertEquals(listOfCountries, countriesFromViewModel)
    }

    @Test
    fun `getting empty list country shows error`() {
        repo.setCountries(emptyList())

        viewModel.start()
        viewModel.countries.waitForValueToSet()
        viewModel.showError.waitForValueToSet()

        assertTrue(viewModel.showError.value!!)
    }

    @Test
    fun `after call list is populated from fake repo`() {
        val testTitle = "Test Title"
        `when`(application.getString(R.string.choose_country)).thenReturn(testTitle)

        viewModel.start()
        viewModel.listToDisplay.waitForValueToSet()

        val values = viewModel.listToDisplay.value!!
        assertEquals(listOfCountries.size + 1, values.size)
        assertEquals(testTitle, values[0])
        assertEquals(listOfCountries[0].country, values[1])
        assertEquals(listOfCountries[1].country, values[2])
        assertEquals(listOfCountries[2].country, values[3])
        assertEquals(listOfCountries[3].country, values[4])
    }

    @Test
    fun `display last stats from the request`() {
        val statsToDisplay = "StatsToDisplay"
        `when`(application.getString(R.string.stats_to_display, countryStatsList[1].confirmed,
            countryStatsList[1].deaths, countryStatsList[1].recovered)).thenReturn(statsToDisplay)
        viewModel.start()
        viewModel.countries.waitForValueToSet()

        viewModel.onItemSelected(1)
        viewModel.statToDisplay.waitForValueToSet()

        assertEquals(statsToDisplay, viewModel.statToDisplay.value)
    }

    @Test
    fun `display empty stats if title is chosen`() {
        viewModel.start()
        viewModel.countries.waitForValueToSet()

        viewModel.onItemSelected(0)
        viewModel.statToDisplay.waitForValueToSet()

        assertEquals("", viewModel.statToDisplay.value)
    }

    @Test
    fun `right slug used`() {
        viewModel.start()
        viewModel.countries.waitForValueToSet()

        viewModel.onItemSelected(2)
        viewModel.statToDisplay.waitForValueToSet()

        assertEquals(listOfCountries[1].slug, repo.lastCountrySlugUsed)
    }

    @Test
    fun `when location is obtained right country preselected`() {
        viewModel.start()
        viewModel.countries.waitForValueToSet()

        viewModel.onLocationObtained("Ireland")
        viewModel.displayedPositionInList.waitForValueToSet()

        assertEquals(3, viewModel.displayedPositionInList.value)
    }
}