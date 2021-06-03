package com.example.tfg.descubrir.busqueda

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg.R
import com.example.tfg.pelicula.DetailPeliculaActivity
import com.example.tfg.pelicula.Pelicula
import com.example.tfg.pelicula.PeliculaAdapter
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class BusquedaFragment : Fragment() {
    lateinit var search: EditText
    lateinit var btnBusqueda: Button

    private val db = FirebaseFirestore.getInstance()

    lateinit var miAdapter: PeliculaAdapter
    var listaPelis = ArrayList<Pelicula>()

    lateinit var recview: RecyclerView
    var flag = false


    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_busqueda, container, false)

        search = root.findViewById(R.id.search)
        recview = root.findViewById(R.id.rcBusqueda)
        btnBusqueda = root.findViewById(R.id.btnBusqueda)

        if (!flag) {
            flag = true
            cargadoDatos("")
        }
        crearAdapter()

        btnBusqueda.setOnClickListener {
            if (!(search.text.trim().toString().isNullOrBlank())){
                val texto = search.text.toString().capitalize().trim()
                cargadoDatos(texto)
            }else{
                cargadoDatos("")
            }
        }

        search.addTextChangedListener(object : TextWatcher {
            private var temporizador: Timer = Timer()
            private val delay: Long = 450

            override fun afterTextChanged(s: Editable) {
                if (search.text.trim().toString().isNullOrBlank()){
                    cargadoDatos("")
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }


        })
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
        miAdapter = PeliculaAdapter(listaPelis, context as Context)
        recview.layoutManager =
            GridLayoutManager(context as Context, 2, GridLayoutManager.VERTICAL, false)
        recview.adapter = miAdapter
        recview.adapter?.notifyDataSetChanged()

        miAdapter.setOnClickListener(View.OnClickListener {
            val i: Intent = Intent(context as Context, DetailPeliculaActivity::class.java)
            var bundle: Bundle = Bundle()
            i.putExtra("titulo", listaPelis.get(recview.getChildAdapterPosition(it)).titulo)
            i.putExtra("fecha", listaPelis.get(recview.getChildAdapterPosition(it)).fecha)
            i.putExtra("duracion", listaPelis.get(recview.getChildAdapterPosition(it)).duracion)
            i.putExtra("categoria", listaPelis.get(recview.getChildAdapterPosition(it)).categoria)
            i.putExtra("sinopsis", listaPelis.get(recview.getChildAdapterPosition(it)).sinopsis)
            i.putExtra("caratula", listaPelis.get(recview.getChildAdapterPosition(it)).caratula)
            i.putExtra("busqueda", "busqueda")
            startActivity(i)
        })
    }

}