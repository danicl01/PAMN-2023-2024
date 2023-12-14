package com.example.appbike.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.appbike.model.Bike
import com.example.appbike.model.BikeRepository
import com.example.appbike.presenter.BikeLoader
import com.example.appbike.presenter.BikePresenter
import com.example.appbike.R
import com.example.appbike.model.UserRepository
import com.example.appbike.presenter.RentBikePresenter
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth

class MapActivity : AppCompatActivity(), OnMapReadyCallback, MapContract.View, GoogleMap.OnMarkerClickListener {


    private lateinit var map: GoogleMap
    private lateinit var bikeLoader: BikeLoader
    private lateinit var presenter: RentBikePresenter
    private lateinit var userRepository: UserRepository

    private var latitudActual: Double = 0.0
    private var longitudActual: Double = 0.0

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(1500)
        setTheme(R.style.SplashTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createFragment()

        val goToAuthButton = findViewById<ImageButton>(R.id.goToAuthButton)
        val mapOptionButton:ImageButton = findViewById(R.id.mapOptionsMenu)
        val popupMenu = PopupMenu(this, mapOptionButton)
        popupMenu.menuInflater.inflate(R.menu.map_options, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            changeMapType(menuItem.itemId)
            true
        }

        mapOptionButton.setOnClickListener {
            popupMenu.show()
        }

        //Presenter initialize
        userRepository = UserRepository()

        val bikeRepository = BikeRepository()
        presenter = RentBikePresenter(this, userRepository,bikeRepository )
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
        val initialLocation = LatLng(28.09973, -15.41343) // Coordenadas para LPGC
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 12.0f))
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

    private fun changeMapType(ItemId: Int) {
        when(ItemId) {
            R.id.normal_map -> map.mapType = GoogleMap.MAP_TYPE_NORMAL
            R.id.hybrid_map -> map.mapType = GoogleMap.MAP_TYPE_HYBRID
            R.id.satellite_map -> map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            R.id.terrain_map -> map.mapType = GoogleMap.MAP_TYPE_TERRAIN
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
            val markerColor = when(bike.state) {
                "En espera" ->  BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                "Alquilada" ->  BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                "Averiada" ->  BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                else -> {
                    null
                }
            }
            if (markerColor != null) {
                map.addMarker(MarkerOptions().position(bikeLocation).title(bike.name).icon(markerColor))
            }

        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        // Bike Info
        //val bikeId = marker.tag as? String
        val distanceKm = calculateDistanceFromCurrentToBike(marker.position)

        // Dialog
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_bike_info, null)
        builder.setView(dialogView)

        // Obtén referencias a los elementos del diseño personalizado
        val titleTextView = dialogView.findViewById<TextView>(R.id.textViewDialogTitle)
        val bikeIconImageView = dialogView.findViewById<ImageView>(R.id.imageViewBikeIcon)
        val bikeInfoTextView = dialogView.findViewById<TextView>(R.id.textViewBikeInfo)
        val checkBoxReservar = dialogView.findViewById<CheckBox>(R.id.checkBoxReservar)

        // Establece la información de la bicicleta en los elementos del diseño personalizado
        titleTextView.text = "Información de la Bicicleta"
        bikeIconImageView.setImageResource(R.drawable.ic_bike_24) // Reemplaza con el recurso correcto
        bikeInfoTextView.text = "${marker.title} \nA: ${String.format("%.2f", distanceKm)} km"


        builder.setPositiveButton("Alquilar") { dialog, which ->
            val isUserLoggedIn = userRepository.isUserLoggedIn()
            val isBikeSelected = checkBoxReservar.isChecked
            presenter.onRentButtonClick(isUserLoggedIn, isBikeSelected,marker)
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

    override fun showBikeReservedMessage() {
        Toast.makeText(this, "Bicicleta reservada", Toast.LENGTH_SHORT).show()
    }

    override fun showMustSelectBikeMessage() {
        Toast.makeText(this, "Debe seleccionar una bicicleta", Toast.LENGTH_SHORT).show()
    }

    override fun showUserNotPaidMessage() {
        Toast.makeText(this, "Usuario no registrado como usuario de pago", Toast.LENGTH_SHORT).show()
    }

    override fun navigateToLoginScreen() {
        // Redirigir a la pantalla de inicio de sesión
        val intent = Intent(this, AuthSignUpActivity::class.java)
        startActivity(intent)
    }

    override fun showBikeNotReservedMessage() {
        Toast.makeText(this, "Error al reservar la bicicleta", Toast.LENGTH_SHORT).show()
    }

    override fun showBikeNotAvailableMessage() {
        Toast.makeText(this, "Bicicleta Reservada, por favor eliga otra", Toast.LENGTH_SHORT).show()
    }

    override fun showBikeBrokenMessage() {
        Toast.makeText(this, "Bicicleta averiada, por favor eliga otra", Toast.LENGTH_SHORT).show()
    }
}