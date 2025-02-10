package com.example.gr2sw2024b_cara

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar

class GGoogleMaps : AppCompatActivity() {
    private lateinit var mapa: GoogleMap
    var permisos = false
    val nombrePermisoFine = android.Manifest.permission.ACCESS_FINE_LOCATION
    val nombrePermisoCoarse = android.Manifest.permission.ACCESS_COARSE_LOCATION
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ggoogle_maps)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        solicitarPermisos()
        inicializarLogicaMapa()

        val botonCarolina = findViewById<Button>(R.id.btn_ir_carolina)
        botonCarolina.setOnClickListener {
            val carolina = LatLng(-0.18221288005854652, -78.48553955554578)
            moverCamaraConZoom(carolina)
        }
    }
    fun tengoPermisos():Boolean{
        val contexto = applicationContext
        val permisoFine = ContextCompat.checkSelfPermission(contexto, nombrePermisoFine)
        val permisoCoarse = ContextCompat.checkSelfPermission(contexto, nombrePermisoCoarse)
        val tienePermisos = permisoFine == PackageManager.PERMISSION_GRANTED &&
                permisoCoarse == PackageManager.PERMISSION_GRANTED
        permisos = tienePermisos
        return permisos
    }
    fun solicitarPermisos(){
        if(!tengoPermisos()){
            ActivityCompat.requestPermissions(
                this, arrayOf(nombrePermisoFine, nombrePermisoCoarse), 1
            )
        }
    }

    fun inicializarLogicaMapa(){
        val fragmentoMapa = supportFragmentManager.findFragmentById(
            R.id.map ) as SupportMapFragment
        fragmentoMapa.getMapAsync { googleMap ->
            with(googleMap){
                mapa = googleMap
                establecerConfiguracionMapa()
                moverQuicentro()
                anadirPolilinea()
                anadirPoligono()
                escucharListeners()
            }
        }
    }
    fun moverQuicentro(){
        val quicentro = LatLng(-0.17633018352832922, -78.47853222234333)
        val titulo = "Quicentro"
        val marcadorQuicentro = anadirMarcador(quicentro, titulo)
        marcadorQuicentro.tag = titulo
        moverCamaraConZoom(quicentro)
    }
    fun anadirPolilinea(){
        with(mapa){
            val polilinea = mapa.addPolyline(
                PolylineOptions()
                    .clickable(true)
                    .add(
                        LatLng(-0.17791267925471754, -78.48185816127831),
                        LatLng(-0.18019791013168018, -78.48539867691878),
                        LatLng(-0.1822149211841061, -78.48320999452285)
                    )
            )
            polilinea.tag = "polilinea-uno"
        }
    }
    fun anadirPoligono(){
        with(mapa){
            val poligono = mapa.addPolygon(
                PolygonOptions()
                    .clickable(true)
                    .add(
                        LatLng(-0.17810579736798682, -78.48025956482248),
                        LatLng(-0.18047685848208925, -78.47937980033),
                        LatLng(-0.17664668268438008, -78.4796694788824),
                    )
            )
            poligono.tag = "poligono-uno"
        }
    }
    fun escucharListeners(){
        mapa.setOnPolygonClickListener {
            mostrarSnackbar("setOnPolygonClickListener ${it.tag}")
        }
        mapa.setOnPolylineClickListener {
            mostrarSnackbar("setOnPolylineClickListener ${it.tag}")
        }
        mapa.setOnMarkerClickListener {
            mostrarSnackbar("setOnMarkerClickListener ${it.tag}")
            return@setOnMarkerClickListener true
        }
        mapa.setOnCameraIdleListener { mostrarSnackbar("setOnCameraIdleListener") }
        mapa.setOnCameraMoveListener { mostrarSnackbar("setOnCameraMoveListener") }
        mapa.setOnCameraMoveStartedListener { mostrarSnackbar("setOnCameraMoveStartedListener") }
    }


    @SuppressLint("MissingPermission")
    fun establecerConfiguracionMapa(){
        with(mapa){
            if(tengoPermisos()){
                mapa.isMyLocationEnabled = true
                uiSettings.isMyLocationButtonEnabled = true
            }
            uiSettings.isZoomControlsEnabled = true
        }
    }
    fun moverCamaraConZoom(latLang:LatLng, zoom: Float = 17f){
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(latLang,zoom))
    }
    fun anadirMarcador(latLang:LatLng, title: String):Marker{
        return mapa.addMarker(MarkerOptions().position(latLang).title(title))!!
    }
    fun mostrarSnackbar(texto: String){
        val snack = Snackbar.make(
            findViewById(R.id.main),
            texto,
            Snackbar.LENGTH_INDEFINITE
        )
        snack.show()
    }


}