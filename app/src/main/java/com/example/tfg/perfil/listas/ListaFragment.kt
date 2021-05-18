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
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.tfg.R
import com.example.tfg.pelicula.DetailPeliculaActivity
import com.example.tfg.pelicula.Pelicula
import com.example.tfg.perfil.PerfilFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import www.sanju.motiontoast.MotionToast

class ListaFragment : Fragment() {


    lateinit var tvNombre: TextView
    lateinit var tvDelete: TextView
    private val db = FirebaseFirestore.getInstance()

    lateinit var miAdapter: PeliculaListaAdapter
    var listaPelis = ArrayList<Pelicula>()

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
        var nombre = datos?.getString("nombre")
        tvNombre.text = nombre

        crearAdapter()
        buscadoPelis(nombre)

        tvDelete.setOnClickListener {
            MaterialDialog(context as Context)
                .show {
                    title(null, "Borrar lista")
                    message(null, "¿Deseas borrar la lista?")
                    negativeButton(R.string.opcion_positivia) { dialog ->
                        db.collection("usuarios")
                            .document(FirebaseAuth.getInstance().currentUser.email)
                            .collection("listas").document(tvNombre.text.toString()).delete()

                        MotionToast.darkToast(activity as Activity,
                            "Lista borrada 👍",
                            "Lista borrada correctamente!",
                            MotionToast.TOAST_SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(context as Context,R.font.helvetica_regular))

                        val perfilFragment = PerfilFragment()
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.container,perfilFragment)
                            ?.addToBackStack(null)
                            ?.commit();
                    }
                    positiveButton(R.string.opcion_negativa) { dialog ->
                        {
                            dialog.dismiss()
                        }
                    }

                }
        }

        return root
    }

    fun buscadoPelis(nombre: String?) {
        if (nombre != null) {
            db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email)
                .collection("listas").document(nombre).collection("peliculas").get()
                .addOnSuccessListener {
                    for (doc in it) {
                        var categoria = doc.getString("categoria")
                        var nombrePeli = doc.getString("titulo")
                        Log.d("Pelicula", doc.getString("titulo").toString())
                        if (nombrePeli != null) {
                            buscadoCaratula(nombrePeli, categoria)
                        }
                    }
                }
        }
    }

    fun buscadoCaratula(nombre: String, categoria: String?) {
        var titulo = ""
        var fecha = ""
        var sinopsis = ""
        var duracion = ""
        var cat = ""
        var platNom = ""
        var enlace = ""
        var caratula = ""
        var trailer = ""

        if (categoria != null) {
            db.collection("categorias").document(categoria).collection("peliculas").document(nombre)
                .get().addOnSuccessListener {
                    titulo = nombre
                    fecha = it.getString("fecha").toString()
                    sinopsis = it.getString("sinopsis").toString()
                    duracion = it.getString("duracion").toString()
                    cat = categoria
                    caratula = it.getString("caratula").toString()
                    trailer = it.getString("trailer").toString()
                    db.collection("categorias").document(categoria)
                        .collection("peliculas").document(nombre)
                        .collection("plataformas").get().addOnSuccessListener {
                            for (doc in it) {
                                platNom = doc.getString("nombre").toString()
                                enlace = doc.getString("enlace").toString()
                            }
                        }
                    var peli = Pelicula(
                        titulo,
                        fecha,
                        sinopsis,
                        duracion,
                        cat,
                        caratula,
                        platNom,
                        enlace,
                        trailer
                    )
                    listaPelis.add(peli)
                    crearAdapter()
                }
        }
    }

    private fun crearAdapter() {
        recview.setHasFixedSize(true)
        miAdapter = PeliculaListaAdapter(listaPelis, context as Context, tvNombre.text.toString())
        recview.layoutManager =
            GridLayoutManager(context as Context, 2, GridLayoutManager.VERTICAL, false)
        recview.adapter = miAdapter

        miAdapter.setOnClickListener(View.OnClickListener {
            val i: Intent = Intent(context as Context, DetailPeliculaActivity::class.java)
            var bundle: Bundle = Bundle()
            i.putExtra("titulo", listaPelis.get(recview.getChildAdapterPosition(it)).titulo)
            i.putExtra("fecha", listaPelis.get(recview.getChildAdapterPosition(it)).fecha)
            i.putExtra("duracion", listaPelis.get(recview.getChildAdapterPosition(it)).duracion)
            i.putExtra("categoria", listaPelis.get(recview.getChildAdapterPosition(it)).categoria)
            i.putExtra("sinopsis", listaPelis.get(recview.getChildAdapterPosition(it)).sinopsis)
            i.putExtra("caratula", listaPelis.get(recview.getChildAdapterPosition(it)).caratula)
            startActivity(i)
        })
    }


}