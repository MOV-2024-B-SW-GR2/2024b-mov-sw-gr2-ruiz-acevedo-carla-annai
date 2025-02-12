package com.example.a04_deber

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.a04_deber.modelo.CampoPetrolero
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var camposPetroleros: ArrayList<CampoPetrolero>
    private lateinit var btnAgregarCampoPetrolero: Button
    private lateinit var listViewCamposPetroleros: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Activar Edge to Edge para mejor visualización
        enableEdgeToEdge()

        // Inicializar base de datos
        if (BDSQLite.bdsqLite == null) {
            BDSQLite.bdsqLite = SQLiteHelper(this)
        }

        // Configurar vistas
        listViewCamposPetroleros = findViewById(R.id.ls_campos_petroleros)
        btnAgregarCampoPetrolero = findViewById(R.id.btn_crear_cp)

        // Registrar para el menú contextual
        registerForContextMenu(listViewCamposPetroleros)

        // Manejar clic en el botón para agregar un campo petrolero
        btnAgregarCampoPetrolero.setOnClickListener {
            startActivity(Intent(this, CrearCampoPetroleroActivity::class.java))
        }

        // Actualizar la lista al iniciar la actividad
        actualizarLista()
    }

    private fun actualizarLista() {
        try {
            // Obtener la lista de campos petroleros de la base de datos
            camposPetroleros = BDSQLite.bdsqLite?.listarCampoPetroleros() ?: ArrayList()

            // Verifica si la lista está vacía
            if (camposPetroleros.isEmpty()) {
                mostrarSnackbar("No se encontraron campos petroleros.")
            }

            // Crear el adaptador para mostrar los campos petroleros
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                camposPetroleros.map { "${it.nombre} - ${it.codigo}" } // Mostrar nombre y código
            )
            listViewCamposPetroleros.adapter = adapter
        } catch (e: Exception) {
            // Imprimir el stack trace para obtener más detalles
            e.printStackTrace()
            mostrarSnackbar("Error al cargar los campos petroleros: ${e.message}")
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.menu_cp, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as? AdapterView.AdapterContextMenuInfo
        val campoPetroleroIndex = info?.position
        val campoPetroleroSeleccionado = campoPetroleroIndex?.let { camposPetroleros[it] }

        when (item.itemId) {
            R.id.m_eliminar_cp -> {
                campoPetroleroSeleccionado?.let {
                    BDSQLite.bdsqLite?.eliminarCampoPetrolero(it.id)
                    mostrarSnackbar("Campo Petrolero ${it.nombre} eliminado")
                    actualizarLista() // Refrescar la lista después de la eliminación
                }
            }
            R.id.m_ver_cp -> {
                campoPetroleroSeleccionado?.let {
                    val intent = Intent(this, PozoActivity::class.java)
                    intent.putExtra("campoPetroleroId", it.id)
                    mostrarSnackbar("Ver pozos del campo petrolero ${it.id}")
                    startActivity(intent)
                }
            }
            R.id.m_editar_cp -> {
                campoPetroleroSeleccionado?.let {
                    mostrarSnackbar("Editar campo petrolero ${it.nombre}")
                    val intent = Intent(this, CrearCampoPetroleroActivity::class.java)
                    intent.putExtra("campoPetroleroId", it.id)
                    intent.putExtra("nombre", it.nombre)
                    intent.putExtra("fecha_instalacion", it.fechaInstalacion.toString())
                    intent.putExtra("pais_origen", it.paisOrigen)
                    intent.putExtra("numero_pozos", it.numeroPozos)
                    intent.putExtra("codigo", it.codigo)
                    startActivity(intent)
                }
            }
            R.id.m_ver_ubicacion -> {
                campoPetroleroSeleccionado?.let {
                    val intent = Intent(this, MapsActivity::class.java)
                    intent.putExtra("campoPetroleroId", it.id)  // Asegúrate de pasar el ID aquí también
                    intent.putExtra("latitud", it.latitud)
                    intent.putExtra("longitud", it.longitud)
                    startActivity(intent)
                }
            }
        }
        return super.onContextItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        actualizarLista() // Actualizar la lista cada vez que se vuelva a esta actividad
    }

    private fun mostrarSnackbar(texto: String) {
        Snackbar.make(
            findViewById(R.id.main),
            texto,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun enableEdgeToEdge() {
        // Activar el modo Edge to Edge en la actividad para visualización sin recortes
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
