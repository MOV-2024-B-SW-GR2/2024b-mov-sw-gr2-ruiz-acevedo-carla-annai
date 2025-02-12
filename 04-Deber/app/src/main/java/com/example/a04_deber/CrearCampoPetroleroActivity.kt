package com.example.a04_deber

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.a04_deber.modelo.CampoPetrolero
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class CrearCampoPetroleroActivity : AppCompatActivity() {
    private var campoPetroleroId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_campo_petrolero)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnGuardar = findViewById<Button>(R.id.btn_guardar_cp)
        val itNombre = findViewById<EditText>(R.id.it_nombre_cp)
        val itFechaInstalacion = findViewById<EditText>(R.id.it_fecha_instalacion)
        val itPaisOrigen = findViewById<EditText>(R.id.it_pais_origen)
        val itNumeroPozos = findViewById<EditText>(R.id.it_numero_pozos)
        val itCodigo = findViewById<EditText>(R.id.it_codigo_cp)
        val itLatitud = findViewById<EditText>(R.id.it_latitud_cp)
        val itLongitud = findViewById<EditText>(R.id.it_longitud_cp)

        // Recuperar datos si es una edición
        campoPetroleroId = intent.getIntExtra("campoPetroleroId", -1)
        val nombre = intent.getStringExtra("nombre")
        val fechaInstalacion = intent.getStringExtra("fechaInstalacion")
        val paisOrigen = intent.getStringExtra("paisOrigen")
        val numeroPozos = intent.getIntExtra("numeroPozos", -1)
        val codigo = intent.getStringExtra("codigo")
        val latitud = intent.getDoubleExtra("latitud", Double.NaN)
        val longitud = intent.getDoubleExtra("longitud", Double.NaN)

        if (campoPetroleroId != -1 && nombre != null && fechaInstalacion != null && paisOrigen != null
            && numeroPozos != -1 && codigo != null && !latitud.isNaN() && !longitud.isNaN()) {
            itNombre.setText(nombre)
            itFechaInstalacion.setText(fechaInstalacion)
            itPaisOrigen.setText(paisOrigen)
            itNumeroPozos.setText(numeroPozos.toString())
            itCodigo.setText(codigo)
            itLatitud.setText(latitud.toString())
            itLongitud.setText(longitud.toString())
        }

        btnGuardar.setOnClickListener {
            val nuevoNombre = itNombre.text.toString()
            val nuevaFechaInstalacion = itFechaInstalacion.text.toString()
            val nuevoPaisOrigen = itPaisOrigen.text.toString()
            val nuevoNumeroPozos = itNumeroPozos.text.toString()
            val nuevoCodigo = itCodigo.text.toString()
            val nuevaLatitud = itLatitud.text.toString()
            val nuevaLongitud = itLongitud.text.toString()

            if (nuevoNombre.isEmpty() || nuevaFechaInstalacion.isEmpty() || nuevoPaisOrigen.isEmpty() ||
                nuevoNumeroPozos.isEmpty() || nuevoCodigo.isEmpty()) {
                mostrarSnackbar("Por favor, llene todos los campos")
            } else {
                // Validar el formato de la fecha
                val nuevaFecha: LocalDate? = try {
                    LocalDate.parse(nuevaFechaInstalacion, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                } catch (e: DateTimeParseException) {
                    mostrarSnackbar("Fecha inválida. Use el formato yyyy-MM-dd.")
                    return@setOnClickListener
                }

                // Validar el número de pozos
                val nuevoNumeroPozosInt = nuevoNumeroPozos.toIntOrNull()
                if (nuevoNumeroPozosInt == null || nuevoNumeroPozosInt <= 0) {
                    mostrarSnackbar("El número de pozos debe ser un número positivo.")
                    return@setOnClickListener
                }

                // Validar latitud y longitud
                val nuevaLatitudDouble = nuevaLatitud.toDoubleOrNull()
                val nuevaLongitudDouble = nuevaLongitud.toDoubleOrNull()

                if (nuevaLatitudDouble == null || nuevaLongitudDouble == null) {
                    mostrarSnackbar("Latitud y longitud deben ser valores numéricos válidos.")
                    return@setOnClickListener
                }

                val campoPetrolero = CampoPetrolero(
                    id = campoPetroleroId ?: 0,  // El ID se asignará automáticamente si es nuevo
                    nombre = nuevoNombre,
                    fechaInstalacion = nuevaFecha ?: LocalDate.now(),  // Usar la fecha actual si no se proporciona
                    paisOrigen = nuevoPaisOrigen,
                    numeroPozos = nuevoNumeroPozosInt,
                    codigo = nuevoCodigo,
                    latitud = nuevaLatitudDouble,
                    longitud = nuevaLongitudDouble,
                    pozos = mutableListOf()
                )

                if (campoPetroleroId != -1) {
                    // Actualizar campo petrolero existente
                    BDSQLite.bdsqLite?.actualizarCampoPetrolero(campoPetrolero)
                    mostrarSnackbar("Campo Petrolero actualizado")
                } else {
                    // Registrar nuevo campo petrolero
                    BDSQLite.bdsqLite?.registrarCampoPetrolero(campoPetrolero)
                    mostrarSnackbar("Campo Petrolero guardado")
                }
            }
        }
    }

    private fun mostrarSnackbar(texto: String) {
        Snackbar.make(findViewById(R.id.main), texto, Snackbar.LENGTH_LONG).show()
    }
}
