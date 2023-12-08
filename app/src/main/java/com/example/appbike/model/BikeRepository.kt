package com.example.appbike.model

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BikeRepository {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://bicicletaapp-2324-default-rtdb.europe-west1.firebasedatabase.app")
    private val bicicletasReference: DatabaseReference = database.reference.child("bicicletas")

    fun guardarBicicleta(bicicleta: Bicicleta, callback: (Boolean) -> Unit) {
        // Obtener un nuevo ID único para la bicicleta
        val bicicletaId = bicicletasReference.push().key

        // Asignar el ID único a la bicicleta
        bicicleta.id = bicicletaId

        // Crear un nuevo nodo para la bicicleta
        bicicletasReference.child(bicicletaId!!).setValue(bicicleta)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun obtenerBicicletas(callback: (List<Bicicleta>) -> Unit) {
        bicicletasReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bicicletasList = mutableListOf<Bicicleta>()
                for (childSnapshot in snapshot.children) {
                    val bicicleta = childSnapshot.getValue(Bicicleta::class.java)
                    bicicleta?.let { bicicletasList.add(it) }
                }
                callback(bicicletasList)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }
}

