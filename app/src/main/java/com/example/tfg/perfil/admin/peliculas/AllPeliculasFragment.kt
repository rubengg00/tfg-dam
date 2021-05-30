package com.example.tfg.perfil.admin.peliculas

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg.R
import com.example.tfg.pelicula.DetailPeliculaActivity
import com.example.tfg.pelicula.Pelicula
import com.example.tfg.pelicula.PeliculaAdapter
import com.google.firebase.firestore.FirebaseFirestore

class AllPeliculasFragment : Fragment() {

    lateinit var recview: RecyclerView

    private val db = FirebaseFirestore.getInstance()

    lateinit var miAdapter: PelisAdapter
    var listaPelis = ArrayList<Pelicula>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_all_peliculas, container, false)

        recview = root.findViewById(R.id.rcTodasPelis)

        cargadoDatos("")

        return root
    }

    fun cargadoDatos(cadena: String) {

        listaPelis.clear()


        var titulo = ""
        var fecha = ""
        var sinopsis = ""
        var duracion = ""
        var categoria = ""
        var caratula = ""
        var platNom = ""
        var enlace = ""
        var trailer = ""


        db.collection("categorias").get().addOnSuccessListener {
            for (doc in it) {
                val nombreCat = doc.getString("titulo").toString()
                db.collection("categorias").document(nombreCat).collection("peliculas").get()
                    .addOnSuccessListener {
                        for (doc in it) {
                            if (doc.getString("titulo").toString().startsWith(cadena)) {
                                titulo = doc.getString("titulo").toString()
                                fecha = doc.getString("fecha").toString()
                                sinopsis = doc.getString("sinopsis").toString()
                                duracion = doc.getString("duracion").toString()
                                categoria = doc.getString("categoria").toString()
                                caratula = doc.getString("caratula").toString()
                                trailer = doc.getString("trailer").toString()
                                db.collection("categorias").document(nombreCat.toString())
                                    .collection("peliculas")
                                    .document(doc.getString("titulo").toString())
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
                                    enlace,
                                    trailer
                                )
                                listaPelis.add(peli)
                            }
                            Log.d("hola",doc.getString("titulo").toString())
                        }
                        crearAdapter()

                    }
            }
        }
    }

    private fun crearAdapter() {
        recview.setHasFixedSize(true)
        miAdapter = PelisAdapter(listaPelis, context as Context)
        recview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recview.adapter = miAdapter
        recview.adapter?.notifyDataSetChanged()

        miAdapter.setOnClickListener(View.OnClickListener {
            val editPelicula = EditPeliculaFragment()

            var bundle: Bundle = Bundle()
            bundle.putString("titulo", listaPelis.get(recview.getChildAdapterPosition(it)).titulo)
            bundle.putString("fecha", listaPelis.get(recview.getChildAdapterPosition(it)).fecha)
            bundle.putString("duracion", listaPelis.get(recview.getChildAdapterPosition(it)).duracion)
            bundle.putString("categoria", listaPelis.get(recview.getChildAdapterPosition(it)).categoria)
            bundle.putString("sinopsis", listaPelis.get(recview.getChildAdapterPosition(it)).sinopsis)
            bundle.putString("caratula", listaPelis.get(recview.getChildAdapterPosition(it)).caratula)
            bundle.putString("trailer", listaPelis.get(recview.getChildAdapterPosition(it)).trailer)

            editPelicula.arguments = bundle

            activity?.getSupportFragmentManager()?.beginTransaction()
                ?.setCustomAnimations(
                    R.anim.slide_bottom_up,
                    R.anim.slide_bottom_down
                )
                ?.replace(R.id.container,editPelicula)
                ?.addToBackStack(null)
                ?.commit();
        })
    }


}