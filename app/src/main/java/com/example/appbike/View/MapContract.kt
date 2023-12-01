package com.example.appbike.View

import com.example.appbike.Model.Bicicleta

interface MapContract {
    interface View {
        fun displayBikes(bicicletas: List<Bicicleta>)
    }

    interface  Presenter {
    }

    interface  Model {
    }
}