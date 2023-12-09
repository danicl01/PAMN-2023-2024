package com.example.appbike.view

import com.example.appbike.model.Bike

interface MapContract {
    interface View {
        fun displayBikes(bikes: List<Bike>)
    }
}