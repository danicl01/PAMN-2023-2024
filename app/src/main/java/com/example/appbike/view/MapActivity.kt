package com.example.appbike.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.appbike.model.Bike
import com.example.appbike.model.BikeRepository
import com.example.appbike.presenter.BikeLoader
import com.example.appbike.presenter.BikePresenter
import com.example.appbike.R
import com.example.appbike.model.UserRepository
import com.example.appbike.presenter.RentBikePresenter
import com.google.android.gms.common.api.Status
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
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth

class MapActivity : AppCompatActivity(), OnMapReadyCallback, MapContract.View, GoogleMap.OnMarkerClickListener, BikeLoader {

    private lateinit var map: GoogleMap
    private lateinit var bikeLoader: BikeLoader
    private lateinit var presenter: RentBikePresenter
    private lateinit var userRepository: UserRepository
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private lateinit var permissionManager: PermissionManager

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(1500)   // WE HAVE TO REPLACE THIS FOR A HANDLER
        setTheme(R.style.SplashTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionManager = PermissionManager(this)

        initializeDependencies()
        setUpAutoCompletePlace()
        createFragment()
        setPopUpMenu()
        setAuthButton()
        checkLocationPermissionAndEnable()
        loadBikes()
    }

    private fun initializeDependencies() {
        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        userRepository = UserRepository()
        val bikeRepository = BikeRepository()
        presenter = RentBikePresenter(this, userRepository,bikeRepository )
        bikeLoader = BikePresenter(bikeRepository, this)
    }

    private fun setUpAutoCompletePlace() {
        autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(object :PlaceSelectionListener{
            override fun onError(p0: Status) {
                Toast.makeText(this@MapActivity, "Some Error in Search", Toast.LENGTH_SHORT).show()
            }

            override fun onPlaceSelected(place: Place) {
                zoomOnMap(place.latLng!!)
            }
        })
    }

    private fun setPopUpMenu() {
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
    }

    private fun changeMapType(itemId: Int) {
        map.mapType = when(itemId) {
            R.id.normal_map -> GoogleMap.MAP_TYPE_NORMAL
            R.id.hybrid_map -> GoogleMap.MAP_TYPE_HYBRID
            R.id.satellite_map -> GoogleMap.MAP_TYPE_SATELLITE
            R.id.terrain_map -> GoogleMap.MAP_TYPE_TERRAIN
            else -> map.mapType
        }
    }

