package com.example.appbike.presenter

import com.example.appbike.model.PaymentModel
import com.example.appbike.view.PaymentView

class PaymentPresenter(private val model: PaymentModel, private var view: PaymentView?) {

    fun paySubscription() {
        // Simular el pago y cambiar el estado del usuario
        model.updateUserState("Usuario de Pago")
        view?.showPaymentSuccess()
    }

    fun cancelPayment() {
        // Vuelve al perfil sin cambiar el estado
        view?.navigateToProfile()
    }

    fun detachView() {
        view = null
    }
}