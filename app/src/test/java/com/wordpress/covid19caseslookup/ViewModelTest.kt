package com.wordpress.covid19caseslookup

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

class ViewModelTest {
    @get:Rule
    var rule = InstantTaskExecutorRule()
    private val fakeMainThread = newSingleThreadContext("Fake Android UI Thread")

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
        Dispatchers.setMain(fakeMainThread)
        repo = FakeRepo(listOfCountries)
        viewModel = LookupViewModel(repo)
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
}