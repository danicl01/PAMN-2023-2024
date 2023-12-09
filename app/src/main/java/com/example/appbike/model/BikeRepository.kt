package com.example.appbike.model

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BikeRepository {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://bicicletaapp-2324-default-rtdb.europe-west1.firebasedatabase.app")
    private val bikeReference: DatabaseReference = database.reference.child("bicicletas")

    fun saveBikes(bike: Bike, callback: (Boolean) -> Unit) {
        // Obtener un nuevo ID único para la bicicleta
        val bikeId = bikeReference.push().key

        // Asignar el ID único a la bicicleta
        bike.id = bikeId

        // Crear un nuevo nodo para la bicicleta
        bikeReference.child(bikeId!!).setValue(bike)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun obtainBike(callback: (List<Bike>) -> Unit) {
        bikeReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bikeList = mutableListOf<Bike>()
                for (childSnapshot in snapshot.children) {
                    val bike = childSnapshot.getValue(Bike::class.java)
                    bike?.let { bikeList.add(it) }
                }
                callback(bikeList)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }
}

