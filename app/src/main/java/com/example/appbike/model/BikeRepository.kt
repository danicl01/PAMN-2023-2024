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
        val bikeId = bikeReference.push().key
        bike.id = bikeId
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

    fun updateBikeState(bikeName: String, newState: String, callback: (Boolean) -> Unit) {
        val bikeRef = bikeReference.orderByChild("name")

        bikeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var bikeId: String? = null

                for (child in snapshot.children) {
                    val name = child.child("name").getValue(String::class.java)
                    if (name == bikeName) {
                        bikeId = child.key
                        break
                    }
                }

                if (bikeId != null) {
                    val stateRef = bikeReference.child(bikeId).child("state")

                    stateRef.setValue(newState)
                        .addOnSuccessListener {
                            callback(true)
                        }
                        .addOnFailureListener {
                            // Manejar el fallo
                            callback(false)
                        }
                } else {
                    // Manejar el caso en que no se encuentre la bicicleta
                    callback(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error de lectura de la base de datos
                callback(false)
            }
        })
    }




}

