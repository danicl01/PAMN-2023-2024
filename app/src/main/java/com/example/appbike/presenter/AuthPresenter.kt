package com.example.appbike.presenter

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.example.appbike.model.AuthSignInModel
import com.example.appbike.model.AuthSignUpModel
import com.example.appbike.view.MapActivity
import com.example.appbike.view.SignInActivity
import com.google.firebase.auth.FirebaseUser

class AuthPresenter() {

    private val AuthSignInModel = AuthSignInModel()
    private val AuthSignUpModel = AuthSignUpModel()



    fun signUp(email: String, password: String) {
        AuthSignUpModel.signUp(email, password) { isSuccess, message ->
            if (isSuccess) {

            } else {

            }
        }
    }

    fun signIn(email: String, password: String, context: Context) {
        AuthSignInModel.signIn(email, password) { isSuccess, message ->
            if (isSuccess) {
                val intent = Intent(context, SignInActivity::class.java)
                context.startActivity(intent)
            } else {

            }
        }
    }



    fun checkCurrentUser() {
        val currentUser = AuthSignInModel.getCurrentUser()
        if (currentUser != null) {
            // El usuario está autenticado
            // Puedes realizar acciones adicionales si es necesario
        } else {
            // El usuario no está autenticado
            // Puedes redirigir a la pantalla de inicio de sesión, mostrar un mensaje, etc.
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return AuthSignInModel.getCurrentUser()
    }

    fun signOut(context: Context) {
        AuthSignInModel.signOut()
        val intent = Intent(context, MapActivity::class.java)
        context.startActivity(intent)
        // Puedes realizar acciones adicionales después de cerrar sesión si es necesario
    }
}
