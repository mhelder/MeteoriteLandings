package com.antonio.samir.meteoritelandingsspots.features.list.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.antonio.samir.meteoritelandingsspots.rule.MainCoroutineRule
import com.antonio.samir.meteoritelandingsspots.service.business.MeteoriteServiceInterface
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import java.lang.Thread.sleep
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class MeteoriteListViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule: TestRule = MainCoroutineRule()

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    private lateinit var meteoriteService: MeteoriteServiceInterface

    private lateinit var gpsTracker: GPSTrackerInterface

    private lateinit var viewModel: MeteoriteListViewModel

    private var location: String? = null

    @Before
    fun setUp() {

        meteoriteService = mock()

        gpsTracker = mock()

        viewModel = MeteoriteListViewModel(meteoriteService, gpsTracker, Dispatchers.Default)

    }

    @Test
    fun loadMeteoriteSuccess() = runBlockingTest {

        val data = MutableLiveData<List<Meteorite>>()

        data.value = listOf(Meteorite().apply {
            id = 123
        })

        Mockito.`when`(meteoriteService.loadMeteorites(location)).thenReturn(data)

        viewModel.loadMeteorites(location)

        sleep(1000)

        val meteorite = viewModel.meteorites.apply {
            observeForever {}
        }

        val loadingStatus = viewModel.loadingStatus.apply {
            observeForever {}
        }

        val actual = meteorite.value
        val expected = data.value
        assertEquals(expected, actual)

        assertEquals(MeteoriteListViewModel.DownloadStatus.DONE, loadingStatus.value)

    }

    @Test
    fun loadMeteoriteError() = runBlockingTest {

        val data = MutableLiveData<List<Meteorite>>()

        data.value = listOf(Meteorite().apply {
            id = 123
        })

        Mockito.`when`(meteoriteService.loadMeteorites(location)).thenThrow(Error("some error"))

        viewModel.loadMeteorites(location)

        sleep(1000)

        val meteorite = viewModel.meteorites.apply {
            observeForever {}
        }

        val loadingStatus = viewModel.loadingStatus.apply {
            observeForever {}
        }

        val actual = meteorite.value
        val expected = data.value
        assertEquals(expected, actual)

        assertEquals(MeteoriteListViewModel.DownloadStatus.UNABLE_TO_FETCH, loadingStatus.value)

    }

}