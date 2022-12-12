package com.cmc.openweather

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cmc.openweather.common.Resource
import com.cmc.openweather.common.UiText
import com.cmc.openweather.domain.model.Coord
import com.cmc.openweather.domain.model.CurrentForecast
import com.cmc.openweather.domain.model.Main
import com.cmc.openweather.domain.use_case.GetCurrentForecastUseCase
import com.cmc.openweather.presentation.detail.WeatherDetailViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WeatherDetailViewModelTest {
    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: WeatherDetailViewModel

    private val weatherUseCase = mockk<GetCurrentForecastUseCase>()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        viewModel = WeatherDetailViewModel(weatherUseCase, TestingDispatcher())
    }

    @Test
    fun `when use case success then resource returned should be success`() {
        val lat = 100.5f
        val long = 100.5f
        val temp = 30f

        val currentForecast = CurrentForecast(coord = Coord(long, lat), main = Main(temp = temp))
        coEvery {
            weatherUseCase.invoke(lat, long)
        } returns flow {
            emit(Resource.Success(currentForecast))
        }
        viewModel.getCurrentForecast(lat, long)
        assert(viewModel.currentForecast.value is Resource.Success)
        viewModel.currentForecast.value?.data?.let {
            assert(it.coord?.lat == lat)
            assert(it.coord?.lon == long)
            assert(it.main?.temp == temp)
        }
    }

    @Test
    fun `when repo returns error then resource returned should be error`() {
        val lat = 100.5f
        val long = 100.5f

        coEvery {
            weatherUseCase.invoke(lat, long)
        } returns flow {
            emit(Resource.Error(UiText.unknownError(), null))
        }
        viewModel.getCurrentForecast(lat, long)
        assert(viewModel.currentForecast.value is Resource.Error)
        assert(viewModel.currentForecast.value?.data == null)
    }
}
