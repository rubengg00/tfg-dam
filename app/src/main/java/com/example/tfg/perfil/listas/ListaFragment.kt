package com.example.tfg.perfil.listas

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.example.tfg.R
import com.example.tfg.pelicula.DetailPeliculaActivity
import com.example.tfg.pelicula.Pelicula
import com.example.tfg.perfil.PerfilFragment
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import www.sanju.motiontoast.MotionToast

class ListaFragment : Fragment() {


    lateinit var tvNombre: TextView
    lateinit var tvDelete: TextView
    private val db = FirebaseFirestore.getInstance()

    var listaPelis = ArrayList<Pelicula>()
    var nombre = ""

    lateinit var FirestoreRecyclerAdapter: FirestoreRecyclerAdapter<Pelicula, ListaViewHolder>


    lateinit var recview: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_lista, container, false)

        //Declaración de variables
        tvNombre = root.findViewById(R.id.tvNomLista)
        recview = root.findViewById(R.id.rcPelisLista)
        tvDelete = root.findViewById(R.id.tvDelLista)

        //Recogemos los datos del Bundle
        var datos: Bundle? = this.arguments
        nombre = datos?.getString("nombre").toString()
        tvNombre.text = nombre

        comprobarPeliculaDisponible()
        listaPelis.clear()


        tvDelete.setOnClickListener {
            limpiadoLista()
        }

        cargarRecyclerView()

        return root
    }

    private fun cargarRecyclerView() {

        val query: Query =
            db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email)
                .collection("listas").document(tvNombre.text.toString()).collection("peliculas")
                .orderBy("titulo")

        val options: FirestoreRecyclerOptions<Pelicula> =
            FirestoreRecyclerOptions.Builder<Pelicula>()
                .setQuery(query, Pelicula::class.java)
                .build()

        FirestoreRecyclerAdapter =
            object : FirestoreRecyclerAdapter<Pelicula, ListaViewHolder>(options) {

                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): ListaViewHolder {
                    val inflate = LayoutInflater.from(parent.context)
                    val v =
                        inflate.inflate(R.layout.custom_grid_movie_list_layout, parent, false)
                    return ListaViewHolder(v)
                }

                override fun onBindViewHolder(
                    holder: ListaViewHolder,
                    position: Int,
                    model: Pelicula
                ) {
                    holder.tvTitulo.text = model.titulo
                    holder.tvFecha.text = model.fecha
                    holder.tvCat.text = model.categoria
                    db.collection("categorias").document(holder.tvCat.text.toString())
                        .collection("peliculas")
                        .document(holder.tvTitulo.text.toString()).get().addOnSuccessListener {
                            Log.d("caratula", it.getString("caratula").toString())
                            Picasso.get().load(it.getString("caratula").toString())
                                .into(holder.ivCaratula)
                        }

                    holder.itemView.setOnClickListener((object : View.OnClickListener {
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
                    }))

                    holder.tvOpciones.setOnClickListener { v ->
                        //Creamos el menu Popup
                        var popupMenu: PopupMenu = PopupMenu(context, holder.tvOpciones)
                        //Inflamos el menu con el recurso XML
                        popupMenu.inflate(R.menu.options_menu)
                        //Añadimos un evento Click
                        popupMenu.setOnMenuItemClickListener { item ->
                            when (item?.itemId) {
                                R.id.menu1 -> {
                                    eliminacionPelicula(holder)
                                }
                            }
                            false
                        }
                        popupMenu.show()
                    }

                }

                private fun eliminacionPelicula(holder: ListaViewHolder) {
                    MaterialDialog(context!!).show {
                        title(null, "Eliminar de la lista")
                        message(
                            null,
                            "¿Deseas eliminar la película ${holder.tvTitulo.text} de la lista?"
                        )
                        negativeButton(R.string.opcion_positivia) { dialog ->
                            db.collection("usuarios")
                                .document(FirebaseAuth.getInstance().currentUser.email)
                                .collection("listas").document(tvNombre.text.toString())
                                .collection("peliculas")
                                .document(holder.tvTitulo.text.toString()).delete()

                            if (holder.itemView != null) {
                                Snackbar.make(
                                    holder.itemView,
                                    "Borrada de ${tvNombre.text}",
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
            }

        recview.setHasFixedSize(true)
        recview.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        recview.adapter = FirestoreRecyclerAdapter

    }


    class ListaViewHolder(var v: View) : RecyclerView.ViewHolder(v) {
        val tvTitulo: TextView = v.findViewById(R.id.tvTitMov)
        val tvFecha: TextView = v.findViewById(R.id.tvFechaMov)
        val ivCaratula: ImageView = v.findViewById(R.id.ivMov)
        val tvOpciones: TextView = v.findViewById(R.id.tvOpciones)
        val tvCat: TextView = v.findViewById(R.id.tvCatInv)
    }

    override fun onStart() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            super.onStart()
            FirestoreRecyclerAdapter.startListening()
        } else {
            super.onStart()
        }
    }

    override fun onStop() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            super.onStop()
            FirestoreRecyclerAdapter.stopListening()
        } else {
            super.onStop()
        }
    }


    private fun comprobarPeliculaDisponible() {

        db.collection("categorias").get().addOnSuccessListener {
            var cat = arrayListOf<String>()

            for (doc in it) {
                cat.add(doc.getString("titulo").toString())
            }

            db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email)
                .collection("listas").document(tvNombre.text.toString()).collection("peliculas")
                .get().addOnSuccessListener {
                    for (doc in it) {
                        if (!(cat.contains(doc.getString("categoria")))) {
                            db.collection("usuarios")
                                .document(FirebaseAuth.getInstance().currentUser.email)
                                .collection("listas").document(tvNombre.text.toString())
                                .collection("peliculas")
                                .document(doc.getString("titulo").toString()).delete()
                        } else {
                            Log.d("La categoria", " existe")
                        }
                    }
                }

        }

    }

    private fun limpiadoLista() {
        MaterialDialog(context as Context)
            .show {
                var titulo = ""
                title(null, "Limpiado de lista")
                message(null, "¿Deseas borrar todas las películas guardadas en esta lista?")
                negativeButton(R.string.opcion_positivia) { dialog ->
                    db.collection("usuarios")
                        .document(FirebaseAuth.getInstance().currentUser.email)
                        .collection("listas").document(tvNombre.text.toString())
                        .collection("peliculas").get().addOnSuccessListener {
                            for (doc in it){
                                titulo = doc.getString("titulo").toString()
                                db.collection("usuarios")
                                    .document(FirebaseAuth.getInstance().currentUser.email)
                                    .collection("listas").document(tvNombre.text.toString())
                                    .collection("peliculas").document(titulo).delete()
                            }
                        }

                    MotionToast.darkToast(
                        activity as Activity,
                        "Lista vaciada 👍",
                        "Lista vaciada correctamente!",
                        MotionToast.TOAST_SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(context as Context, R.font.helvetica_regular)
                    )

                }
                positiveButton(R.string.opcion_negativa) { dialog ->
                    {
                        dialog.dismiss()
                    }
                }

            }
    }

}