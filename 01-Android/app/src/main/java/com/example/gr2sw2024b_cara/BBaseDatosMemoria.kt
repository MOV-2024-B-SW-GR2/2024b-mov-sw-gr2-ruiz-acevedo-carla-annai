package com.example.gr2sw2024b_cara

class BBaseDatosMemoria {
    companion object{
        var arregloBEntrenador = arrayListOf<BEntrenador>()
        init{
            arregloBEntrenador.add(BEntrenador(1,"Charlie","c@c.com"))
            arregloBEntrenador.add(BEntrenador(2,"Azucar","a@a.com"))
            arregloBEntrenador.add(BEntrenador(3,"Alexei","l@l.com"))
        }
    }
}

