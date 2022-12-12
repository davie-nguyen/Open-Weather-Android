package com.cmc.openweather

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cmc.openweather.common.Resource
import com.cmc.openweather.common.UiText
import com.cmc.openweather.domain.model.WholeDayForecast
import com.cmc.openweather.domain.use_case.GetWeatherWholeDayUseCase
import com.cmc.openweather.presentation.wholeday.WholeDayViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WholeDayViewModelTest {
    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: WholeDayViewModel
    private var getWeatherWholeDayUseCase = mockk<GetWeatherWholeDayUseCase>()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        viewModel = WholeDayViewModel(getWeatherWholeDayUseCase, TestingDispatcher())
    }

    @Test
    fun `when use case success then resource returned should be success`() {
        val lat = 12.5f
        val long = 20.4f
        val isCelsius = true

        val wholeDayForecast = WholeDayForecast(lon = long.toString(), lat = lat.toString())

        coEvery {
            getWeatherWholeDayUseCase.invoke(lat, long, isCelsius)
        } returns flow {
            emit(Resource.Success(wholeDayForecast))
        }
        viewModel.getWholeDayForecast(lat, long, true)
        viewModel.wholeDayForecast.value?.data?.let {
            assert(it.lat == lat.toString())
            assert(it.lon == long.toString())
        }
    }

    @Test
    fun `when repo returns error then resource returned should be error`() {
        val lat = 100.5f
        val long = 100.5f
        val isCelsius = true

        coEvery {
            getWeatherWholeDayUseCase.invoke(lat, long, isCelsius)
        } returns flow {
            emit(Resource.Error(UiText.unknownError(), null))
        }

        viewModel.getWholeDayForecast(lat, long, isCelsius)
        assert(viewModel.wholeDayForecast.value is Resource.Error)
        assert(viewModel.wholeDayForecast.value?.data == null)
    }
}
