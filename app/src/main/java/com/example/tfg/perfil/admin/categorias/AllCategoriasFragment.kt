package com.example.tfg.perfil.admin.categorias

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.example.tfg.R
import com.example.tfg.casa.Categoria
import com.example.tfg.casa.CategoriaAdapter
import com.example.tfg.pelicula.Pelicula
import com.example.tfg.pelicula.PelisPorCatFragment
import com.example.tfg.perfil.admin.peliculas.PelisAdapter
import com.google.firebase.firestore.FirebaseFirestore

class AllCategoriasFragment : Fragment() {

    lateinit var recview: RecyclerView

    private val db = FirebaseFirestore.getInstance()

    lateinit var miAdapter: CatAdapter
    var listaCategoria = ArrayList<Categoria>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_all_categorias, container, false)

        recview = root.findViewById(R.id.rcTodasCat)

        listaCategoria.clear()

        rellenadoLista()

        return root
    }

    private fun rellenadoLista() {
        db.collection("categorias").get().addOnSuccessListener {
            for (doc in it){
                var cat = Categoria(
                    doc.getString("titulo").toString(),
                    doc.getString("logo").toString()
                )
                listaCategoria.add(cat)
            }
            crearAdapter()
        }
    }


    private fun crearAdapter() {
        recview.setHasFixedSize(true)
        miAdapter = CatAdapter(listaCategoria,context as Context)
        recview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recview.adapter = miAdapter

        miAdapter.setOnClickListener(View.OnClickListener {
            var titulo = listaCategoria[recview.getChildAdapterPosition(it)].titulo
            var emoji = listaCategoria[recview.getChildAdapterPosition(it)].logo

            val editCat = EditCatFragment()

            var bundle: Bundle = Bundle()
            bundle.putString("titulo", titulo)
            bundle.putString("emoji", emoji)
            editCat.arguments = bundle

            activity?.getSupportFragmentManager()?.beginTransaction()
                ?.setCustomAnimations(
                    R.anim.slide_bottom_up,
                    R.anim.slide_bottom_down
                )
                ?.replace(R.id.container,editCat)
                ?.addToBackStack(null)
                ?.commit();
        })

    }

}