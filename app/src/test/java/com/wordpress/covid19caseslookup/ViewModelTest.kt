package com.wordpress.covid19caseslookup

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
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

class ViewModelTest {
    @get:Rule
    var rule = InstantTaskExecutorRule()
    private val fakeMainThread = newSingleThreadContext("Fake Android UI Thread")
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

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(fakeMainThread)
        repo = FakeRepo(listOfCountries)
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
    }
}