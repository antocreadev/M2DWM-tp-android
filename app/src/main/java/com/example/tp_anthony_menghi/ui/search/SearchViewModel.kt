package com.example.tp_anthony_menghi.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tp_anthony_menghi.domain.model.City
import com.example.tp_anthony_menghi.domain.repository.WeatherRepository
import com.example.tp_anthony_menghi.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel pour l'écran de recherche
 * Gère la recherche de villes avec debounce
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _searchState = MutableStateFlow<SearchState>(SearchState.Idle)
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()
    
    init {
        // Debounce sur la recherche (300ms)
        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .filter { it.length >= 2 }
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
        if (query.length < 2) {
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
