package com.example.appbike.view

import com.example.appbike.model.Bike
import com.google.android.gms.maps.model.Marker

interface MapContract {
    interface View {
        fun displayBikes(bikes: List<Bike>)
        fun showBikeReservedMessage()
        fun showMustSelectBikeMessage()
        fun showUserNotPaidMessage()
        fun navigateToLoginScreen()
        fun showBikeNotReservedMessage()
        fun showBikeNotAvailableMessage()
        fun showBikeBrokenMessage()
    }
    interface Presenter {
        fun onRentButtonClick(isUserLoggedIn: Boolean, isBikeSelected: Boolean, marker: Marker)
    }
}