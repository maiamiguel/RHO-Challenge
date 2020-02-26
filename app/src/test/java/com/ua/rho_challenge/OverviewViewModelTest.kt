package com.ua.rho_challenge

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ua.rho_challenge.overview.OverviewViewModel
import net.bytebuddy.implementation.FixedValue.nullValue
import org.junit.Test
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.getOrAwaitValue
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OverviewViewModelTest {

    private lateinit var viewModel: OverviewViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel(){
        viewModel = OverviewViewModel()
    }

    @Test
    fun insertNewTweeet() {
        // Given a fresh ViewModel
        val viewModel = OverviewViewModel()

        viewModel.insertNewTweeet()

        assertThat(viewModel.properties.getOrAwaitValue(), not(CoreMatchers.nullValue()))
    }
}