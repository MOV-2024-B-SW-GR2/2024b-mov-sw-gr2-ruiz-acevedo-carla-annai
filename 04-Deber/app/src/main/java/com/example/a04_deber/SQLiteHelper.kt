package com.example.a04_deber

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.a04_deber.modelo.Pozo
import com.example.a04_deber.modelo.CampoPetrolero
import java.time.LocalDate

class SQLiteHelper(contexto: Context?) : SQLiteOpenHelper(
    contexto, "moviles", null, 1
) {
    override fun onCreate(db: SQLiteDatabase?) {
        // Crear tabla CampoPetrolero sin el campo "ubicacion"
        val scriptSQLCrearTablaCampoPetrolero =
            """
                CREATE TABLE CAMPOPETROLERO(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre VARCHAR(50),
                    fecha_instalacion DATE,
                    pais_origen VARCHAR(100),
                    numero_pozos INTEGER,
                    codigo VARCHAR(50)
                )
            """.trimIndent()
        db?.execSQL(scriptSQLCrearTablaCampoPetrolero)

        // Crear tabla Pozo con runLife
        val scriptSQLCrearTablaPozo =
            """
                CREATE TABLE POZO(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre VARCHAR(50),
                    capacidad DOUBLE,
                    ubicacion VARCHAR(100),
                    activo INTEGER,  -- Usamos INTEGER para representar el booleano
                    runLife INTEGER, -- Nueva columna para almacenar runLife
                    campo_petrolero_id INTEGER,
                    FOREIGN KEY(campo_petrolero_id) REFERENCES CAMPOPETROLERO(id) ON DELETE CASCADE
                )
            """.trimIndent()
        db?.execSQL(scriptSQLCrearTablaPozo)
    }

    // Método para crear la tabla Pozo si no existe
    fun createPozoTableIfNotExists() {
        val db = writableDatabase
        val scriptSQLCrearTablaPozo =
            """
                CREATE TABLE IF NOT EXISTS POZO(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre VARCHAR(50),
                    capacidad DOUBLE,
                    ubicacion VARCHAR(100),
                    activo INTEGER,  -- Usamos INTEGER para representar el booleano
                    runLife INTEGER, -- Nueva columna para almacenar runLife
                    campo_petrolero_id INTEGER,
                    FOREIGN KEY(campo_petrolero_id) REFERENCES CAMPOPETROLERO(id) ON DELETE CASCADE
                )
            """.trimIndent()
        db.execSQL(scriptSQLCrearTablaPozo)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Implementar la lógica de actualización si es necesario
    }

    // Registrar CampoPetrolero (sin "ubicacion")
    fun registrarCampoPetrolero(campoPetrolero: CampoPetrolero): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", campoPetrolero.nombre)
            put("fecha_instalacion", campoPetrolero.fechaInstalacion.toString())
            put("pais_origen", campoPetrolero.paisOrigen)
            put("numero_pozos", campoPetrolero.numeroPozos)
            put("codigo", campoPetrolero.codigo)
        }
        val resultado = db.insert("CAMPOPETROLERO", null, valores)
        db.close()
        return resultado != -1L
    }

    // Listar CampoPetroleros (sin "ubicacion")
    fun listarCampoPetroleros(): ArrayList<CampoPetrolero> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM CAMPOPETROLERO", null)
        val lista = ArrayList<CampoPetrolero>()

        while (cursor.moveToNext()) {
            lista.add(
                CampoPetrolero(
                    cursor.getInt(0),
                    cursor.getString(1),
                    LocalDate.parse(cursor.getString(2)), // Asumiendo que "fecha_instalacion" es un String
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getString(5)
                )
            )
        }
        cursor.close()
        db.close()
        return lista
    }

    // Eliminar CampoPetrolero
    fun eliminarCampoPetrolero(id: Int) {
        val db = writableDatabase
        db.execSQL("DELETE FROM CAMPOPETROLERO WHERE id = ?", arrayOf(id.toString()))
        db.close()
    }

    // Actualizar CampoPetrolero (sin "ubicacion")
    fun actualizarCampoPetrolero(campoPetrolero: CampoPetrolero): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", campoPetrolero.nombre)
            put("fecha_instalacion", campoPetrolero.fechaInstalacion.toString())
            put("pais_origen", campoPetrolero.paisOrigen)
            put("numero_pozos", campoPetrolero.numeroPozos)
            put("codigo", campoPetrolero.codigo)
        }
        val resultado = db.update("CAMPOPETROLERO", valores, "id = ?", arrayOf(campoPetrolero.id.toString()))
        db.close()
        return resultado > 0
    }

    // Registrar Pozo
    fun registrarPozo(pozo: Pozo): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", pozo.nombre)
            put("capacidad", pozo.capacidad)
            put("ubicacion", pozo.ubicacion)
            put("activo", if (pozo.activo) 1 else 0)
            put("runLife", pozo.runLife) // Nuevo campo
            put("campo_petrolero_id", pozo.campoPetroleroId)
        }
        val resultado = db.insert("POZO", null, valores)
        db.close()
        return resultado != -1L
    }

    // Listar Pozos de un CampoPetrolero específico
    fun listarPozos(campoPetroleroId: Int): ArrayList<Pozo> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM POZO WHERE campo_petrolero_id = ?", arrayOf(campoPetroleroId.toString()))
        val lista = ArrayList<Pozo>()

        while (cursor.moveToNext()) {
            lista.add(
                Pozo(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getString(3),
                    cursor.getInt(4) == 1, // Convertir Integer a Boolean
                    cursor.getInt(5), // runLife
                    cursor.getInt(6) // campoPetroleroId
                )
            )
        }
        cursor.close()
        db.close()
        return lista
    }

    // Eliminar Pozo
    fun eliminarPozo(pozoId: Int, campoPetroleroId: Int) {
        val db = writableDatabase
        db.execSQL("DELETE FROM POZO WHERE id = ? AND campo_petrolero_id = ?", arrayOf(pozoId.toString(), campoPetroleroId.toString()))
        db.close()
    }

    // Actualizar Pozo
    fun actualizarPozo(pozo: Pozo): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", pozo.nombre)
            put("capacidad", pozo.capacidad)
            put("ubicacion", pozo.ubicacion)
            put("activo", if (pozo.activo) 1 else 0)
            put("runLife", pozo.runLife) // Nuevo campo
        }
        val resultado = db.update("POZO", valores, "id = ?", arrayOf(pozo.id.toString()))
        db.close()
        return resultado > 0
    }
}
