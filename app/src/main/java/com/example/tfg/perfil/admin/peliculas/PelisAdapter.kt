package com.example.tfg.perfil.admin.peliculas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg.R
import com.example.tfg.pelicula.Pelicula
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class PelisAdapter(private val miLista: ArrayList<Pelicula>, val c: Context):
    RecyclerView.Adapter<PelisAdapter.MiViewHolder>(), View.OnClickListener{

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    //Listener
    lateinit var listener: View.OnClickListener

    class MiViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val tvTitulo: TextView = v.findViewById(R.id.tvTituloPelicula)
        val tvFecha: TextView = v.findViewById(R.id.tvAñoPeli)
        val ivPelicula: ImageView = v.findViewById(R.id.ivPelicula)
        val tvSinopsis: TextView = v.findViewById(R.id.tvSinopsisPeli)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val v = inflate.inflate(R.layout.custom_grid_all_movie_layout, parent, false)
        v.setOnClickListener(this)
        return MiViewHolder(v)
    }

    override fun getItemCount(): Int {
        return miLista.size
    }

    override fun onBindViewHolder(holder: MiViewHolder, position: Int) {
        val item = miLista[position]
        holder.tvTitulo.text = item.titulo
        holder.tvFecha.text = item.fecha
        holder.tvSinopsis.text = item.sinopsis
        Picasso.get().load(item.caratula).into(holder.ivPelicula)

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