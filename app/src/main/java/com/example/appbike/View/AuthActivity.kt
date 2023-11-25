package com.example.appbike.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.appbike.MainActivity
import com.example.appbike.Presenter.AuthPresenter
import com.example.appbike.Model.AuthModel
import com.example.appbike.Model.Bicicleta
import com.example.appbike.Model.BikeRepository

import com.example.appbike.R

class AuthActivity : AppCompatActivity(), AuthPresenter.View {

    private lateinit var authPresenter: AuthPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)



        // Inicializar AuthPresenter
        val authModel = AuthModel()
        authPresenter = AuthPresenter(authModel)
        authPresenter.attachView(this)

        // Setup
        setup()
    }

    private fun setup() {
        title = "Autenticación"

        val signUpButton = findViewById<Button>(R.id.signUpbutton)
        val logInButton = findViewById<Button>(R.id.logInbutton)
        val emailText = findViewById<EditText>(R.id.emailEditText)
        val passwordText = findViewById<EditText>(R.id.passwordEditText)
        val bikeRepository = BikeRepository()

        signUpButton.setOnClickListener {
            val email = emailText.text.toString()
            val password = passwordText.text.toString()
            authPresenter.signUp(email, password)
            val bikeRepository = BikeRepository()

// Guardar una bicicleta
            val nuevaBicicleta = Bicicleta(estado = "En espera", latitud = 40.7128, altitud = -74.0060)
            bikeRepository.guardarBicicleta(nuevaBicicleta) { exito ->
                if (exito) {
                    Log.d("TAG", "Bicicleta guardada con éxito")
                } else {
                    Log.e("TAG", "Error al guardar la bicicleta")
                }
            }

// Obtener todas las bicicletas
            bikeRepository.obtenerBicicletas { bicicletas ->
                for (bicicleta in bicicletas) {
                    Log.d("TAG", "ID: ${bicicleta.id}, Estado: ${bicicleta.estado}, Latitud: ${bicicleta.latitud}, Altitud: ${bicicleta.altitud}")
                }
            }
        }

        logInButton.setOnClickListener {
            val email = emailText.text.toString()
            val password = passwordText.text.toString()
            authPresenter.signIn(email, password)
        }
    }

    override fun showSuccessMessage(message: String) {
    }

    override fun showErrorMessage(message: String) {
        // Manejar error
    }

    override fun onDestroy() {
        super.onDestroy()
        authPresenter.detachView()
    }
}
