package com.example.appbike.presenter

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
        if (isUserLoggedIn) {
            val userId = userRepository.getCurrentUserId()

            if (userId != null) {
                userRepository.getUserPaymentState(userId) { isUserPaid ->
                    if (isUserPaid) {
                        if (isBikeSelected) {
                            val selectedBikeName = marker.title // Obtener el nombre de la bicicleta desde el Marker
                            val newState = "Alquilada"  // Cambiar el estado a "Alquilada"

                            // Actualizar el estado de la bicicleta en la base de datos
                            bikeRepository.updateBikeState(selectedBikeName, newState) { success ->
                                if (success) {
                                    view.showBikeReservedMessage()
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
                // Manejar el caso en que no se pueda obtener el ID del usuario actual
                view.showUserNotPaidMessage()
            }
        } else {
            view.navigateToLoginScreen()
        }
    }
}
