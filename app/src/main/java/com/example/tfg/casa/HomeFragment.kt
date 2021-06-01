package com.example.tfg.casa

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg.R
import com.example.tfg.pelicula.PelisPorCatFragment
import com.example.tfg.perfil.EditPerfilFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    lateinit var miAdapter: CategoriaAdapter
    var listaCategoria = ArrayList<Categoria>()

    lateinit var recview: RecyclerView
    lateinit var tvSaludos: TextView
    var flag = false

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        recview = root.findViewById(R.id.rvListas)
        tvSaludos = root.findViewById(R.id.tvSaludos)


        if (!flag){
            flag = true
            rellenadoLista()
        }
        crearAdapter()

        cargarSaludos()

        return root
    }

    private fun cargarSaludos() {
        val c: Calendar = Calendar.getInstance()
        //Obtenemos la hora del día
        val timeOfDay: Int = c.get(Calendar.HOUR_OF_DAY)
        //Obtenemos la referencia al usuario actual
        var usu = FirebaseAuth.getInstance().currentUser

        if(timeOfDay in 6..11){
            if (usu != null){
                tvSaludos.text = "¡Buenos días ${usu.displayName}!"
            }else{
                tvSaludos.text = "¡Buenos días!"
            }
        }else if(timeOfDay in 12..19){
            if (usu != null){
                tvSaludos.text = "¡Buenas tardes ${usu.displayName}!"
            }else{
                tvSaludos.text = "¡Buenas tardes!"
            }
        }else if(timeOfDay in 20..23){
            if (usu != null){
                tvSaludos.text = "¡Buenas noches ${usu.displayName}!"
            }else{
                tvSaludos.text = "¡Buenas noches!"
            }
        }else{
            if (usu != null){
                tvSaludos.text = "¡Buenas noches ${usu.displayName}!"
            }else{
                tvSaludos.text = "¡Buenas noches!"
            }
        }
    }

    private fun crearAdapter() {
        recview.setHasFixedSize(true)
        miAdapter = CategoriaAdapter(listaCategoria,context as Context)
        recview.layoutManager = GridLayoutManager(context as Context, 2, GridLayoutManager.VERTICAL, false)
        recview.adapter = miAdapter

        miAdapter.setOnClickListener(View.OnClickListener {
            val pelisPorCatFragment = PelisPorCatFragment()

            var bundle: Bundle = Bundle()
            bundle.putString("nombre", listaCategoria.get(recview.getChildAdapterPosition(it)).titulo)
            pelisPorCatFragment.arguments = bundle

            activity?.getSupportFragmentManager()?.beginTransaction()
                ?.setCustomAnimations(
                    R.anim.slide_bottom_up,
                    R.anim.slide_bottom_down
                )
                ?.replace(R.id.container,pelisPorCatFragment)
                ?.addToBackStack(null)
                ?.commit();
        })

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


}