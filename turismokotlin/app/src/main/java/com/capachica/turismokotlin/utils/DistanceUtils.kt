package com.capachica.turismokotlin.utils

import kotlin.math.*

/**
 * Utilidades para cálculos de distancia y ubicación
 */
object DistanceUtils {
    
    private const val EARTH_RADIUS_KM = 6371.0
    
    /**
     * Calcula la distancia entre dos puntos geográficos usando la fórmula de Haversine
     * @param lat1 Latitud del primer punto
     * @param lon1 Longitud del primer punto
     * @param lat2 Latitud del segundo punto
     * @param lon2 Longitud del segundo punto
     * @return Distancia en kilómetros
     */
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        return EARTH_RADIUS_KM * c
    }
    
    /**
     * Formatea la distancia para mostrar al usuario
     * @param distanceKm Distancia en kilómetros
     * @return Texto formateado de la distancia
     */
    fun formatDistance(distanceKm: Double): String {
        return when {
            distanceKm < 1.0 -> "${(distanceKm * 1000).roundToInt()} m"
            distanceKm < 10.0 -> "${String.format("%.1f", distanceKm)} km"
            else -> "${distanceKm.roundToInt()} km"
        }
    }
    
    /**
     * Verifica si una coordenada está dentro de un área circular
     * @param centerLat Latitud del centro
     * @param centerLon Longitud del centro
     * @param pointLat Latitud del punto a verificar
     * @param pointLon Longitud del punto a verificar
     * @param radiusKm Radio del área en kilómetros
     * @return true si el punto está dentro del área
     */
    fun isWithinRadius(
        centerLat: Double, 
        centerLon: Double, 
        pointLat: Double, 
        pointLon: Double, 
        radiusKm: Double
    ): Boolean {
        val distance = calculateDistance(centerLat, centerLon, pointLat, pointLon)
        return distance <= radiusKm
    }
    
    /**
     * Valida si las coordenadas son válidas
     * @param latitude Latitud a validar
     * @param longitude Longitud a validar
     * @return true si las coordenadas son válidas
     */
    fun isValidCoordinate(latitude: Double, longitude: Double): Boolean {
        return latitude >= -90.0 && latitude <= 90.0 && 
               longitude >= -180.0 && longitude <= 180.0
    }
    
    /**
     * Obtiene el bearing (dirección) entre dos puntos
     * @param lat1 Latitud del primer punto
     * @param lon1 Longitud del primer punto
     * @param lat2 Latitud del segundo punto
     * @param lon2 Longitud del segundo punto
     * @return Bearing en grados (0-360)
     */
    fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLon = Math.toRadians(lon2 - lon1)
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)
        
        val y = sin(dLon) * cos(lat2Rad)
        val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(dLon)
        
        val bearing = Math.toDegrees(atan2(y, x))
        return (bearing + 360) % 360
    }
    
    /**
     * Convierte bearing a dirección cardinal
     * @param bearing Bearing en grados
     * @return Dirección cardinal (N, NE, E, SE, S, SW, W, NW)
     */
    fun bearingToCardinal(bearing: Double): String {
        val directions = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
        val index = ((bearing + 22.5) / 45.0).toInt() % 8
        return directions[index]
    }
    
    /**
     * Genera un texto descriptivo de la ubicación relativa
     * @param fromLat Latitud origen
     * @param fromLon Longitud origen
     * @param toLat Latitud destino
     * @param toLon Longitud destino
     * @return Texto descriptivo como "2.5 km al norte"
     */
    fun getRelativeLocation(fromLat: Double, fromLon: Double, toLat: Double, toLon: Double): String {
        val distance = calculateDistance(fromLat, fromLon, toLat, toLon)
        val bearing = calculateBearing(fromLat, fromLon, toLat, toLon)
        val cardinal = bearingToCardinal(bearing)
        
        return "${formatDistance(distance)} al ${cardinal.lowercase()}"
    }
}