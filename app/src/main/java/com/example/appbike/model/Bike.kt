package com.example.appbike.model

data class Bike(
    var id: String? = null,
    var name: String? = null,
    val state: String? = null,
    val latitude: Double = 0.0,
    val altitude: Double = 0.0
)
