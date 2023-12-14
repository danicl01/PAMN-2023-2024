package com.example.appbike.view

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.appbike.R
import com.example.appbike.model.SignInModel
import com.example.appbike.presenter.SignInPresenter

class SignInActivity : AppCompatActivity(), SignInView {

    private lateinit var signInPresenter: SignInPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        signInPresenter = SignInPresenter(SignInModel())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val backButton = findViewById<Button>(R.id.backButton)
        val logoutButton = findViewById<Button>(R.id.logOutButton)
        val paymentButton = findViewById<Button>(R.id.paymentButton)
        val editNameButton = findViewById<Button>(R.id.EditNamebutton)
        paymentButton.text = "Subscribirse"
        backButton.setOnClickListener {
            signInPresenter.navigateToMapActivity()
        }

        logoutButton.setOnClickListener {
            signInPresenter.signOut()
            finishActivity()
        }

        paymentButton.setOnClickListener {
            signInPresenter.onPaymentButtonClick()
        }
        editNameButton.setOnClickListener {
            showEditNamePopup()
        }

        signInPresenter.attachView(this)
        signInPresenter.getCurrentUser()
    }

    override fun showUserDetails(userName: String, email: String, userState: String) {
        val userEmailTextView = findViewById<TextView>(R.id.userEmailTextView)
        val userNameTextView = findViewById<TextView>(R.id.userNameTextView)
        val userStateTextView = findViewById<TextView>(R.id.textView7)

        userEmailTextView.text = email
        userNameTextView.text = userName
        userStateTextView.text = userState
    }

    override fun navigateToMapActivity() {
        val intent = Intent(this, MapActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
    }

    override fun navigateToPaymentActivity() {
        startActivity(Intent(this, PaymentActivity::class.java))
    }

    override fun finishActivity() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        signInPresenter.detachView()
    }

    override fun showSuccessMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    override fun updateUserName(newName: String) {
        val userNameTextView = findViewById<TextView>(R.id.userNameTextView)
        userNameTextView.text = newName
    }
    override fun showEditNamePopup() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar Nombre")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Guardar") { _, _ ->
            val newName = input.text.toString()
            signInPresenter.updateUserName(newName)
        }
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    override fun updatePaymentButtonText(text: String) {
        val paymentButton = findViewById<Button>(R.id.paymentButton)
        paymentButton.text = text
        if (text == "Cancelar Subscripción") {
            paymentButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorCancelSubscriptionBackground))
        } else {
            // Restaura el color original en caso contrario
            paymentButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorOriginalBackground))
        }
    }
    override fun onResume() {
        super.onResume()
        signInPresenter.updatePaymentButtonText()
    }

}


