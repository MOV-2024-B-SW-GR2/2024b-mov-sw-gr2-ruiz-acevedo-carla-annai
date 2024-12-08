import java.io.File
import java.time.LocalDate
import java.util.Scanner

data class Pozo(
    var nombre: String,
    var capacidad: Double,
    var ubicacion: String,
    var activo: Boolean,
    var runLife: Int
)

data class CampoPetrolero(
    var nombre: String,
    var fechaInstalacion: LocalDate,
    var paisOrigen: String,
    var numeroPozos: Int,
    var codigo: String,
    var pozos: MutableList<Pozo> = mutableListOf()
) {
    fun generarCodigo(): String {
        val consonantes = nombre.filter { it.isLetter() && it.toLowerCase() !in "aeiou" }
        return consonantes.toUpperCase()
    }
}

class CRUD {
    private val campoFile = File("campos.txt")
    private val pozosFile = File("pozos.txt")

    private val campos = mutableListOf<CampoPetrolero>()
    private val pozos = mutableListOf<Pozo>()

    init {
        loadData()
    }

    private fun loadData() {
        if (campoFile.exists()) {
            campoFile.readLines().forEach { line ->
                val data = line.split("|")
                val campo = CampoPetrolero(
                    nombre = data[0],
                    fechaInstalacion = LocalDate.parse(data[1]),
                    paisOrigen = data[2],
                    numeroPozos = data[3].toInt(),
                    codigo = data[4]
                )
                campos.add(campo)
            }
        }
        if (pozosFile.exists()) {
            pozosFile.readLines().forEach { line ->
                val data = line.split("|")
                val pozo = Pozo(
                    nombre = data[0],
                    capacidad = data[1].toDouble(),
                    ubicacion = data[2],
                    activo = data[3].toBoolean(),
                    runLife = data[4].toInt()
                )
                pozos.add(pozo)
                campos.find { it.nombre == data[5] }?.pozos?.add(pozo)
            }
        }
    }

    private fun saveData() {
        campoFile.writeText(campos.joinToString("\n") {
            "${it.nombre}|${it.fechaInstalacion}|${it.paisOrigen}|${it.numeroPozos}|${it.codigo}"
        })
        pozosFile.writeText(pozos.joinToString("\n") {
            "${it.nombre}|${it.capacidad}|${it.ubicacion}|${it.activo}|${it.runLife}|${findCampoNameByPozo(it)}"
        })
    }

    fun findCampoNameByPozo(pozo: Pozo): String {
        return campos.find { it.pozos.contains(pozo) }?.nombre ?: ""
    }

    fun campoExiste(nombre: String): Boolean {
        return campos.any { it.nombre == nombre }
    }

    fun pozoExiste(campoNombre: String, nombrePozo: String): Boolean {
        val campo = campos.find { it.nombre == campoNombre }
        return campo?.pozos?.any { it.nombre == nombrePozo } == true
    }

    fun createCampo(nombre: String, paisOrigen: String) {
        if (campoExiste(nombre)) {
            println("El campo '$nombre' ya existe.")
            return
        }
        val campo = CampoPetrolero(nombre, LocalDate.now(), paisOrigen, 0, "")
        campo.codigo = campo.generarCodigo()
        campos.add(campo)
        saveData()
        println("Campo '$nombre' creado con éxito. Código: ${campo.codigo}")
    }

    fun readCampos() = campos

    fun updateCampo(nombre: String, nuevoNombre: String? = null, paisOrigen: String? = null) {
        val campo = campos.find { it.nombre == nombre } ?: return
        if (nuevoNombre != null) campo.nombre = nuevoNombre
        if (paisOrigen != null) campo.paisOrigen = paisOrigen
        campo.codigo = campo.generarCodigo()
        saveData()
        println("Campo '$nombre' actualizado con éxito. Código: ${campo.codigo}")
    }

    fun deleteCampo(nombre: String) {
        campos.removeIf { it.nombre == nombre }
        pozos.removeIf { findCampoNameByPozo(it) == nombre }
        saveData()
        println("Campo '$nombre' eliminado con éxito.")
    }

    fun createPozo(campoNombre: String, nombre: String, capacidad: Double, ubicacion: String, activo: Boolean, runLife: Int) {
        if (pozoExiste(campoNombre, nombre)) {
            println("El pozo '$nombre' ya existe en el campo '$campoNombre'.")
            return
        }
        val campo = campos.find { it.nombre == campoNombre } ?: return
        val pozo = Pozo(nombre, capacidad, ubicacion, activo, runLife)
        campo.pozos.add(pozo)
        campo.numeroPozos = campo.pozos.size
        pozos.add(pozo)
        saveData()
        println("Pozo '$nombre' agregado con éxito al campo '$campoNombre'.")
    }

    fun readPozos(campoNombre: String) = campos.find { it.nombre == campoNombre }?.pozos ?: listOf()

    fun updatePozo(nombre: String, nuevoNombre: String? = null, capacidad: Double? = null, ubicacion: String? = null, activo: Boolean? = null, runLife: Int? = null) {
        val pozo = pozos.find { it.nombre == nombre } ?: return
        if (nuevoNombre != null) pozo.nombre = nuevoNombre
        if (capacidad != null) pozo.capacidad = capacidad
        if (ubicacion != null) pozo.ubicacion = ubicacion
        if (activo != null) pozo.activo = activo
        if (runLife != null) pozo.runLife = runLife
        saveData()
        println("Pozo '$nombre' actualizado con éxito.")
    }

