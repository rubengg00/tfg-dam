package com.example.tfg.descubrir.busqueda

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg.R
import com.example.tfg.pelicula.DetailPeliculaActivity
import com.example.tfg.pelicula.Pelicula
import com.example.tfg.pelicula.PeliculaAdapter
import com.google.firebase.firestore.FirebaseFirestore
import www.sanju.motiontoast.MotionToast

class BusquedaFragment : Fragment() {
    lateinit var search: EditText

    private val db = FirebaseFirestore.getInstance()

    lateinit var miAdapter: PeliculaAdapter
    var listaPelis = ArrayList<Pelicula>()

    lateinit var recview: RecyclerView
    var flag = false


    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_busqueda, container, false)

        search = root.findViewById(R.id.search)
        recview = root.findViewById(R.id.rcBusqueda)

        if (!flag) {
            flag = true
            cargadoDatos("")
        }
        crearAdapter()


        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s.toString() != null) {
                    val texto = s.toString().capitalize()
                    cargadoDatos(texto)
                    recview.adapter!!.notifyDataSetChanged()
                }
                if (s.toString().isEmpty()){
                    listaPelis.clear()
                    cargadoDatos("")
                    recview.adapter!!.notifyDataSetChanged()
                }
            }

        })

        return root
    }


    fun cargadoDatos(cadena: String) {
        var titulo = ""
        var fecha = ""
        var sinopsis = ""
        var duracion = ""
        var categoria = ""
        var caratula = ""
        var platNom = ""
        var enlace = ""

        listaPelis.clear()

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
                                    enlace
                                )
                                listaPelis.add(peli)
                            }
                            crearAdapter()
                        }
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