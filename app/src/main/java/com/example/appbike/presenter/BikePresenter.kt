package com.example.appbike.presenter

import com.example.appbike.model.BikeRepository
import com.example.appbike.view.MapContract

class BikePresenter(private val bikeRepository: BikeRepository, private val mapView: MapContract.View) : BikeLoader {

    override fun loadBikes() {
        bikeRepository.obtenerBicicletas { bicicletas ->
            mapView.displayBikes(bicicletas)
        }
    }
}