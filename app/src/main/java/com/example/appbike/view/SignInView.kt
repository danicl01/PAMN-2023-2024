package com.example.appbike.view

interface SignInView {

    fun showUserDetails(name: String, email: String, userState: String)

    fun navigateToMapActivity()

    fun navigateToPaymentActivity()

    fun finishActivity()

    fun showSuccessMessage(message:String)

    fun showErrorMessage(message:String)

    fun showEditNamePopup()

    fun updateUserName(newName: String)
}