package com.cmc.openweather.presentation.city

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.cmc.openweather.R
import com.cmc.openweather.common.ArgumentKey
import com.cmc.openweather.common.Resource
import com.cmc.openweather.common.hideKeyboard
import com.cmc.openweather.databinding.FragmentCitiesBinding
import com.cmc.openweather.domain.model.CityForecast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CitiesFragment : Fragment() {

    private lateinit var binding: FragmentCitiesBinding
    private val viewModel: CitiesViewModel by viewModels()

    private val adapter by lazy {
        CitiesAdapter().apply {
            onItemClicked = { it -> navigateToWeatherDetailFragment(it) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            rvCities.adapter = adapter
        }
        setUpListeners()
        setupObserver()
    }

    private fun navigateToWeatherDetailFragment(position: Int) {
        viewModel.cities.value?.data?.get(position)?.let { cityForecast ->
            findNavController().navigate(
                R.id.action_cityFragment_to_detailFragment,
                bundleOf(
                    ArgumentKey.LAT to cityForecast.lat,
                    ArgumentKey.LONG to cityForecast.long
                )
            )
        }
    }

    private fun setUpListeners() {
        binding.run {
            imgSearch.setOnClickListener {
                edtCity.clearFocus()
                this@CitiesFragment.context?.hideKeyboard(edtCity)
                val keyword = edtCity.text.toString()
                if (keyword.isNotBlank()) {
                    viewModel.getCitiesByKeyWork(keyword)
                }
            }
        }
    }

    private fun setupObserver() {
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cities.observe(viewLifecycleOwner) {
                    when (it) {
                        is Resource.Success -> {
                            adapter.updateData(
                                it.data as? MutableList<CityForecast> ?: mutableListOf()
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
