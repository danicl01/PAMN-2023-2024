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

class AuthSignUpActivity : AppCompatActivity() {

    private lateinit var authPresenter: AuthPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        authPresenter = AuthPresenter()

        // Setup
        setup()
    }

    private fun setup() {
        title = "Autenticación"

        val signUpButton = findViewById<Button>(R.id.signUpbutton2)
        val logInButton = findViewById<Button>(R.id.logInbutton)
        val logInButton3 = findViewById<Button>(R.id.logInbutton3)
        val emailText = findViewById<EditText>(R.id.emailEditText)
        val passwordText = findViewById<EditText>(R.id.passwordEditText)
        val confirmPasswordText = findViewById<EditText>(R.id.passwordEditText2)

        signUpButton.setOnClickListener {
            val email = emailText.text.toString()
            val password = passwordText.text.toString()
            val confirmPassword = confirmPasswordText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password.length >= 6) {
                    if (password == confirmPassword) {
                        // Contraseñas coinciden, proceder con el registro
                        authPresenter.signUp(email, password)
                        startActivity(Intent(this, AuthLogInActivity::class.java))
                    } else {
                        // Mostrar diálogo de contraseñas no coinciden
                        showPasswordMismatchDialog()
                    }
                } else {
                    // Mostrar diálogo de contraseña corta
                    showShortPasswordDialog()
                }
            } else {
                // Mostrar diálogo de campos vacíos
                showEmptyFieldsWarning()
            }
        }

        logInButton.setOnClickListener {
            startActivity(Intent(this, AuthLogInActivity::class.java))
            finish()

        }

        logInButton3.setOnClickListener {
            startActivity(Intent(this, AuthLogInActivity::class.java))
            finish()
        }
    }


    private fun showPasswordMismatchDialog() {
        AlertDialog.Builder(this)
            .setTitle("Contraseñas no coinciden")
            .setMessage("Las contraseñas no coinciden. Por favor, inténtalo de nuevo.")
            .setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
            .show()
    }

    private fun showShortPasswordDialog() {
        AlertDialog.Builder(this)
            .setTitle("Contraseña corta")
            .setMessage("La contraseña debe tener al menos 6 caracteres. Por favor, inténtalo de nuevo.")
            .setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
            .show()
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
