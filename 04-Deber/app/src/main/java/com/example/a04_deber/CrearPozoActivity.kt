package com.example.a04_deber

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.a04_deber.modelo.Pozo
import com.google.android.material.snackbar.Snackbar

class CrearPozoActivity : AppCompatActivity() {
    private var campoPetroleroId: Int = 0
    private var pozoId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_pozo)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        campoPetroleroId = intent.getIntExtra("campoPetroleroId", -1)
        pozoId = intent.getIntExtra("pozoId", 0)

        val btnGuardarPozo = findViewById<Button>(R.id.btn_guardar_pozo)
        val itNombre = findViewById<EditText>(R.id.it_nombre_pozo)
        val itCapacidad = findViewById<EditText>(R.id.it_capacidad_pozo)
        val itUbicacion = findViewById<EditText>(R.id.it_ubicacion_pozo)
        val swActivo = findViewById<Switch>(R.id.sw_activo_pozo)
        val itRunLife = findViewById<EditText>(R.id.it_runLife_pozo)

        // Si es edición, llenar los campos con los datos existentes
        if (pozoId != 0) {
            itNombre.setText(intent.getStringExtra("nombre"))
            itCapacidad.setText(intent.getDoubleExtra("capacidad", 0.0).toString())
            itUbicacion.setText(intent.getStringExtra("ubicacion"))
            swActivo.isChecked = intent.getBooleanExtra("activo", false) // Corregido aquí
            itRunLife.setText(intent.getIntExtra("runLife", 0).toString())
        }

        btnGuardarPozo.setOnClickListener {
            val nuevoNombre = itNombre.text.toString()
            val nuevaCapacidad = itCapacidad.text.toString()
            val nuevaUbicacion = itUbicacion.text.toString()
            val nuevoActivo = swActivo.isChecked // Corregido aquí
            val nuevoRunLife = itRunLife.text.toString()

            if (nuevoNombre.isEmpty() || nuevaCapacidad.isEmpty() || nuevaUbicacion.isEmpty() || nuevoRunLife.isEmpty()) {
                mostrarSnackbar("Por favor, llene todos los campos")
            } else {
                val pozo = Pozo(
                    id = pozoId,
                    nombre = nuevoNombre,
                    capacidad = nuevaCapacidad.toDouble(),
                    ubicacion = nuevaUbicacion,
                    activo = nuevoActivo,
                    runLife = nuevoRunLife.toInt(),
                    campoPetroleroId = campoPetroleroId
                )

                val exito = if (pozoId == 0) {
                    BDSQLite.bdsqLite?.registrarPozo(pozo) ?: false
                } else {
                    BDSQLite.bdsqLite?.actualizarPozo(pozo) ?: false
                }

                if (exito) {
                    mostrarSnackbar("Pozo ${if (pozoId == 0) "creado" else "actualizado"} exitosamente")
                } else {
                    mostrarSnackbar("Error al ${if (pozoId == 0) "crear" else "actualizar"} el pozo")
                }
            }
        }
    }

    private fun mostrarSnackbar(texto: String) {
        Snackbar.make(findViewById(R.id.main), texto, Snackbar.LENGTH_LONG).show()
    }
}
