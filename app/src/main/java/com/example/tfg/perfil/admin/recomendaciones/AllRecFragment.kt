package com.example.tfg.perfil.admin.recomendaciones

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.tfg.R
import com.example.tfg.descubrir.recomendaciones.Recomendacion
import com.example.tfg.pelicula.DetailPeliculaActivity
import com.example.tfg.perfil.admin.categorias.EditCatFragment
import com.example.tfg.perfil.reseñas.MisRecomenFragment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class AllRecFragment : Fragment() {

    lateinit var recview: RecyclerView

    private val nodoRaiz = FirebaseDatabase.getInstance()
    lateinit var FirebaseRecyclerAdapter: FirebaseRecyclerAdapter<Recomendacion, RecomendacionViewHolder>
    private val db = FirebaseFirestore.getInstance()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_all_rec, container, false)

        recview = root.findViewById(R.id.rcTodasReseñas)

        cargarRecyclerView()

        return root
    }

    //-----------------------------------------------------------------------------------------------
    private fun cargarRecyclerView() {

        var query: Query =
            FirebaseDatabase.getInstance().getReference("recomendaciones")
                .orderByChild("nomUsuario")

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
                        inflate.inflate(
                            R.layout.custom_grid_recomendacion_layout,
                            parent,
                            false
                        )
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
                    holder.tvCat.text = model.categoria
                    holder.tvFechaSubida.text = model.fechaSubida

                    holder.itemView.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(v: View?) {
                            var titulo = holder.tvTitulo.text.toString()
                            var fecha = holder.tvFecha.text.toString()
                            var categoria = holder.tvCat.text.toString()
                            var resena = holder.reseña.text.toString()
                            var nomusu = holder.nomusu.text.toString()
                            var subida = holder.tvFechaSubida.text.toString()

                            val editRec = EditRecFragment()

                            var bundle: Bundle = Bundle()
                            bundle.putString("titulo", titulo)
                            bundle.putString("fecha", fecha)
                            bundle.putString("categoria", categoria)
                            bundle.putString("reseña", resena)
                            bundle.putString("nomusu", nomusu)
                            bundle.putString("subida", subida)
                            editRec.arguments = bundle

                            activity?.getSupportFragmentManager()?.beginTransaction()
                                ?.setCustomAnimations(
                                    R.anim.slide_bottom_up,
                                    R.anim.slide_bottom_down
                                )
                                ?.replace(R.id.container,editRec)
                                ?.addToBackStack(null)
                                ?.commit();
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
        val tvCat: TextView = v.findViewById(R.id.tvCatRese)
        val tvFechaSubida: TextView = v.findViewById(R.id.tvCatSubFecha)
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