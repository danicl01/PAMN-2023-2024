package com.example.appbike.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AuthModel {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signUp(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        // UID del usuario registrado
                        val uid = user.uid

                        // Crear referencia a la base de datos
                        val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://bicicletaapp-2324-default-rtdb.europe-west1.firebasedatabase.app")
                        val usuarioRef: DatabaseReference = database.reference.child("usuarios")

                        val nombre = "Nombre del Usuario"
                        val infoUsuario = HashMap<String, Any>()
                        infoUsuario["nombre"] = nombre
                        infoUsuario["correo"] = email

                        usuarioRef.setValue(infoUsuario)
                    }
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    fun signIn(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}
