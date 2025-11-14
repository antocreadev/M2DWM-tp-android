package com.example.tp_anthony_menghi.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tp_anthony_menghi.domain.model.City
import com.example.tp_anthony_menghi.domain.repository.WeatherRepository
import com.example.tp_anthony_menghi.utils.LocationHelper
import com.example.tp_anthony_menghi.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel pour l'écran de recherche
 * Gère la recherche de villes avec debounce et la géolocalisation
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationHelper: LocationHelper
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _searchState = MutableStateFlow<SearchState>(SearchState.Idle)
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()
    
    private val _locationState = MutableStateFlow<LocationState>(LocationState.Idle)
    val locationState: StateFlow<LocationState> = _locationState.asStateFlow()
    
    init {
        // Debounce sur la recherche (300ms)
        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .filter { it.length >= 3 } // Min 3 caractères pour fuzzy matching
                .distinctUntilChanged()
                .collect { query ->
                    searchCities(query)
                }
        }
    }
    
    /**
     * Met à jour la requête de recherche
     */
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        if (query.length < 3) {
            _searchState.value = SearchState.Idle
        } else {
            _searchState.value = SearchState.Loading
        }
    }
    
    /**
     * Recherche des villes via le repository
     */
    private suspend fun searchCities(query: String) {
        _searchState.value = SearchState.Loading
        
        when (val result = repository.searchCities(query)) {
            is Resource.Success -> {
                _searchState.value = SearchState.Success(result.data ?: emptyList())
            }
            is Resource.Error -> {
                _searchState.value = SearchState.Error(result.message ?: "Erreur inconnue")
            }
            is Resource.Loading -> {
                _searchState.value = SearchState.Loading
            }
        }
    }
    
    /**
     * Ajoute une ville aux favoris
     */
    fun addFavorite(city: City) {
        viewModelScope.launch {
            repository.addFavorite(city)
        }
    }
    
    /**
     * Vérifie si une ville est en favoris
     */
    suspend fun isFavorite(cityId: Int): Boolean {
        return repository.isFavorite(cityId)
    }
    
    /**
     * Vérifie si les permissions de localisation sont accordées
     */
    fun hasLocationPermission(): Boolean {
        return locationHelper.hasLocationPermission()
    }
    
    /**
     * Géolocalise l'utilisateur et trouve les villes à proximité
     */
    fun findNearbyCities() {
        viewModelScope.launch {
            _locationState.value = LocationState.Loading
            
            // Récupérer la position actuelle
            locationHelper.getCurrentLocation()
                .onSuccess { location ->
                    // Rechercher les villes proches
                    when (val result = repository.getCitiesNearLocation(
                        location.latitude,
                        location.longitude
                    )) {
                        is Resource.Success -> {
                            val cities = result.data ?: emptyList()
                            _locationState.value = LocationState.Success(cities)
                        }
                        is Resource.Error -> {
                            _locationState.value = LocationState.Error(
                                result.message ?: "Erreur lors de la recherche"
                            )
                        }
                        is Resource.Loading -> {
                            _locationState.value = LocationState.Loading
                        }
                    }
                }
                .onFailure { exception ->
                    _locationState.value = LocationState.Error(
                        exception.message ?: "Impossible d'obtenir votre position"
                    )
                }
        }
    }
    
    /**
     * Réinitialise l'état de localisation
     */
    fun resetLocationState() {
        _locationState.value = LocationState.Idle
    }
}

/**
 * États de la recherche
 */
sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Success(val cities: List<City>) : SearchState()
    data class Error(val message: String) : SearchState()
}

/**
 * États de la géolocalisation
 */
sealed class LocationState {
    object Idle : LocationState()
    object Loading : LocationState()
    data class Success(val cities: List<City>) : LocationState()
    data class Error(val message: String) : LocationState()
}