    fun deletePozo(nombre: String) {
        val pozo = pozos.find { it.nombre == nombre } ?: return
        val campoNombre = findCampoNameByPozo(pozo)
        val campo = campos.find { it.nombre == campoNombre }
        campo?.pozos?.remove(pozo)
        campo?.numeroPozos = campo?.pozos?.size ?: 0
        pozos.removeIf { it.nombre == nombre }
        saveData()
        println("Pozo '$nombre' eliminado con éxito.")
    }

    fun readPozosActivos(): List<Pozo> {
        return pozos.filter { it.activo }
    }
}

fun main() {
    val crud = CRUD()
    val scanner = Scanner(System.`in`)

    while (true) {
        println("\n=== MENÚ PRINCIPAL ===")
        println("1. Crear campo petrolero")
        println("2. Leer campos petroleros")
        println("3. Actualizar campo petrolero")
        println("4. Eliminar campo petrolero")
        println("5. Agregar pozo a campo")
        println("6. Leer pozos de un campo")
        println("7. Actualizar pozo")
        println("8. Eliminar pozo")
        println("9. Ver pozos activos")
        println("10. Salir")
        print("Seleccione una opción: ")

        when (scanner.nextInt()) {
            1 -> {
                print("Nombre del campo: ")
                val nombre = readLine()!!
                print("País de origen: ")
                val paisOrigen = readLine()!!
                crud.createCampo(nombre, paisOrigen)
            }
            2 -> {
                println("=== Campos disponibles ===")
                crud.readCampos().forEach { campo ->
                    println("------------------------------------------------")
                    println("Nombre: ${campo.nombre}")
                    println("Código: ${campo.codigo}") // Mostrar el código
                    println("Fecha de instalación: ${campo.fechaInstalacion}")
                    println("País de origen: ${campo.paisOrigen}")
                    println("Número de pozos: ${campo.numeroPozos}")
                    println("------------------------------------------------")
                }
            }
            3 -> {
                print("Nombre del campo a actualizar: ")
                val nombre = readLine()!!
                print("Nuevo nombre (dejar vacío para no cambiar): ")
                val nuevoNombre = readLine()
                print("Nuevo país de origen (dejar vacío para no cambiar): ")
                val paisOrigen = readLine()
                crud.updateCampo(nombre, nuevoNombre?.takeIf { it.isNotBlank() }, paisOrigen?.takeIf { it.isNotBlank() })
            }
            4 -> {
                print("Nombre del campo a eliminar: ")
                val nombre = readLine()!!
                crud.deleteCampo(nombre)
            }
            5 -> {
                print("Nombre del campo: ")
                val campoNombre = readLine()!!
                print("Nombre del pozo: ")
                val nombre = readLine()!!
                print("Capacidad (en barriles): ")
                val capacidad = readLine()?.toDoubleOrNull() ?: 0.0
                print("Ubicación: ")
                val ubicacion = readLine()!!
                print("¿Está activo? (true/false): ")
                val activo = readLine()?.toBoolean() ?: false
                print("Run Life del pozo (en días): ")
                val runLife = scanner.nextInt()
                crud.createPozo(campoNombre, nombre, capacidad, ubicacion, activo, runLife)
            }
            6 -> {
                print("Nombre del campo: ")
                val campoNombre = readLine()!!
                println("=== Pozos del campo '$campoNombre' ===")
                crud.readPozos(campoNombre).forEach { pozo ->
                    println("------------------------------------------------")
                    println("Nombre del pozo: ${pozo.nombre}")
                    println("Capacidad: ${pozo.capacidad} barriles")
                    println("Ubicación: ${pozo.ubicacion}")
                    println("Activo: ${if (pozo.activo) "Sí" else "No"}")
                    println("RunLife: ${pozo.runLife} días")
                    println("------------------------------------------------")
                }
            }
            7 -> {
                print("Nombre del pozo a actualizar: ")
                val nombre = readLine()!!
                print("Nuevo nombre (dejar vacío para no cambiar): ")
                val nuevoNombre = readLine()
                print("Nueva capacidad (dejar vacío para no cambiar): ")
                val capacidad = readLine()?.toDoubleOrNull()
                print("Nueva ubicación (dejar vacío para no cambiar): ")
                val ubicacion = readLine()
                print("¿Está activo? (true/false, dejar vacío para no cambiar): ")
                val activo = readLine()?.toBoolean()
                print("Nuevo Run Life (dejar vacío para no cambiar): ")
                val runLife = readLine()?.toIntOrNull()
                crud.updatePozo(nombre, nuevoNombre?.takeIf { it.isNotBlank() }, capacidad, ubicacion?.takeIf { it.isNotBlank() }, activo, runLife)
            }
            8 -> {
                print("Nombre del pozo a eliminar: ")
                val nombre = readLine()!!
                crud.deletePozo(nombre)
            }
            9 -> {
                println("=== Pozos activos ===")
                val pozosActivos = crud.readPozosActivos()
                if (pozosActivos.isEmpty()) {
                    println("No hay pozos activos.")
                } else {
                    pozosActivos.forEach { pozo ->
                        val campoNombre = crud.findCampoNameByPozo(pozo)
                        println("------------------------------------------------")
                        println("Pozo: ${pozo.nombre}")
                        println("Campo: $campoNombre")
                        println("Capacidad: ${pozo.capacidad} barriles")
                        println("Ubicación: ${pozo.ubicacion}")
                        println("Activo: ${if (pozo.activo) "Sí" else "No"}")
                        println("RunLife: ${pozo.runLife} días")
                        println("------------------------------------------------")
                    }
                }
            }
            10 -> {
                println("¡Hasta luego!")
                return
            }
            else -> println("Opción no válida.")
        }
    }
}
