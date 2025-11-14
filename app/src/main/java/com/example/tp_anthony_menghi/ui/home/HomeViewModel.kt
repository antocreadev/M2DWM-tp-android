package com.example.tp_anthony_menghi.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tp_anthony_menghi.domain.model.FavoriteCity
import com.example.tp_anthony_menghi.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel pour l'écran d'accueil
 * Gère l'état des villes favorites et le rafraîchissement
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {
    
    private val _favorites = MutableStateFlow<List<FavoriteCity>>(emptyList())
    val favorites: StateFlow<List<FavoriteCity>> = _favorites.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadFavorites()
    }
    
    /**
     * Charge la liste des favoris depuis le repository
     */
    private fun loadFavorites() {
        viewModelScope.launch {
            repository.getFavorites().collect { favs ->
                _favorites.value = favs
            }
        }
    }
    
    /**
     * Supprime une ville des favoris
     */
    fun removeFavorite(cityId: Int) {
        viewModelScope.launch {
            repository.removeFavorite(cityId)
        }
    }
    
    /**
     * Rafraîchit la météo de tous les favoris
     */
    fun refreshWeather() {
        viewModelScope.launch {
            _isLoading.value = true
            _favorites.value.forEach { fav ->
                repository.getWeather(
                    fav.city.latitude,
                    fav.city.longitude,
                    fav.city.id,
                    fav.city.name
                )
            }
            _isLoading.value = false
        }
    }
}
