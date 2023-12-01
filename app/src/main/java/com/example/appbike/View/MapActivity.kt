package com.example.appbike.View

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.appbike.Model.Bicicleta
import com.example.appbike.Model.BikeRepository
import com.example.appbike.Presenter.BikeLoader
import com.example.appbike.Presenter.BikePresenter
import com.example.appbike.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback, MapContract.View {

    private lateinit var map: GoogleMap
    private lateinit var bikeLoader: BikeLoader

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createFragment()

        val goToAuthButton = findViewById<Button>(R.id.goToAuthButton)

        //Presenter initialize
        val bikeRepository = BikeRepository()
        bikeLoader = BikePresenter(bikeRepository, this)

        goToAuthButton.setOnClickListener {
            Log.d("MapActivity", "Botón presionado. Iniciando AuthActivity.")
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
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
        val initialLocation = LatLng(37.7749, -122.4194) // Coordenadas para San Francisco (puedes cambiarlas)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 15.0f))

        enableLocation()
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

    override fun displayBikes(bicicletas: List<Bicicleta>) {
        for (bicicleta in bicicletas) {
            val bikeLocation = LatLng(bicicleta.latitud, bicicleta.altitud)
            map.addMarker(MarkerOptions().position(bikeLocation).title("Bicicleta"))
        }
    }


}