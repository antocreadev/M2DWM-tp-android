package com.example.tp_anthony_menghi.ui.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tp_anthony_menghi.domain.model.FavoriteCity
import com.example.tp_anthony_menghi.domain.model.WeatherCondition
import kotlin.math.roundToInt

/**
 * Carte affichant une ville favorite avec son résumé météo
 */
@Composable
fun FavoriteCard(
    favorite: FavoriteCity,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Informations de la ville
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = favorite.city.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = favorite.city.country,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Météo
            if (favorite.weather != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.Top) {
                            Text(
                                text = "${favorite.weather.currentTemperature.roundToInt()}",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "°",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        Text(
                            text = getWeatherEmoji(favorite.weather.weatherCondition),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    
                    // Bouton supprimer
                    IconButton(onClick = onRemove) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            } else {
                // Pas de données météo
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                    IconButton(onClick = onRemove) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
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
