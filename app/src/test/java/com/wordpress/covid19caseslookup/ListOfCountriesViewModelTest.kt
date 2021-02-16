package com.wordpress.covid19caseslookup

import android.content.Context
import com.wordpress.covid19caseslookup.data.entities.Country
import com.wordpress.covid19caseslookup.data.entities.CountryStats
import com.wordpress.covid19caseslookup.presentation.CountriesFailedToLoad
import com.wordpress.covid19caseslookup.presentation.CountriesLoaded
import com.wordpress.covid19caseslookup.presentation.ListOfCountriesViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class ListOfCountriesViewModelTest {
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()
    @Mock
    private lateinit var application: Context

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
        repo = FakeRepo(emptyList(), countryStatsList)
        viewModel = ListOfCountriesViewModel(repo, application)
    }

    @Test
    fun `with empty repo initial state is error`() {
        assertEquals(CountriesFailedToLoad, viewModel.stateOfCountriesList.value)
    }

    @Test
    fun `countries list parsed right`() {
        repo.setCountries(listOfCountries)

        viewModel.retry()

        assertTrue(viewModel.stateOfCountriesList.value is CountriesLoaded)
        assertEquals(listOf("Croatia", "Kiribati", "Ireland", "Russian Federation"),
            (viewModel.stateOfCountriesList.value as CountriesLoaded).countries.value)
    }

    @Test
    fun `countries sorted as per search term`() {
        //List of countries "Croatia", "Kiribati", "Ireland", "Russian Federation"
        repo.setCountries(listOfCountries)

        viewModel.retry()

        viewModel.onQueryChanged("i")
        assertEquals(listOf("Croatia", "Kiribati", "Ireland", "Russian Federation"),
            (viewModel.stateOfCountriesList.value as CountriesLoaded).countries.value)

        viewModel.onQueryChanged("ir")
        assertEquals(listOf("Kiribati", "Ireland"),
            (viewModel.stateOfCountriesList.value as CountriesLoaded).countries.value)

        viewModel.onQueryChanged("ire")
        assertEquals(listOf("Ireland"),
            (viewModel.stateOfCountriesList.value as CountriesLoaded).countries.value)

        viewModel.onQueryChanged("ir")
        assertEquals(listOf("Kiribati", "Ireland"),
            (viewModel.stateOfCountriesList.value as CountriesLoaded).countries.value)

        viewModel.onQueryChanged("i")
        assertEquals(listOf("Croatia", "Kiribati", "Ireland", "Russian Federation"),
            (viewModel.stateOfCountriesList.value as CountriesLoaded).countries.value)

        viewModel.onQueryChanged("")
        assertEquals(listOf("Croatia", "Kiribati", "Ireland", "Russian Federation"),
            (viewModel.stateOfCountriesList.value as CountriesLoaded).countries.value)

        viewModel.onQueryChanged(null)
        assertEquals(listOf("Croatia", "Kiribati", "Ireland", "Russian Federation"),
            (viewModel.stateOfCountriesList.value as CountriesLoaded).countries.value)
    }
}