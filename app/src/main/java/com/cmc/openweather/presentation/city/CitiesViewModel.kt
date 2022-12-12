package com.cmc.openweather.presentation.city

import androidx.lifecycle.MutableLiveData
import com.cmc.openweather.common.Resource
import com.cmc.openweather.common.SingleLiveEvent
import com.cmc.openweather.core.dispatchers.Dispatcher
import com.cmc.openweather.core.viewmodel.BaseViewModel
import com.cmc.openweather.domain.model.CityForecast
import com.cmc.openweather.domain.use_case.GetCitiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CitiesViewModel @Inject constructor(
    private val getCitiesUseCase: GetCitiesUseCase,
    dispatcher: Dispatcher
) : BaseViewModel(dispatcher) {

    private val _cities = SingleLiveEvent<Resource<List<CityForecast>>>()
    val cities: MutableLiveData<Resource<List<CityForecast>>> = _cities

    fun getCitiesByKeyWork(keyWord: String) {
        launchOnBack {
            getCitiesUseCase.invoke(keyWord).collect {
                _cities.postValue(it)
            }
        }
    }
}
