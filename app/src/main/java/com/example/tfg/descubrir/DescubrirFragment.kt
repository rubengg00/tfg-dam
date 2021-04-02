package com.example.tfg.descubrir

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg.R
import com.example.tfg.descubrir.busqueda.BusquedaFragment
import com.example.tfg.pelicula.PeliculaAdapter
import com.example.tfg.descubrir.recomendaciones.Recomendacion
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.custom_grid_recomendacion_layout.view.*


class DescubrirFragment : Fragment() {

    lateinit var btnBuscar : Button
    lateinit var recview: RecyclerView
    lateinit var mDatabase: DatabaseReference
    lateinit var tvTitulo: TextView
    lateinit var tvFecha: TextView
    lateinit var imagen: ImageView
    lateinit var reseña: TextView
    lateinit var emoji: TextView
    lateinit var FirebaseRecyclerAdapter: FirebaseRecyclerAdapter<Recomendacion, RecomendacionViewHolder>

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_descubrir, container, false)

        btnBuscar = root.findViewById(R.id.btnBus)
        recview = root.findViewById(R.id.rcRecomendaciones)
        mDatabase = FirebaseDatabase.getInstance().getReference("recomendaciones")
//        tvTitulo = root.findViewById(R.id.tvMovRec)
//        tvFecha = root.findViewById(R.id.tvFechaRec)
//        imagen = root.findViewById(R.id.ivMovRec)
//        reseña = root.findViewById(R.id.tvReseRec)
//        emoji = root.findViewById(R.id.tvEmojiRec)

        btnBuscar.setOnClickListener {
            val busquedaFragment = BusquedaFragment()
            activity?.getSupportFragmentManager()?.beginTransaction()
                ?.setCustomAnimations(
                    R.anim.slide_bottom_up,
                    R.anim.slide_bottom_down
                )
                ?.replace(R.id.container,busquedaFragment)
                ?.addToBackStack(null)
                ?.commit();
        }

        cargarRecyclerView()
        return root
    }

    private fun cargarRecyclerView() {

        val query: Query = FirebaseDatabase.getInstance()
            .reference
            .child("recomendaciones")


        val options: FirebaseRecyclerOptions<Recomendacion> = FirebaseRecyclerOptions.Builder<Recomendacion>()
            .setQuery(query, Recomendacion::class.java)
            .build()

        FirebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Recomendacion, RecomendacionViewHolder>(options){

            override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): RecomendacionViewHolder {
                val inflate = LayoutInflater.from(parent.context)
                val v = inflate.inflate(R.layout.custom_grid_recomendacion_layout, parent, false)
                return RecomendacionViewHolder(v)
            }

            override fun onBindViewHolder(holder: RecomendacionViewHolder,position: Int,model: Recomendacion) {
                holder.tvTitulo.text = model.titulo
                holder.tvFecha.text = model.fecha
                Picasso.get().load(model.caratula).into(holder.imagen)
                holder.reseña.text = model.reseña
                holder.emoji.text = model.emoticono
            }

        }

        recview.setHasFixedSize(true)
        recview.layoutManager =
            GridLayoutManager(context as Context, 2, GridLayoutManager.VERTICAL, false)
        recview.adapter = FirebaseRecyclerAdapter
    }


    class RecomendacionViewHolder(var v: View): RecyclerView.ViewHolder(v){
        val tvTitulo: TextView = v.findViewById(R.id.tvMovRec)
        val tvFecha: TextView = v.findViewById(R.id.tvFechaRec)
        val imagen: ImageView = v.findViewById(R.id.ivMovRec)
        val reseña: TextView = v.findViewById(R.id.tvReseRec)
        val emoji: TextView = v.findViewById(R.id.tvEmojiRec)
    }

    override fun onStart() {
        super.onStart()
        FirebaseRecyclerAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        FirebaseRecyclerAdapter.stopListening()
    }

}