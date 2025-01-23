package com.example.gr2sw2024b_cara

class BEntrenador (
    var id: Int,
    var nombre: String,
    var descripcion: String?
    ) {
        override fun toString(): String {
            return "$nombre ${descripcion}"
        }
}