package com.example.appbike

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import com.example.appbike.Modelo.Bicicleta
import com.example.appbike.Modelo.BikeRepository
import com.google.firebase.analytics.FirebaseAnalytics

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
    /* Analytics Event
    val analytics = FirebaseAnalytics.getInstance(this)
    val bundle = Bundle()
    bundle.putString("message", "Integracion de Firebase completa")
    analytics.logEvent("InitScreen", bundle)*/
}