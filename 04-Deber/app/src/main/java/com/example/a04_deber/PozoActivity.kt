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
import com.example.a04_deber.modelo.Pozo
import com.google.android.material.snackbar.Snackbar

class PozoActivity : AppCompatActivity() {
    private var campoPetroleroId: Int? = null
    private lateinit var pozos: ArrayList<Pozo>
    private lateinit var btnAgregarPozo: Button
    private lateinit var listViewPozos: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pozo)

        // Obtener el ID del campo petrolero que se pasó desde MainActivity
        campoPetroleroId = intent.getIntExtra("campoPetroleroId", -1)
        if (campoPetroleroId == -1) {
            mostrarSnackbar("Error: No se proporcionó un ID válido para el campo petrolero.")
            finish()  // Cerrar la actividad si no hay un ID válido
            return
        }

        // Configurar vistas
        listViewPozos = findViewById(R.id.ls_pozos)
        btnAgregarPozo = findViewById(R.id.btn_crear_p)

        // Registrar para el menú contextual
        registerForContextMenu(listViewPozos)

        // Actualizar la lista de pozos
        actualizarLista()

        // Manejar clic en el botón para agregar un pozo
        btnAgregarPozo.setOnClickListener {
            val intent = Intent(this, CrearPozoActivity::class.java)
            intent.putExtra("campoPetroleroId", campoPetroleroId)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        actualizarLista() // Actualizar la lista de pozos cada vez que se vuelva a esta actividad
    }

    private fun actualizarLista() {
        pozos = campoPetroleroId?.let { BDSQLite.bdsqLite?.listarPozos(it) } ?: ArrayList()

        if (pozos.isEmpty()) {
            mostrarSnackbar("No se encontraron pozos para este campo petrolero.")
        } else {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                pozos.map { it.nombre }
            )
            listViewPozos.adapter = adapter
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.menu_p, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as? AdapterView.AdapterContextMenuInfo
        val pozoIndex = info?.position
        val pozoSeleccionado = pozoIndex?.let { pozos[it] }

        when (item.itemId) {
            R.id.m_eliminar_p -> {
                pozoSeleccionado?.let {
                    BDSQLite.bdsqLite?.eliminarPozo(it.id, campoPetroleroId ?: 0)
                    mostrarSnackbar("Pozo ${it.nombre} eliminado")
                    actualizarLista()
                }
            }
            R.id.m_editar_p -> {
                pozoSeleccionado?.let {
                    val intent = Intent(this, CrearPozoActivity::class.java)
                    intent.putExtra("pozoId", it.id)
                    intent.putExtra("nombre", it.nombre)
                    intent.putExtra("capacidad", it.capacidad)
                    intent.putExtra("ubicacion", it.ubicacion)
                    intent.putExtra("activo", it.activo)
                    intent.putExtra("runLife", it.runLife)
                    intent.putExtra("campoPetroleroId", campoPetroleroId)
                    startActivity(intent)
                }
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun mostrarSnackbar(texto: String) {
        Snackbar.make(
            findViewById(R.id.main),
            texto,
            Snackbar.LENGTH_LONG
        ).show()
    }
}
