package com.example.tp_anthony_menghi.ui.detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tp_anthony_menghi.domain.model.HourlyWeather
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * Item de prévision horaire
 */
@Composable
fun HourlyForecastItem(
    hourlyWeather: HourlyWeather,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Heure
            Text(
                text = formatTime(hourlyWeather.time),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            
            // Température
            Text(
                text = "${hourlyWeather.temperature.roundToInt()}°",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            
            // Vent
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "💨",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${hourlyWeather.windSpeed.roundToInt()} km/h",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Précipitations
            if (hourlyWeather.precipitation > 0) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "🌧️",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${hourlyWeather.precipitation.roundToInt()} mm",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Formate l'heure à partir d'une chaîne ISO
 */
private fun formatTime(isoTime: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = parser.parse(isoTime)
        date?.let { formatter.format(it) } ?: isoTime
    } catch (e: Exception) {
        isoTime
    }
}
