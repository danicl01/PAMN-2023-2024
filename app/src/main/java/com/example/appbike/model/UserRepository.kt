package com.example.appbike.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance("https://bicicletaapp-2324-default-rtdb.europe-west1.firebasedatabase.app").reference

    // Método para verificar si el usuario está autenticado
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // Método para obtener el ID del usuario actual
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    // Método para obtener el estado de pago del usuario desde la base de datos
    fun getUserPaymentState(userId: String, callback: (Boolean) -> Unit) {
        val userRef = database.child("usuarios").child(userId).child("state")

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val state = snapshot.getValue(String::class.java)
                val isUserPaid = state?.equals("Usuario de Pago", ignoreCase = true) == true
                callback(isUserPaid)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error de lectura de la base de datos si es necesario
                callback(false)
            }
        })
    }

}
