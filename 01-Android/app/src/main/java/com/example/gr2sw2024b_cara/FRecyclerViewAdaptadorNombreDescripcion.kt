package com.example.gr2sw2024b_cara

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FRecyclerViewAdaptadorNombreDescripcion(
    private val contexto: FRecyclerView,
    private val lista: ArrayList<BEntrenador>,
    private val recyclerView: RecyclerView
): RecyclerView.Adapter<
        FRecyclerViewAdaptadorNombreDescripcion.MyViewHolder
        >() {
    inner class MyViewHolder(
        view: View
    ): RecyclerView.ViewHolder(view){
        val nombreTextView: TextView
        val descripcionTextView: TextView
        val likesTextView: TextView
        val accionBoton: Button
        var numeroLikes = 0
        init{
            nombreTextView = view.findViewById<TextView>(R.id.tv_nombre)
            descripcionTextView = view.findViewById<TextView>(R.id.tv_descripcion)
            likesTextView = view.findViewById<TextView>(R.id.tv_likes)
            accionBoton = view.findViewById<Button>(R.id.btn_dar_like)
            accionBoton.setOnClickListener { anadirLikes() }
        }
        fun anadirLikes(){
            numeroLikes = numeroLikes + 1
            likesTextView.text = numeroLikes.toString()
            contexto.aumentarTotalLikes()
        }
    }

    // Setear el layout que vamos a utilizar
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recycler_view_vista, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return lista.size
    }
    // Seteamos los datos para la iteracion
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val entrenadorActual = lista[position]
        holder.nombreTextView.text = entrenadorActual.nombre
        holder.descripcionTextView.text = entrenadorActual.descripcion
        holder.likesTextView.text = holder.numeroLikes.toString()
    }
}