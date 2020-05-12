package com.ua.rho_challenge

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.architecture.blueprints.todoapp.getOrAwaitValue
import com.ua.rho_challenge.viewmodels.OverviewViewModel
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.not
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class OverviewViewModelTest {

    private lateinit var viewModel: OverviewViewModel

    @Rule @JvmField
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel(){
        viewModel = OverviewViewModel()
    }

    @Test
    fun insertNewTweeet() {
        // Given a fresh ViewModel
        val viewModel = OverviewViewModel()

        viewModel.insertNewTweet()

        assertThat(viewModel.properties.getOrAwaitValue(), not(CoreMatchers.nullValue()))
    }
}