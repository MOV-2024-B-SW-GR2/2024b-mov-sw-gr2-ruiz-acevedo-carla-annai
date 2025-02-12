package com.example.a04_deber.modelo

import android.os.Parcel
import android.os.Parcelable
import java.time.LocalDate

class CampoPetrolero(
    var id: Int, // Agregar el parámetro 'id'
    var nombre: String,
    var fechaInstalacion: LocalDate,
    var paisOrigen: String,
    var numeroPozos: Int,
    var codigo: String,
    var latitud: Double,  // Nueva propiedad
    var longitud: Double, // Nueva propiedad
    var pozos: MutableList<Pozo> = mutableListOf()
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(), // Leer el 'id' desde el Parcel
        parcel.readString() ?: "",
        LocalDate.parse(parcel.readString() ?: ""),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readDouble(), // Leer latitud
        parcel.readDouble(), // Leer longitud
        mutableListOf<Pozo>().apply {
            // Usamos readTypedList en lugar de readList
            parcel.readTypedList(this, Pozo.CREATOR)
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id) // Escribir el 'id' en el Parcel
        parcel.writeString(nombre)
        parcel.writeString(fechaInstalacion.toString())
        parcel.writeString(paisOrigen)
        parcel.writeInt(numeroPozos)
        parcel.writeString(codigo)
        parcel.writeDouble(latitud) // Escribir latitud
        parcel.writeDouble(longitud) // Escribir longitud
        parcel.writeList(pozos) // No hay cambio en esta parte
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CampoPetrolero> {
        override fun createFromParcel(parcel: Parcel): CampoPetrolero {
            return CampoPetrolero(parcel)
        }

        override fun newArray(size: Int): Array<CampoPetrolero?> {
            return arrayOfNulls(size)
        }
    }
}
