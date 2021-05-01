package com.example.tfg.perfil

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg.R
import com.example.tfg.pelicula.Pelicula
import com.example.tfg.pelicula.PeliculaAdapter
import com.example.tfg.pelicula.PelisPorCatFragment
import com.example.tfg.perfil.listas.Lista
import com.example.tfg.perfil.listas.ListaAdapter
import com.example.tfg.perfil.listas.ListaFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_perfil.*

class PerfilFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    lateinit var numListas: TextView
    lateinit var recview: RecyclerView
    lateinit var miAdapter: ListaAdapter
    var listaListas = ArrayList<Lista>()

    var flag = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_perfil, container, false)

        /* Declaración de varibles */
        val btnEditPerfil: Button = root.findViewById(R.id.editPerfil)
        var fotoPerfil: ImageView = root.findViewById(R.id.userFoto)
        var nombreUser: TextView = root.findViewById(R.id.tvUserName)
        var tvBio: TextView = root.findViewById(R.id.tvBio)
        numListas = root.findViewById(R.id.tvNumListas)
        recview = root.findViewById(R.id.recListas)

        if (FirebaseAuth.getInstance().currentUser != null) {
            Picasso.get().load(FirebaseAuth.getInstance().currentUser.photoUrl).into(fotoPerfil)
            db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email).get().addOnSuccessListener {
                if (it.getString("nickname").toString() == ""){
                    nombreUser.text = FirebaseAuth.getInstance().currentUser.displayName
                }else{
                    nombreUser.text = it.getString("nickname").toString()
                }
            }
            establecerBio()
            establecerListas()
            crearAdapter()
            if (!flag){
                flag = true
                rellenadoDatos()
            }
        } else {
            nombreUser.visibility = View.GONE
            tvBio.visibility = View.GONE
        }

        btnEditPerfil.setOnClickListener { checkPerfil() }



        return root
    }

    //-----------------------------------------------------------------------------------------------
    /*
    * Función rellenadoDatos()
    *   Rellena el RecyclerView con las listas del usuario
    * */
    private fun rellenadoDatos() {

        db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email)
            .collection("listas").orderBy("nombre").get().addOnSuccessListener {
                for (doc in it) {
                    var titulo = doc.getString("nombre").toString()
                    db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email)
                        .collection("listas").document(titulo).collection("peliculas").get()
                        .addOnSuccessListener {
                            var contador = it.size().toString()
                            var lista = Lista(
                                titulo,
                                contador
                            )
                            añadidoLista(lista)
                        }
                }
            }

    }
    /*
    * Función añadidoLista(lista: Lista)
    *   Recibe el objeto de tipo Lista, y lo agrega a la lista, llamando luego a la función
    *   crearAdapter()
    * */
    private fun añadidoLista(lista: Lista) {
        listaListas.add(lista)
        crearAdapter()
    }


    //-----------------------------------------------------------------------------------------------

    /*
    * Función crearAdapter()
    *   Crear el adapter para el RecyclerView y establece el evento onClick sobre los elementos
    * */
    private fun crearAdapter() {
        recview.setHasFixedSize(true)
        miAdapter = ListaAdapter(listaListas, context as Context)
        recview.layoutManager = LinearLayoutManager(context as Context)
        recview.adapter = miAdapter

        miAdapter.setOnClickListener(View.OnClickListener {
            val listaFragment = ListaFragment()

            var bundle: Bundle = Bundle()
            bundle.putString("nombre", listaListas.get(recview.getChildAdapterPosition(it)).nombre)
            listaFragment.arguments = bundle

            activity?.getSupportFragmentManager()?.beginTransaction()
                ?.setCustomAnimations(
                    R.anim.slide_bottom_up,
                    R.anim.slide_bottom_down
                )
                ?.replace(R.id.container,listaFragment)
                ?.addToBackStack(null)
                ?.commit();
        })
    }

    /*
    * Función establecerListas()
    *   Esta función devuelve el total de listas del usuario
    * */
    private fun establecerListas() {
        db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email)
            .collection("listas").get().addOnSuccessListener {
                numListas.text = it.size().toString()
            }
    }

    /*
    * Función establecerBio()
    *   Esta función busca la biografía del propio del usuario y la coloca en la etiqueta del layout
    * */
    private fun establecerBio() {
        db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    tvBio.setText(it.get("biografia") as String?)
                } else {
                    tvBio.visibility = View.GONE
                }
            }
    }

    /*
    *
    * Función checkPerfil()
    *   Función que comprueba si está logeado el usuario, y dependiendo de esto, redirige a una actividad
    *   o fragmento diferente
    * */
    private fun checkPerfil() {
        val user = FirebaseAuth.getInstance().getCurrentUser()
        if (user == null) {
            val i: Intent =
                Intent(context as Context, com.example.tfg.login.LoginActivity::class.java)
            startActivity(i)
        } else {
            val editPerfilFragment = EditPerfilFragment()
            activity?.getSupportFragmentManager()?.beginTransaction()
                ?.replace(R.id.container, editPerfilFragment)
                ?.addToBackStack(null)
                ?.commit();

        }
    }


}