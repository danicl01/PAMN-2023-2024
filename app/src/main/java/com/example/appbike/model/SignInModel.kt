package com.example.appbike.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignInModel {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance("https://bicicletaapp-2324-default-rtdb.europe-west1.firebasedatabase.app").reference

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun getUserDetails(uid: String, callback: (String, String) -> Unit) {
        val userRef = databaseRef.child("usuarios").child(uid)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userName = dataSnapshot.child("name").getValue(String::class.java) ?: ""
                val userEmail = dataSnapshot.child("email").getValue(String::class.java) ?: ""
                callback.invoke(userName, userEmail)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar errores de lectura desde la base de datos
                callback.invoke("", "")
            }
        })
    }

    fun getUserState(uid: String, callback: (String) -> Unit) {
        val userRef = databaseRef.child("usuarios").child(uid)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userState = dataSnapshot.child("state").getValue(String::class.java)
                callback.invoke(userState ?: "Usuario Gratis")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar errores de lectura desde la base de datos
                callback.invoke("Usuario Gratis")
            }
        })
    }

    fun updateUserName(userId: String, newName: String, callback: (Boolean, String?) -> Unit) {
        val userRef = databaseRef.child("usuarios").child(userId)
        val infoUsuario = HashMap<String, Any>()
        infoUsuario["name"] = newName

        userRef.updateChildren(infoUsuario)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun getUserPaymentState(userId: String, callback: (Boolean) -> Unit) {
        val userRef = databaseRef.child("usuarios").child(userId).child("state")

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
    fun updateUserState(userId: String, newState: String, callback: (Boolean) -> Unit) {
        val userRef = databaseRef.child("usuarios").child(userId).child("state")

        userRef.setValue(newState)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                // Manejar el fallo de la actualización si es necesario
                callback(false)
            }
    }


}
