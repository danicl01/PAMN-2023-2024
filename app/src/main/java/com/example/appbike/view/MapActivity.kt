package com.example.appbike.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.appbike.model.Bike
import com.example.appbike.model.BikeRepository
import com.example.appbike.presenter.BikeLoader
import com.example.appbike.presenter.BikePresenter
import com.example.appbike.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth

class MapActivity : AppCompatActivity(), OnMapReadyCallback, MapContract.View, GoogleMap.OnMarkerClickListener {


    private lateinit var map: GoogleMap
    private lateinit var bikeLoader: BikeLoader
    private var latitudActual: Double = 0.0
    private var longitudActual: Double = 0.0

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createFragment()

        val goToAuthButton = findViewById<ImageButton>(R.id.goToAuthButton)

        //Presenter initialize
        val bikeRepository = BikeRepository()
        bikeLoader = BikePresenter(bikeRepository, this)

        goToAuthButton.setOnClickListener {
            // Verificar si hay un usuario logeado
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser != null) {
                // Ya hay un usuario logeado, puedes redirigir a la pantalla de perfil u otra actividad
                Log.d("MapActivity", "Usuario logeado. Redirigiendo a la pantalla de perfil.")
                // Ejemplo de redirección a la pantalla de perfil
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
            } else {
                // No hay usuario logeado, inicia la actividad de autenticación
                Log.d("MapActivity", "Ningún usuario logeado. Iniciando AuthActivity.")
                val intent = Intent(this, AuthSignUpActivity::class.java)
                startActivity(intent)
            }
        }
        //load bikes when start activity
        bikeLoader.loadBikes()
    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val initialLocation = LatLng(37.7749, -122.4194) // Coordenadas para San Francisco
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 15.0f))
        enableLocation()
        map.setOnMarkerClickListener(this)
    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private fun enableLocation() {
        if (!::map.isInitialized) return
        if (isLocationPermissionGranted()) {
            map.isMyLocationEnabled = true
            // Current Location
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        latitudActual = it.latitude
                        longitudActual = it.longitude
                    }
                }
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(this, "Go to settings and accept the permissions", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        }
    }

    @SuppressLint("MissingSuperCall", "MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(
                    this,
                    "To activate the location, go to settings and accept the permissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
            }
        }
    }

    override fun displayBikes(bikes: List<Bike>) {
        for (bike in bikes) {
            val bikeLocation = LatLng(bike.latitude, bike.altitude)
            map.addMarker(MarkerOptions().position(bikeLocation).title(bike.name))
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        // Bike Info
        val bikeId = marker.tag as? String
        val distanceKm = calculateDistanceFromCurrentToBike(marker.position)

        // Dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Información de la Bicicleta")
        builder.setMessage("ID: $bikeId\nDistancia: ${String.format("%.2f", distanceKm)} km")

        val checkBoxReservar = CheckBox(this)
        checkBoxReservar.text = "Reservar"
        builder.setView(checkBoxReservar)

        builder.setPositiveButton("Alquilar") { dialog, which ->
            if (checkBoxReservar.isChecked) {
                Toast.makeText(this, "Bicicleta reservada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Bicicleta alquilada", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
        return true
    }

    private fun calculateDistanceFromCurrentToBike(bikePosition: LatLng): Double {
        val result = FloatArray(10)
        Location.distanceBetween(latitudActual, longitudActual, bikePosition.latitude, bikePosition.longitude, result)
        return result[0].toDouble() / 1000
    }
}