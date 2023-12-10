package com.example.appbike.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class PaymentModel {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val databaseReference = FirebaseDatabase.getInstance("https://bicicletaapp-2324-default-rtdb.europe-west1.firebasedatabase.app").reference.child("usuarios")

    fun getCurrentUser() = firebaseAuth.currentUser

    fun updateUserState(userState: String) {
        val currentUser = getCurrentUser()
        currentUser?.let {
            // Actualizar el estado del usuario en Firebase Realtime Database
            val uid = currentUser.uid
            databaseReference.child(uid).child("state").setValue(userState)
        }
    }
}
