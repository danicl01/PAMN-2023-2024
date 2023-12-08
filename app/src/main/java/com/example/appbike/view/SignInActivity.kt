package com.example.appbike.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.appbike.MainActivity
import com.example.appbike.R
import com.example.appbike.model.AuthSignInModel
import com.example.appbike.model.AuthSignUpModel
import com.example.appbike.presenter.AuthPresenter
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    private lateinit var authPresenter: AuthPresenter
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        authPresenter = AuthPresenter()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val userEmailTextView = findViewById<TextView>(R.id.userEmailTextView)
        val userNameTextView = findViewById<TextView>(R.id.userNameTextView)
        val backButton = findViewById<Button>(R.id.backButton)
        val logoutButton = findViewById<Button>(R.id.logOutButton) // Asumiendo que tienes un botón en tu layout con el ID logoutButton

        // Obtener datos del usuario actual
        var currentUser = authPresenter.getCurrentUser()
        currentUser?.let {
            it.reload() // Recargar los datos del usuario
            it.getIdToken(true)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Los datos del usuario se han cargado correctamente
                        userEmailTextView.text = it.email
                        userNameTextView.text = "Usuario Registrado"
                    } else {
                        // Hubo un problema al obtener los datos del usuario
                        // Puedes manejar el error aquí
                    }
                }
        }

        backButton.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        logoutButton.setOnClickListener {
            // Cerrar sesión
            authPresenter.signOut()
            startActivity(Intent(this, MapActivity::class.java))
        }
    }
}

