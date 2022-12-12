package com.cmc.openweather

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cmc.openweather.common.Resource
import com.cmc.openweather.common.UiText
import com.cmc.openweather.domain.model.CityForecast
import com.cmc.openweather.domain.use_case.GetCitiesUseCase
import com.cmc.openweather.presentation.city.CitiesViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CitiesViewModelTest {
    private lateinit var viewModel: CitiesViewModel
    private var getCitiesUseCase: GetCitiesUseCase = mockk<GetCitiesUseCase>()

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        viewModel = CitiesViewModel(getCitiesUseCase, TestingDispatcher())
    }

    @Test
    fun `when use case success then resource returned should be success`() {
        val keyword = "bangkok"
        val citiesForecast = mutableListOf(
            CityForecast("Bangkok"),
            CityForecast("HaNoi"),
            CityForecast("HoChiMinh")
        )

        coEvery {
            getCitiesUseCase.invoke(keyword)
        } returns flow {
            emit(Resource.Success(citiesForecast))
        }
        viewModel.getCitiesByKeyWork(keyword)
        viewModel.cities.value?.data?.forEachIndexed { index, item ->
            assert(item.name == citiesForecast[index].name)
        }
    }


    @Test
    fun `when repo returns error then resource returned should be error`() {
        val keyword = "bangkok"

        coEvery {
            getCitiesUseCase.invoke(keyword)
        } returns flow {
            emit(Resource.Error(UiText.unknownError(), null))
        }

        viewModel.getCitiesByKeyWork(keyword)
        assert(viewModel.cities.value is Resource.Error)
        assert(viewModel.cities.value?.data.isNullOrEmpty())
    }
}