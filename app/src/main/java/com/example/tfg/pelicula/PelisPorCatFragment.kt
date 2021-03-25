package com.example.tfg.pelicula

import android.app.Activity
import android.app.ActivityOptions
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
import com.example.tfg.R
import com.google.firebase.firestore.FirebaseFirestore
import www.sanju.motiontoast.MotionToast


class PelisPorCatFragment : Fragment() {

    lateinit var tvCat: TextView
    private val db = FirebaseFirestore.getInstance()

    lateinit var miAdapter: PeliculaAdapter
    var listaPelis = ArrayList<Pelicula>()

    lateinit var recview: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_pelis_por_cat, container, false)

        //Recogemos los datos del Bundle
        var datos: Bundle? = this.arguments

        //Declaración de variables
        tvCat = root.findViewById(R.id.tvCat)
        recview = root.findViewById(R.id.rcMovi)

        tvCat.text = datos?.getString("nombre")
        var nombreCat = tvCat.text.toString()

        crearAdapter()
        rellenadoDatos(nombreCat)

        return root
    }

    private fun crearAdapter() {
        recview.setHasFixedSize(true)
        miAdapter = PeliculaAdapter(listaPelis, context as Context)
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

    private fun rellenadoDatos(nombreCat: String?) {
        var titulo = ""
        var fecha = ""
        var sinopsis = ""
        var duracion = ""
        var categoria = ""
        var caratula = ""
        var platNom = ""
        var enlace = ""

        db.collection("categorias").document(nombreCat.toString()).collection("peliculas").get()
            .addOnSuccessListener {
                for (doc in it) {
                    titulo = doc.getString("titulo").toString()
                    fecha = doc.getString("fecha").toString()
                    sinopsis = doc.getString("sinopsis").toString()
                    duracion = doc.getString("duracion").toString()
                    categoria = doc.getString("categoria").toString()
                    caratula = doc.getString("caratula").toString()
                    db.collection("categorias").document(nombreCat.toString())
                        .collection("peliculas").document(doc.getString("titulo").toString())
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
                        categoria,
                        caratula,
                        platNom,
                        enlace
                    )
                    listaPelis.add(peli)
                }
                if (listaPelis.isEmpty()) {
                    MotionToast.createToast(
                        activity as Activity,
                        "No hay resultados!",
                        "No hay peliculas de " + nombreCat,
                        MotionToast.TOAST_WARNING,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(context as Context, R.font.helvetica_regular)
                    )
                }
                crearAdapter()
            }


        db.collection("categorias").get().addOnSuccessListener {
            for (doc in it) {
                val nombreCat = doc.getString("titulo").toString()
                Log.d("nombreCat", nombreCat)
                db.collection("categorias").document(nombreCat).collection("peliculas").get()
                    .addOnSuccessListener {
                        for (doc in it) {
                            Log.d("titulo", doc.getString("titulo").toString())
                        }
                    }
            }

        }

    }


}