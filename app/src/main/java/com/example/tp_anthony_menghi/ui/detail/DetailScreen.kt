package com.example.tp_anthony_menghi.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tp_anthony_menghi.domain.model.Weather
import com.example.tp_anthony_menghi.domain.model.WeatherCondition
import com.example.tp_anthony_menghi.ui.detail.components.HourlyForecastItem
import com.example.tp_anthony_menghi.ui.detail.components.WeatherMetricCard
import com.example.tp_anthony_menghi.utils.Resource
import kotlin.math.roundToInt

/**
 * Écran de détail météo d'une ville
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val weatherState by viewModel.weatherState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Météo") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "Retirer des favoris" else "Ajouter aux favoris",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Rafraîchir"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (weatherState) {
                is Resource.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                is Resource.Success -> {
                    val weather = (weatherState as Resource.Success<Weather>).data
                    if (weather != null) {
                        WeatherContent(weather = weather)
                    }
                }
                
                is Resource.Error -> {
                    ErrorView(
                        message = (weatherState as Resource.Error).message ?: "Erreur inconnue",
                        onRetry = { viewModel.refresh() }
                    )
                }
            }
        }
    }
}

/**
 * Contenu principal affichant toutes les informations météo
 */
@Composable
private fun WeatherContent(weather: Weather) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // En-tête avec température et ville
        item {
            WeatherHeader(weather = weather)
        }
        
        // Métriques principales
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WeatherMetricCard(
                    title = "Min",
                    value = "${weather.minTemperature.roundToInt()}°",
                    icon = "🌡️",
                    modifier = Modifier.weight(1f)
                )
                WeatherMetricCard(
                    title = "Max",
                    value = "${weather.maxTemperature.roundToInt()}°",
                    icon = "🌡️",
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WeatherMetricCard(
                    title = "Vent",
                    value = "${weather.windSpeed.roundToInt()} km/h",
                    icon = "💨",
                    modifier = Modifier.weight(1f)
                )
                WeatherMetricCard(
                    title = "Humidité",
                    value = "${weather.humidity}%",
                    icon = "💧",
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Prévisions horaires
        if (weather.hourlyForecast.isNotEmpty()) {
            item {
                Text(
                    text = "Prévisions horaires",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            items(weather.hourlyForecast.take(12)) { hourly ->
                HourlyForecastItem(hourlyWeather = hourly)
            }
        }
    }
}

/**
 * En-tête avec température principale et conditions
 */
@Composable
private fun WeatherHeader(weather: Weather) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = weather.cityName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = getWeatherEmoji(weather.weatherCondition),
                style = MaterialTheme.typography.displayLarge
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = "${weather.currentTemperature.roundToInt()}",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "°C",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            Text(
                text = "Ressenti ${weather.apparentTemperature.roundToInt()}°C",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = getWeatherDescription(weather.weatherCondition),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

/**
 * Vue d'erreur avec bouton de réessai
 */
@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⚠️",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Erreur",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Réessayer")
        }
    }
}

/**
 * Retourne l'emoji correspondant à la condition météo
 */
private fun getWeatherEmoji(condition: WeatherCondition): String {
    return when (condition) {
        WeatherCondition.SUNNY -> "☀️"
        WeatherCondition.CLOUDY -> "☁️"
        WeatherCondition.RAINY -> "🌧️"
        WeatherCondition.STORMY -> "⛈️"
        WeatherCondition.SNOWY -> "❄️"
        WeatherCondition.FOGGY -> "🌫️"
    }
}

/**
 * Retourne la description textuelle de la condition météo
 */
private fun getWeatherDescription(condition: WeatherCondition): String {
    return when (condition) {
        WeatherCondition.SUNNY -> "Ensoleillé"
        WeatherCondition.CLOUDY -> "Nuageux"
        WeatherCondition.RAINY -> "Pluvieux"
        WeatherCondition.STORMY -> "Orageux"
        WeatherCondition.SNOWY -> "Neigeux"
        WeatherCondition.FOGGY -> "Brumeux"
    }
}
