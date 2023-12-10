package com.example.appbike.view

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.appbike.presenter.AuthPresenter

import com.example.appbike.R
import com.example.appbike.model.AuthSignInModel
import com.example.appbike.model.AuthSignUpModel

class AuthLogInActivity : AppCompatActivity() {

    private lateinit var authPresenter: AuthPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in_auth)


        // Inicializar AuthPresenter
        authPresenter = AuthPresenter()

        // Setup
        setup()
    }
    private fun setup() {
        title = "Autenticación"

        val signUpButton = findViewById<Button>(R.id.signUpbutton)
        val signUpButton2 = findViewById<Button>(R.id.signUpbutton4)
        val logInButton = findViewById<Button>(R.id.logInbutton2)
        val emailText = findViewById<EditText>(R.id.emailEditText)
        val passwordText = findViewById<EditText>(R.id.passwordEditText)

        signUpButton.setOnClickListener {
            startActivity(Intent(this, AuthSignUpActivity::class.java))
            finish()
        }
        signUpButton2.setOnClickListener {
            startActivity(Intent(this, AuthSignUpActivity::class.java))
            finish()
        }

        logInButton.setOnClickListener {
            val email = emailText.text.toString()
            val password = passwordText.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                authPresenter.signIn(email, password,this)
            } else {
                showEmptyFieldsWarning()
            }

        }
    }


    private fun showEmptyFieldsWarning() {
        AlertDialog.Builder(this)
            .setTitle("Campos Vacíos")
            .setMessage("Por favor, introduce tu correo electrónico y contraseña.")
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                // Cerrar el diálogo si se hace clic en OK
                dialog.dismiss()
            })
            .show()
    }


}
