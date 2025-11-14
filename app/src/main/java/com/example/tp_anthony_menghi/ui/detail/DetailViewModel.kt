package com.example.tp_anthony_menghi.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tp_anthony_menghi.domain.model.City
import com.example.tp_anthony_menghi.domain.model.Weather
import com.example.tp_anthony_menghi.domain.repository.WeatherRepository
import com.example.tp_anthony_menghi.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel pour l'écran de détail météo
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: WeatherRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val cityId: Int = savedStateHandle["cityId"] ?: 0
    private val latitude: Double = savedStateHandle.get<Float>("latitude")?.toDouble() ?: 0.0
    private val longitude: Double = savedStateHandle.get<Float>("longitude")?.toDouble() ?: 0.0
    private val cityName: String = savedStateHandle["cityName"] ?: ""
    
    private val _weatherState = MutableStateFlow<Resource<Weather>>(Resource.Loading())
    val weatherState: StateFlow<Resource<Weather>> = _weatherState.asStateFlow()
    
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()
    
    init {
        loadWeather()
        checkFavorite()
    }
    
    /**
     * Charge les données météo
     */
    private fun loadWeather() {
        viewModelScope.launch {
            _weatherState.value = Resource.Loading()
            val result = repository.getWeather(latitude, longitude, cityId, cityName)
            _weatherState.value = result
        }
    }
    
    /**
     * Vérifie si la ville est en favoris
     */
    private fun checkFavorite() {
        viewModelScope.launch {
            _isFavorite.value = repository.isFavorite(cityId)
        }
    }
    
    /**
     * Bascule l'état favori de la ville
     */
    fun toggleFavorite() {
        viewModelScope.launch {
            if (_isFavorite.value) {
                repository.removeFavorite(cityId)
            } else {
                val city = City(cityId, cityName, latitude, longitude, "")
                repository.addFavorite(city)
            }
            _isFavorite.value = !_isFavorite.value
        }
    }
    
    /**
     * Rafraîchit les données météo
     */
    fun refresh() {
        loadWeather()
    }
}
