package com.example.appbike.model

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AuthSignUpModel {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()


    fun signUp(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Obtener el UID del usuario registrado
                    val uid = task.result?.user?.uid

                    if (uid != null) {
                        // Guardar información en FirebaseDatabase
                        val database: FirebaseDatabase =
                            FirebaseDatabase.getInstance("https://bicicletaapp-2324-default-rtdb.europe-west1.firebasedatabase.app")
                        val usuarioRef: DatabaseReference = database.reference.child("usuarios").child(uid)

                        // Crear un objeto User con la información relevante
                        val usuario = User(id = uid, name = "Nombre del Usuario", email = email)

                        // Guardar el usuario en FirebaseDatabase
                        usuarioRef.setValue(usuario).addOnCompleteListener { databaseTask ->
                            if (databaseTask.isSuccessful) {
                                // Llamada de retorno exitosa
                                callback(true, null)
                            } else {
                                // Llamada de retorno con error
                                callback(false, databaseTask.exception?.message)
                            }
                        }
                    }
                } else {
                    // Llamada de retorno con error
                    callback(false, task.exception?.message)
                }
            }
    }
}