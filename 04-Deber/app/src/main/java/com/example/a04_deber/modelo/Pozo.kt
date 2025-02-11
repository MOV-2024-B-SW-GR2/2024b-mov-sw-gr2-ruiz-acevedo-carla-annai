package com.example.a04_deber.modelo

import android.os.Parcel
import android.os.Parcelable

class Pozo(
    var id: Int = 0,
    var nombre: String,
    var capacidad: Double,
    var ubicacion: String,
    var activo: Boolean,
    var runLife: Int,
    var campoPetroleroId: Int // Agregar el campo campoPetroleroId
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),  // Leer el id
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),  // Convertir byte a booleano
        parcel.readInt(),
        parcel.readInt()  // Leer el campoPetroleroId
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)  // Escribir el id
        parcel.writeString(nombre)
        parcel.writeDouble(capacidad)
        parcel.writeString(ubicacion)
        parcel.writeByte(if (activo) 1 else 0)  // Convertir booleano a byte
        parcel.writeInt(runLife)
        parcel.writeInt(campoPetroleroId)  // Escribir el campoPetroleroId
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Pozo> {
        override fun createFromParcel(parcel: Parcel): Pozo {
            return Pozo(parcel)
        }

        override fun newArray(size: Int): Array<Pozo?> {
            return arrayOfNulls(size)
        }
    }
}
