package com.cmc.openweather.presentation.wholeday

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.cmc.openweather.R
import com.cmc.openweather.common.ArgumentKey
import com.cmc.openweather.common.Resource
import com.cmc.openweather.databinding.FragmentWholeDayBinding
import com.cmc.openweather.domain.model.Hourly
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WholeDayFragment : Fragment() {

    private val viewModel: WholeDayViewModel by viewModels()
    lateinit var binding: FragmentWholeDayBinding

    private val adapter by lazy {
        WholeDayAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWholeDayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            viewModel.getWholeDayForecast(
                it.getFloat(ArgumentKey.LAT),
                it.getFloat(ArgumentKey.LONG),
                it.getBoolean(ArgumentKey.IS_CELSIUS)
            )
        }
        binding.rvWholeDayForecast.adapter = adapter
        binding.tvWholeDayCity.text =
            getString(
                R.string.whole_day_forecast_of_city,
                arguments?.getString(ArgumentKey.CITY_NAME, "") ?: ""
            )
        setupObservers()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.wholeDayForecast.observe(viewLifecycleOwner) {
                    when (it) {
                        is Resource.Success -> {
                            adapter.updateData(
                                it.data?.hourly as? MutableList<Hourly> ?: mutableListOf(),
                                viewModel.isCelsius
                            )
                        }
                        is Resource.Error -> {
                            context?.let { context ->
                                showErrorDialog(it.uiText?.asString(context) ?: "")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .show()
    }
}