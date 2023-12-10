package com.example.appbike.presenter

import com.example.appbike.model.SignInModel
import com.example.appbike.view.SignInView

class SignInPresenter(private val signInModel: SignInModel) {

    private var signInView: SignInView? = null

    fun attachView(view: SignInView) {
        signInView = view
    }

    fun detachView() {
        signInView = null
    }

    fun getCurrentUser() {
        val currentUser = signInModel.getCurrentUser()
        currentUser?.let {
            signInView?.showUserDetails(it.displayName ?: "", it.email ?: "", "Cargando estado...")
            val uid = it.uid
            signInModel.getUserDetails(uid) { userName, userEmail ->
                signInView?.showUserDetails(userName, userEmail, "Cargando estado...")
                signInModel.getUserState(uid) { userState ->
                    signInView?.showUserDetails(userName, userEmail, userState)
                }
            }
        }
    }

    fun signOut() {
        signInModel.signOut()
        signInView?.finishActivity()
    }

    fun navigateToMapActivity() {
        signInView?.navigateToMapActivity()
    }

    fun navigateToPaymentActivity() {
        signInView?.navigateToPaymentActivity()
    }

}
