package com.example.appbike.view

interface SignInView {

    fun showUserDetails(name: String, email: String, userState: String)

    fun navigateToMapActivity()

    fun navigateToPaymentActivity()

    fun finishActivity()

}