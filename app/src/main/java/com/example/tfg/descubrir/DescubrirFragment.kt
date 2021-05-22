package com.example.tfg.descubrir

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.tfg.R
import com.example.tfg.descubrir.busqueda.BusquedaFragment
import com.example.tfg.descubrir.recomendaciones.Recomendacion
import com.example.tfg.pelicula.DetailPeliculaActivity
import com.example.tfg.pelicula.Pelicula
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class DescubrirFragment : Fragment() {

    lateinit var btnBuscar: Button
    lateinit var recview: RecyclerView
    lateinit var mDatabase: DatabaseReference
    lateinit var tvTitulo: TextView
    lateinit var tvFecha: TextView
    lateinit var imagen: ImageView
    lateinit var reseña: TextView
    lateinit var emoji: TextView
    lateinit var FirebaseRecyclerAdapter: FirebaseRecyclerAdapter<Recomendacion, RecomendacionViewHolder>
    var listaPelis = ArrayList<Pelicula>()
    private val db = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_descubrir, container, false)

        btnBuscar = root.findViewById(R.id.btnBus)
        recview = root.findViewById(R.id.rcRecomendaciones)
        mDatabase = FirebaseDatabase.getInstance().getReference("recomendaciones")

        btnBuscar.setOnClickListener {
            val busquedaFragment = BusquedaFragment()
            activity?.getSupportFragmentManager()?.beginTransaction()
                ?.setCustomAnimations(
                    R.anim.slide_bottom_up,
                    R.anim.slide_bottom_down
                )
                ?.replace(R.id.container, busquedaFragment)
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


        val options: FirebaseRecyclerOptions<Recomendacion> =
            FirebaseRecyclerOptions.Builder<Recomendacion>()
                .setQuery(query, Recomendacion::class.java)
                .build()

        FirebaseRecyclerAdapter =
            object : FirebaseRecyclerAdapter<Recomendacion, RecomendacionViewHolder>(options) {

                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): RecomendacionViewHolder {
                    val inflate = LayoutInflater.from(parent.context)
                    val v =
                        inflate.inflate(R.layout.custom_grid_recomendacion_layout, parent, false)
                    return RecomendacionViewHolder(v)
                }

                override fun onBindViewHolder(
                    holder: RecomendacionViewHolder,
                    position: Int,
                    model: Recomendacion
                ) {
                    holder.tvTitulo.text = model.titulo
                    holder.tvFecha.text = model.fecha
                    Picasso.get().load(model.caratula).into(holder.imagen)
                    holder.reseña.text = model.reseña
                    holder.emoji.text = model.emoticono
                    holder.nomusu.text = model.nomUsuario
                    Picasso.get().load(model.fotoUsuario).into(holder.imUsu)

                    holder.itemView.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(v: View?) {
                            db.collection("categorias").document(model.categoria.toString())
                                .collection("peliculas").document(model.titulo.toString()).get().addOnSuccessListener {
                                    val i: Intent = Intent(context as Context, DetailPeliculaActivity::class.java)
                                    var bundle: Bundle = Bundle()
                                    i.putExtra("titulo", it.getString("titulo"))
                                    i.putExtra("fecha", it.getString("fecha"))
                                    i.putExtra("duracion", it.getString("duracion"))
                                    i.putExtra("categoria", it.getString("categoria"))
                                    i.putExtra("sinopsis", it.getString("sinopsis"))
                                    i.putExtra("caratula", it.getString("caratula"))
                                    startActivity(i)
                                }
                        }

                    })
                }


            }

        recview.setHasFixedSize(true)
        var manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        recview.layoutManager = manager
        recview.adapter = FirebaseRecyclerAdapter

    }


    class RecomendacionViewHolder(var v: View) : RecyclerView.ViewHolder(v) {
        val tvTitulo: TextView = v.findViewById(R.id.tvMovRec)
        val tvFecha: TextView = v.findViewById(R.id.tvFechaRec1)
        val imagen: ImageView = v.findViewById(R.id.ivMovRec)
        val reseña: TextView = v.findViewById(R.id.tvReseRec)
        val emoji: TextView = v.findViewById(R.id.tvEmojiRec)
        val nomusu: TextView = v.findViewById(R.id.tvNomUsu)
        val imUsu: ImageView = v.findViewById(R.id.ivUsuPerfil)
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