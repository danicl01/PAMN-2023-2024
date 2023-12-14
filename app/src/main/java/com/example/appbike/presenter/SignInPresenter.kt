package com.example.appbike.presenter

import android.util.Log
import com.example.appbike.model.SignInModel
import com.example.appbike.view.SignInView

class SignInPresenter(private val signInModel: SignInModel) {

    private var signInView: SignInView? = null

    fun attachView(view: SignInView) {
        signInView = view
    }

    fun detachView() {
        signInView = null
    }

    fun updateUserName(newName: String) {
        val currentUser = signInModel.getCurrentUser()
        if (currentUser != null) {
            signInModel.updateUserName(currentUser.uid, newName) { isSuccess, message ->
                if (isSuccess) {
                    signInView?.showSuccessMessage("Nombre actualizado exitosamente")
                    onUserNameUpdated(newName)
                } else {
                    signInView?.showErrorMessage("Error desconocido al actualizar el nombre")
                }
            }
        } else {
            signInView?.showErrorMessage("Usuario no autenticado")
        }
    }
    fun onUserNameUpdated(newName: String) {
        signInView?.updateUserName(newName)
    }
    fun getCurrentUser() {
        val currentUser = signInModel.getCurrentUser()
        currentUser?.let {
            signInView?.showUserDetails(it.displayName ?: "", it.email ?: "", "Cargando estado...")
            val uid = it.uid
            signInModel.getUserDetails(uid) { userName, userEmail ->
                signInView?.showUserDetails(userName, userEmail, "Cargando estado...")
                signInModel.getUserState(uid) { userState ->
                    signInView?.showUserDetails(userName, userEmail, userState)
                }
            }
        }
    }
    fun onPaymentButtonClick() {
        val currentUser = signInModel.getCurrentUser()
        if (currentUser != null) {
            val userId = currentUser.uid
            signInModel.getUserPaymentState(userId) { isUserPaid ->
                if (isUserPaid) {
                    // Usuario de pago, cambiar a usuario gratis
                    signInModel.updateUserState(userId,"Usuario Gratis") { isSuccess ->
                        if (isSuccess) {
                            signInView?.showSuccessMessage("Cancelada la subscripción exitosamente")
                            updatePaymentButtonText()
                        } else {
                            signInView?.showErrorMessage("Error desconocido al cancelar la subscripción")
                        }
                    }
                } else {
                    // Usuario gratis, llevar a PaymentActivity
                    signInView?.navigateToPaymentActivity()
                }
            }
        } else {
            signInView?.showErrorMessage("Usuario no autenticado")
        }
    }
    fun updatePaymentButtonText() {
        val currentUser = signInModel.getCurrentUser()
        if (currentUser != null) {
            val userId = currentUser.uid
            signInModel.getUserPaymentState(userId) { isUserPaid ->
                signInView?.updatePaymentButtonText(if (isUserPaid) "Cancelar Subscripción" else "Subscribirse")
                Log.d("UpdateButtonText", "Text updated: ${if (isUserPaid) "Cancelar Subscripción" else "Subscribirse"}")
            }
        } else {
            signInView?.showErrorMessage("Usuario no autenticado")
        }
    }

    fun signOut() {
        signInModel.signOut()
        signInView?.finishActivity()
    }

    fun navigateToMapActivity() {
        signInView?.navigateToMapActivity()
    }


}
