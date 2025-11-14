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
    
    // Set pour tracker les villes dont on est en train de charger la météo
    private val loadingCities = mutableSetOf<Int>()
    
    init {
        loadFavorites()
    }
    
    /**
     * Charge la liste des favoris depuis le repository
     * et charge automatiquement la météo pour les favoris sans données
     */
    private fun loadFavorites() {
        viewModelScope.launch {
            repository.getFavorites().collect { favs ->
                _favorites.value = favs
                
                // Charger la météo pour les favoris qui n'ont pas encore de données
                favs.forEach { fav ->
                    if (fav.weather == null && !loadingCities.contains(fav.city.id)) {
                        loadWeatherForFavorite(fav)
                    }
                }
                
                // Mettre à jour isLoading
                _isLoading.value = loadingCities.isNotEmpty()
            }
        }
    }
    
    /**
     * Charge la météo pour un favori spécifique
     */
    private fun loadWeatherForFavorite(favorite: FavoriteCity) {
        viewModelScope.launch {
            loadingCities.add(favorite.city.id)
            _isLoading.value = true
            
            try {
                repository.getWeather(
                    favorite.city.latitude,
                    favorite.city.longitude,
                    favorite.city.id,
                    favorite.city.name
                )
            } finally {
                loadingCities.remove(favorite.city.id)
                _isLoading.value = loadingCities.isNotEmpty()
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
                loadingCities.add(fav.city.id)
            }
            
            try {
                _favorites.value.forEach { fav ->
                    repository.getWeather(
                        fav.city.latitude,
                        fav.city.longitude,
                        fav.city.id,
                        fav.city.name
                    )
                }
            } finally {
                loadingCities.clear()
                _isLoading.value = false
            }
        }
    }
}
