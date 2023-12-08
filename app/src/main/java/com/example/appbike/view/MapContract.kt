package com.example.appbike.view

import com.example.appbike.model.Bicicleta

interface MapContract {
    interface View {
        fun displayBikes(bicicletas: List<Bicicleta>)
    }
}