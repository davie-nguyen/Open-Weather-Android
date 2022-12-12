package com.cmc.openweather

import com.cmc.openweather.core.dispatchers.Dispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class TestingDispatcher : Dispatcher {
    override val main: CoroutineDispatcher = Dispatchers.Unconfined

    override val io: CoroutineDispatcher = Dispatchers.Unconfined

    override val db: CoroutineDispatcher = Dispatchers.Unconfined

    override val computation: CoroutineDispatcher = Dispatchers.Unconfined
}