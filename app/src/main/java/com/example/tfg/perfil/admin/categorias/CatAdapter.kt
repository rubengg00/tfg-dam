package com.example.tfg.perfil.admin.categorias

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg.R
import com.example.tfg.casa.Categoria
import com.example.tfg.pelicula.Pelicula
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class CatAdapter(private val miLista: ArrayList<Categoria>, val c: Context):
    RecyclerView.Adapter<CatAdapter.MiViewHolder>(), View.OnClickListener{

    //Listener
    lateinit var listener: View.OnClickListener

    class MiViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val tvNombre: TextView = v.findViewById(R.id.tvNombreCat)
        val tvEmoji: TextView = v.findViewById(R.id.tvEmojiCat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val v = inflate.inflate(R.layout.custom_grid_all_cat_layout, parent, false)
        v.setOnClickListener(this)
        return MiViewHolder(v)
    }

    override fun getItemCount(): Int {
        return miLista.size
    }

    override fun onBindViewHolder(holder: MiViewHolder, position: Int) {
        val item = miLista[position]
        holder.tvNombre.text = item.titulo
        holder.tvEmoji.text = item.logo
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