package com.example.tfg.casa

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg.R
import com.example.tfg.casa.categoria.PelisPorCatFragment
import com.example.tfg.perfil.EditPerfilFragment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

    lateinit var miAdapter: CategoriaAdapter
    var listaCategoria = ArrayList<Categoria>()

    lateinit var recview: RecyclerView
    var flag = false

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        recview = root.findViewById(R.id.rvListas)

        EditPerfilFragment().comprobarTema(activity as Activity, context as Context)

        if (!flag){
            flag = true
            rellenadoLista()
        }
        crearAdapter()


        return root
    }

    private fun crearAdapter() {
        recview.setHasFixedSize(true)
        miAdapter = CategoriaAdapter(listaCategoria,context as Context)
        recview.layoutManager = GridLayoutManager(context as Context, 2, GridLayoutManager.VERTICAL, false)
        recview.adapter = miAdapter

        miAdapter.setOnClickListener(View.OnClickListener {
            Toast.makeText(context as Context, listaCategoria.get(recview.getChildAdapterPosition(it)).titulo, Toast.LENGTH_SHORT).show()
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