    private fun setAuthButton() {
        val goToAuthButton = findViewById<ImageButton>(R.id.goToAuthButton)
        goToAuthButton.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser != null) {
                Log.d("MapActivity", "Usuario logeado. Redirigiendo a la pantalla de perfil.")
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
            } else {
                Log.d("MapActivity", "Ningún usuario logeado. Iniciando AuthActivity.")
                val intent = Intent(this, AuthSignUpActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(28.09973, -15.41343), 12.0f)) //Coordenadas para LPGC
        enableLocation()
        map.setOnMarkerClickListener(this)
        map.uiSettings.isZoomControlsEnabled = true
    }

    private fun checkLocationPermissionAndEnable() {
        if (permissionManager.isLocationPermissionGranted()) {
            if (isLocationEnabled()) { enableLocation()
            } else showEnableLocationDialog()
        } else requestLocationPermission()
    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private fun enableLocation() {
        if (!::map.isInitialized) return
        if (isLocationPermissionGranted()) {
            if (isLocationEnabled()) {
                map.isMyLocationEnabled = true
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        latitude = it.latitude
                        longitude = it.longitude
                    }
                }
            }
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (permissionManager.shouldShowLocationPermissionRationale()) {
            Toast.makeText(this, "Go to settings and accept the permissions", Toast.LENGTH_SHORT).show()
        } else {
            permissionManager.requestLocationPermission(REQUEST_CODE_LOCATION)
        }
    }

    @SuppressLint("MissingSuperCall", "MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(this, "To activate the location, go to settings and accept the permissions", Toast.LENGTH_SHORT).show()
            }else -> {}
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun showEnableLocationDialog() {
        val builder = AlertDialog.Builder(this).apply {
            setTitle("Activar Ubicación")
            setMessage("Para usar la aplicación, es necesario activar la ubicación. ¿Desea activar la ubicación ahora?")
            setPositiveButton("Sí") { _, _ ->
                val enableLocationIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(enableLocationIntent, REQUEST_CODE_LOCATION)
            }
            setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (isLocationEnabled()) {
                enableLocation()
            } else {
                Toast.makeText(this, "La ubicación no está activada. Algunas funciones pueden no estar disponibles.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun zoomOnMap(latLng: LatLng) {
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(latLng, 12f)
        map.animateCamera(newLatLngZoom)
    }

    override fun displayBikes(bikes: List<Bike>) {
        if (!::map.isInitialized) return
        for (bike in bikes) {
            val bikeLocation = LatLng(bike.latitude, bike.altitude)
            val markerColor = getMarkerColorForBikeState(bike.state)
            markerColor?.let {
                map.addMarker(MarkerOptions().position(bikeLocation).title(bike.name).icon(it))
            }
        }
    }

    private fun getMarkerColorForBikeState(state: String?): BitmapDescriptor? {
        return when(state) {
            "En espera" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            "Alquilada" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
            "Averiada" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            else -> null
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        zoomOnMap(marker.position)
        val distanceKm = calculateDistanceFromCurrentToBike(marker.position)
        showBikeInfoDialog(marker.title, distanceKm, marker)
        return true
    }

    private fun showBikeInfoDialog(title: String?, distanceKm: Double, marker: Marker) {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_bike_info, null)
        builder.setView(dialogView)

        val titleTextView = dialogView.findViewById<TextView>(R.id.textViewDialogTitle)
        val bikeIconImageView = dialogView.findViewById<ImageView>(R.id.imageViewBikeIcon)
        val bikeInfoTextView = dialogView.findViewById<TextView>(R.id.textViewBikeInfo)
        val checkBoxReservar = dialogView.findViewById<CheckBox>(R.id.checkBoxReservar)

        titleTextView.text = "Información de la Bicicleta"
        bikeIconImageView.setImageResource(R.drawable.ic_bike_24)
        bikeInfoTextView.text = "${marker.title} \nA: ${String.format("%.2f", distanceKm)} km"

        builder.setPositiveButton("Alquilar") { _, _ ->
            handleRentButtonClick(checkBoxReservar.isChecked, marker)
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun handleRentButtonClick(isBikeSelected: Boolean, marker: Marker) =
        presenter.onRentButtonClick(userRepository.isUserLoggedIn(), isBikeSelected, marker)

    private fun calculateDistanceFromCurrentToBike(bikePosition: LatLng): Double {
        if (latitude == 0.0 || longitude == 0.0) return 0.0
        val result = FloatArray(10)
        Location.distanceBetween(latitude, longitude, bikePosition.latitude, bikePosition.longitude, result)
        return result[0].toDouble() / 1000
    }

    // MapContract.View functions
    override fun navigateToLoginScreen() = startActivity(Intent(this, AuthSignUpActivity::class.java))
    override fun showBikeReservedMessage() = Toast.makeText(this, "Bicicleta reservada", Toast.LENGTH_SHORT).show()
    override fun showMustSelectBikeMessage() = Toast.makeText(this, "Debe seleccionar una bicicleta", Toast.LENGTH_SHORT).show()
    override fun showUserNotPaidMessage() = Toast.makeText(this, "Usuario no registrado como usuario de pago", Toast.LENGTH_SHORT).show()
    override fun showBikeNotReservedMessage() = Toast.makeText(this, "Error al reservar la bicicleta", Toast.LENGTH_SHORT).show()
    override fun showBikeNotAvailableMessage() = Toast.makeText(this, "Bicicleta Reservada, por favor eliga otra", Toast.LENGTH_SHORT).show()
    override fun showBikeBrokenMessage() = Toast.makeText(this, "Bicicleta averiada, por favor eliga otra", Toast.LENGTH_SHORT).show()

    override fun loadBikes() = bikeLoader.loadBikes()
}