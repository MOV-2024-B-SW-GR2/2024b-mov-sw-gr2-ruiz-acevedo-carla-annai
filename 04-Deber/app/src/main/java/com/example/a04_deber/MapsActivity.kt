package com.example.a04_deber

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Marker
import com.example.a04_deber.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var currentMarker: Marker? = null
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    private var campoPetroleroId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Obtener la latitud, longitud e id del campo petrolero desde el Intent
        latitud = intent.getDoubleExtra("latitud", 0.0)
        longitud = intent.getDoubleExtra("longitud", 0.0)
        campoPetroleroId = intent.getIntExtra("campoPetroleroId", 0)

        // Verificar si el campoPetroleroId es válido
        if (campoPetroleroId == 0) {
            Toast.makeText(this, "ID del campo petrolero no válido.", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("MapsActivity", "Campo Petrolero ID recibido: $campoPetroleroId")
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Configurar el mapa
        with(mMap.uiSettings) {
            isZoomControlsEnabled = true
            isZoomGesturesEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }

        // Agregar marcador en la ubicación inicial
        val ubicacion = LatLng(latitud, longitud)
        currentMarker = mMap.addMarker(
            MarkerOptions().position(ubicacion).title("Ubicación actual")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 15f))

        // Permitir al usuario mover el marcador y actualizar la ubicación
        mMap.setOnMapClickListener { latLng ->
            // Eliminar el marcador actual
            currentMarker?.remove()

            // Colocar un nuevo marcador en la posición seleccionada
            currentMarker = mMap.addMarker(
                MarkerOptions().position(latLng).title("Nueva ubicación")
            )

            // Actualizar las coordenadas
            latitud = latLng.latitude
            longitud = latLng.longitude

            // Guardar la nueva ubicación automáticamente
            guardarNuevaUbicacion()

            // Mostrar mensaje de la nueva ubicación
            Toast.makeText(this, "Ubicación cambiada y guardada: $latitud, $longitud", Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarNuevaUbicacion() {
        try {
            // Verificar si el campoPetroleroId es válido antes de intentar actualizar
            if (campoPetroleroId == 0) {
                Toast.makeText(this, "ID del campo petrolero no válido.", Toast.LENGTH_SHORT).show()
                return
            }

            val db = BDSQLite.bdsqLite
            val actualizado = db?.actualizarCampoPetroleroUbicacion(campoPetroleroId, latitud, longitud)

            if (actualizado == true) {
                Toast.makeText(this, "Ubicación guardada con éxito.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No se pudo guardar la ubicación.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar la ubicación: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
