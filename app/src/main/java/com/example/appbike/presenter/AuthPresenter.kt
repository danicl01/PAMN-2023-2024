package com.example.appbike.presenter

import com.example.appbike.model.AuthModel;

class AuthPresenter(private val authModel: AuthModel) {

    interface View {
        fun showSuccessMessage(message: String)
        fun showErrorMessage(message: String)
    }

    private var view: View? = null

    fun attachView(view: View) {
        this.view = view
    }

    fun detachView() {
        view = null
    }

    fun signUp(email: String, password: String) {
        authModel.signUp(email, password) { isSuccess, message ->
            if (isSuccess) {
                view?.showSuccessMessage("Registro exitoso")
            } else {
                view?.showErrorMessage(message ?: "Error desconocido en el registro")
            }
        }
    }

    fun signIn(email: String, password: String) {
        authModel.signIn(email, password) { isSuccess, message ->
            if (isSuccess) {
                view?.showSuccessMessage("Inicio de sesión exitoso")
            } else {
                view?.showErrorMessage(message ?: "Error desconocido en el inicio de sesión")
            }
        }
    }

    fun checkCurrentUser() {
        val currentUser = authModel.getCurrentUser()
        if (currentUser != null) {
            // El usuario está autenticado
            // Puedes realizar acciones adicionales si es necesario
        } else {
            // El usuario no está autenticado
            // Puedes redirigir a la pantalla de inicio de sesión, mostrar un mensaje, etc.
        }
    }

    fun signOut() {
        authModel.signOut()
        // Puedes realizar acciones adicionales después de cerrar sesión si es necesario
    }
}
