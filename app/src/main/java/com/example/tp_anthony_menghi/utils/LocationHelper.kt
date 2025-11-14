package com.example.tp_anthony_menghi.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Helper pour la géolocalisation
 * Utilise FusedLocationProviderClient de Google Play Services
 */
@Singleton
class LocationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    
    /**
     * Vérifie si les permissions de localisation sont accordées
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Récupère la position actuelle de l'utilisateur
     * @return Result avec la Location ou une exception
     */
    suspend fun getCurrentLocation(): Result<Location> = suspendCancellableCoroutine { continuation ->
        if (!hasLocationPermission()) {
            continuation.resume(Result.failure(SecurityException("Permission de localisation refusée")))
            return@suspendCancellableCoroutine
        }
        
        val cancellationTokenSource = CancellationTokenSource()
        
        try {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(Result.success(location))
                } else {
                    continuation.resume(Result.failure(Exception("Position introuvable")))
                }
            }.addOnFailureListener { exception ->
                continuation.resume(Result.failure(exception))
            }
        } catch (e: SecurityException) {
            continuation.resume(Result.failure(e))
        }
        
        continuation.invokeOnCancellation {
            cancellationTokenSource.cancel()
        }
    }
    
    /**
     * Récupère la dernière position connue
     * Plus rapide mais peut être obsolète
     */
    suspend fun getLastKnownLocation(): Result<Location> = suspendCancellableCoroutine { continuation ->
        if (!hasLocationPermission()) {
            continuation.resume(Result.failure(SecurityException("Permission de localisation refusée")))
            return@suspendCancellableCoroutine
        }
        
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        continuation.resume(Result.success(location))
                    } else {
                        continuation.resume(Result.failure(Exception("Aucune position connue")))
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Result.failure(exception))
                }
        } catch (e: SecurityException) {
            continuation.resume(Result.failure(e))
        }
    }
}
