package com.example.appbike.presenter

import android.util.Log
import com.example.appbike.model.BikeRepository
import com.example.appbike.model.UserRepository
import com.example.appbike.view.MapContract
import com.google.android.gms.maps.model.Marker

class RentBikePresenter(
    private val view: MapContract.View,
    private val userRepository: UserRepository,
    private val bikeRepository: BikeRepository
) : MapContract.Presenter {

    override fun onRentButtonClick(isUserLoggedIn: Boolean, isBikeSelected: Boolean, marker: Marker) {
        val userId = userRepository.getCurrentUserId()

        if (userId != null) {
            Log.d("ho", userId.toString())
            userRepository.getUserPaymentState(userId) { isUserPaid ->
                if (isUserPaid) {
                    if (isBikeSelected) {
                        handleBikeSelection(marker)
                    } else {
                        view.showMustSelectBikeMessage()
                    }
                } else {
                    view.showUserNotPaidMessage()
                }
            }
        } else {
            view.navigateToLoginScreen()
        }
    }

    private fun handleBikeSelection(marker: Marker) {
        val selectedBikeName = marker.title

        bikeRepository.getBikeState(selectedBikeName) { bikeState ->
            when (bikeState) {
                "En espera" -> handleFreeBikeState(selectedBikeName)
                "Alquilada" -> view.showBikeNotAvailableMessage()
                else -> view.showBikeBrokenMessage()
            }
        }
    }

    private fun handleFreeBikeState(selectedBikeName: String) {
        val newState = "Alquilada"
        bikeRepository.updateBikeState(selectedBikeName, newState) { success ->
            if (success) {
                view.showBikeReservedMessage()
            } else {
                view.showBikeNotReservedMessage()
            }
        }
    }
}
