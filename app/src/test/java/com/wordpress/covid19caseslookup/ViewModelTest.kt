package com.wordpress.covid19caseslookup

import org.junit.Before

class ViewModelTest {
    private lateinit var viewModel: LookupViewModel
    private lateinit var repo: LookupRepo
    @Before
    fun setUp() {
        repo = FakeRepo()
        viewModel = LookupViewModel(repo)
    }
}