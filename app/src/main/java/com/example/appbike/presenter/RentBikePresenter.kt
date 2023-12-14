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

    override fun onRentButtonClick(isUserLoggedIn: Boolean, isBikeSelected: Boolean, marker:Marker) {
            val userId = userRepository.getCurrentUserId()
            if (userId != null) {
                Log.d("ho", userId.toString())
                userRepository.getUserPaymentState(userId) { isUserPaid ->
                    if (isUserPaid) {
                        if (isBikeSelected) {
                            val selectedBikeName =
                                marker.title // Obtener el nombre de la bicicleta desde el Marker
                            val newState = "Alquilada"
                            bikeRepository.getBikeState(selectedBikeName) { bikeState ->
                                when (bikeState) {
                                    "En espera" -> {
                                        // Actualizar el estado y el ID del usuario que utiliza la bicicleta
                                        bikeRepository.updateBikeState(
                                            selectedBikeName,
                                            newState
                                        ) { success ->
                                            if (success) {
                                                view.showBikeReservedMessage()
                                            } else {
                                                view.showBikeNotReservedMessage()
                                            }
                                        }
                                    }

                                    "Alquilada" -> {
                                        // La bicicleta no está disponible para alquilar
                                        view.showBikeNotAvailableMessage()
                                    }

                                    else -> {
                                        // Manejar otros estados si es necesario
                                        view.showBikeBrokenMessage()
                                    }
                                }
                            }
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
}
