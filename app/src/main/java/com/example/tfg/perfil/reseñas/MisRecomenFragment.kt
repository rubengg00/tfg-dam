package com.example.tfg.perfil.reseñas

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.example.tfg.R
import com.example.tfg.descubrir.recomendaciones.Recomendacion
import com.example.tfg.pelicula.DetailPeliculaActivity
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import www.sanju.motiontoast.MotionToast


class MisRecomenFragment : Fragment() {

    lateinit var recview: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private val nodoRaiz = FirebaseDatabase.getInstance()
    lateinit var FirebaseRecyclerAdapter: FirebaseRecyclerAdapter<Recomendacion, RecomendacionViewHolder>
    var nickname = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_mis_recomen, container, false)

        recview = root.findViewById(R.id.rcReseñas)

        var datos: Bundle? = this.arguments

        nickname = datos?.getString("nickname").toString()

        cargarRecyclerView(nickname)

        return root
    }

    //-----------------------------------------------------------------------------------------------
    private fun cargarRecyclerView(nickname: String) {

        var query: Query =
            FirebaseDatabase.getInstance().getReference("recomendaciones")
                .orderByChild("nomUsuario").equalTo(nickname)

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
                            R.layout.custom_grid_recomendacion_list_layout,
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

                    holder.itemView.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(v: View?) {
                            db.collection("categorias").document(model.categoria.toString())
                                .collection("peliculas").document(model.titulo.toString()).get()
                                .addOnSuccessListener {
                                    val i: Intent = Intent(
                                        context as Context,
                                        DetailPeliculaActivity::class.java
                                    )
                                    var bundle: Bundle = Bundle()
                                    i.putExtra("titulo", it.getString("titulo"))
                                    i.putExtra("fecha", it.getString("fecha"))
                                    i.putExtra("duracion", it.getString("duracion"))
                                    i.putExtra("categoria", it.getString("categoria"))
                                    i.putExtra("sinopsis", it.getString("sinopsis"))
                                    i.putExtra("caratula", it.getString("caratula"))
                                    i.putExtra("trailer", it.getString("trailer"))
                                    startActivity(i)
                                }
                        }

                    })

                    holder.tvOpciones.setOnClickListener { v ->
                        //Creamos el menu Popup
                        var popupMenu: PopupMenu = PopupMenu(context, holder.tvOpciones)
                        //Inflamos el menu con el recurso XML
                        popupMenu.inflate(R.menu.options_menu1)
                        //Añadimos un evento Click
                        popupMenu.setOnMenuItemClickListener { item ->
                            when (item?.itemId) {
                                R.id.menu -> {
                                    dialogoEditarReseña(holder)
                                }
                                R.id.menu2 -> {
                                    dialogoEliminarReseña(holder)
                                }
                            }
                            false
                        }
                        popupMenu.show()
                    }

                }
            }

        recview.setHasFixedSize(true)
        var manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        recview.layoutManager = manager
        recview.adapter = FirebaseRecyclerAdapter
    }

    private fun dialogoEliminarReseña(holder: RecomendacionViewHolder) {
        MaterialDialog(context as Context).show {
            title(null, "Eliminar reseña")
            message(
                null,
                "¿Deseas eliminar esta reseña?"
            )
            negativeButton(R.string.opcion_positivia) {
                var clave = ""
                var query: Query =
                    FirebaseDatabase.getInstance().getReference("recomendaciones")
                        .orderByChild("reseña").equalTo(holder.reseña.text.toString())
                query.addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (resultado in snapshot.getChildren()) {
                            clave = resultado.key.toString()
//                            resultado.ref.removeValue()
                            nodoRaiz.reference.child("recomendaciones")
                        }
                    }
                })

                getView()?.let { it1 ->
                    Snackbar.make(
                        it1,
                        "¡Reseña borrada!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
            positiveButton(R.string.opcion_negativa) { dialog ->
                {
                    dialog.dismiss()
                }
            }
        }
    }

    private fun dialogoEditarReseña(holder: RecomendacionViewHolder) {
        MaterialDialog(context as Context).title(null, "Editando reseña").show() {
            input(maxLength = 60)
            val inputField: EditText = this.getInputField()
            inputField.setText(holder.reseña.text)
            positiveButton(R.string.modificar) {
                if (inputField.text.isNullOrBlank()) {
                    MotionToast.darkToast(
                        activity as Activity,
                        "Error",
                        "Debes escribir una reseña",
                        MotionToast.TOAST_ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(context as Context, R.font.helvetica_regular)
                    )
                } else {
                    var clave = ""
                    var query: Query =
                        FirebaseDatabase.getInstance().getReference("recomendaciones")
                            .orderByChild("reseña").equalTo(holder.reseña.text.toString())
                    query.addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (resultado in snapshot.getChildren()) {
                                clave = resultado.key.toString()
                            }
                            var update = hashMapOf<String, Any>(
                                "reseña" to inputField.text.toString()
                            )
                            nodoRaiz.reference.child("recomendaciones/$clave/reseña")
                                .setValue(inputField.text.toString())
                            Log.d("clave", clave)
                        }


                    })

                    MotionToast.darkToast(
                        activity as Activity,
                        "Reseña actualizada 👍",
                        "Reseña actualizada correctamente!",
                        MotionToast.TOAST_SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(context as Context, R.font.helvetica_regular)
                    )
                }
            }
        }
    }

    class RecomendacionViewHolder(var v: View) : RecyclerView.ViewHolder(v) {
        val tvTitulo: TextView = v.findViewById(R.id.tvMovRec)
        val tvFecha: TextView = v.findViewById(R.id.tvFechaRec)
        val imagen: ImageView = v.findViewById(R.id.ivMovRec)
        val reseña: TextView = v.findViewById(R.id.tvReseRec)
        val emoji: TextView = v.findViewById(R.id.tvEmojiRec)
        val nomusu: TextView = v.findViewById(R.id.tvNomUsu)
        val imUsu: ImageView = v.findViewById(R.id.ivUsuPerfil)
        val tvOpciones: TextView = v.findViewById(R.id.tvOpciones1)
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