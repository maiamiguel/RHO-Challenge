package com.ua.rho_challenge

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import com.ua.rho_challenge.network.Tweet
import com.ua.rho_challenge.network.network.ApiService
import com.ua.rho_challenge.viewmodels.DataApiStatus
import com.ua.rho_challenge.viewmodels.OverviewViewModel
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

// https://medium.com/mindorks/unit-testing-for-viewmodel-19f4d76b20d4
@RunWith(JUnit4::class)
class OverviewViewModelTest2{
    @Rule @JvmField
    val instantExecutorRule : InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var apiClient: ApiService
    var viewModel: OverviewViewModel? = null
    @Mock
    lateinit var observerStatus: Observer<DataApiStatus>
    @Mock
    lateinit var observer: Observer<ArrayList<Tweet>>
    @Mock
    lateinit var lifecycleOwner : LifecycleOwner
    lateinit var lifecycle : Lifecycle


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this);
        lifecycle = LifecycleRegistry(lifecycleOwner)
        viewModel = OverviewViewModel();
        viewModel!!.properties.observeForever(observer);
        viewModel!!.status.observeForever(observerStatus)
    }

    @Test
    fun testNull() {
        `when`<Any?>(apiClient.api?.getTweetList("teste")).thenReturn(null)
        assertNotNull(viewModel!!.properties);
        assertTrue(viewModel!!.properties.hasObservers());
    }

    @After
    fun tearDown() {
        viewModel = null;
    }
}