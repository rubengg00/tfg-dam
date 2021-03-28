package com.example.tfg.perfil.listas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg.R
import com.example.tfg.pelicula.Pelicula
import com.example.tfg.pelicula.PeliculaAdapter
import com.squareup.picasso.Picasso

class ListaAdapter (private val miLista: ArrayList<Lista>, val c: Context):
    RecyclerView.Adapter<ListaAdapter.MiViewHolder>(), View.OnClickListener{

    //Listener
    lateinit var listener: View.OnClickListener

    class MiViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val tvNombreLista: TextView = v.findViewById(R.id.tvNombreLista)
        val tvTotalPelis: TextView = v.findViewById(R.id.tvTotalPelis)
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): ListaAdapter.MiViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val v = inflate.inflate(R.layout.custom_listas_layout, parent, false)
        v.setOnClickListener(this)
        return ListaAdapter.MiViewHolder(v)
    }

    override fun getItemCount(): Int {
        return miLista.size
    }

    override fun onBindViewHolder(holder: MiViewHolder, position: Int) {
        val item = miLista[position]
        holder.tvNombreLista.text = item.nombre
        holder.tvTotalPelis.text = item.total
    }

    fun setOnClickListener(listener: View.OnClickListener){
        this.listener = listener
    }


    override fun onClick(v: View?) {
        if (listener != null){
            listener.onClick(v)
        }
    }


}