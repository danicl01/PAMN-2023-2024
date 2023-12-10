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
import com.example.appbike.model.Bike
import com.example.appbike.model.BikeRepository
import com.example.appbike.model.SignInModel
import com.example.appbike.presenter.AuthPresenter
import com.example.appbike.presenter.SignInPresenter
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity(), SignInView {

    private lateinit var signInPresenter: SignInPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        signInPresenter = SignInPresenter(SignInModel())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val backButton = findViewById<Button>(R.id.backButton)
        val logoutButton = findViewById<Button>(R.id.logOutButton)
        val paymentButton = findViewById<Button>(R.id.paymentButton)

        backButton.setOnClickListener {
            signInPresenter.navigateToMapActivity()
        }

        logoutButton.setOnClickListener {
            signInPresenter.signOut()
        }

        paymentButton.setOnClickListener {
            signInPresenter.navigateToPaymentActivity()
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
        startActivity(Intent(this, MapActivity::class.java))
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
}


