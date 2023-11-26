package com.example.appbike.Presenter

import com.example.appbike.Model.BikeRepository
import com.example.appbike.View.MapContract

class BikePresenter(private val bikeRepository: BikeRepository, private val mapView: MapContract.View) : BikeLoader {

    override fun loadBikes() {
        bikeRepository.obtenerBicicletas { bicicletas ->
            mapView.displayBikes(bicicletas)
        }
    }
}