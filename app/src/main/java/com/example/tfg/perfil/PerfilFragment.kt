package com.example.tfg.perfil

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.tfg.R
import com.example.tfg.perfil.listas.Lista
import com.example.tfg.perfil.listas.ListaAdapter
import com.example.tfg.perfil.listas.ListaFragment
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_perfil.*

class PerfilFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    lateinit var numListas: TextView
    lateinit var recview: RecyclerView
    lateinit var miAdapter: ListaAdapter
    lateinit var FirestoreRecyclerAdapter: FirestoreRecyclerAdapter<Lista, PerfilFragment.ListaViewHolder>

    
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
            db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email).get()
                .addOnSuccessListener {
                    if (it.getString("nickname").toString() == "") {
                        nombreUser.text = FirebaseAuth.getInstance().currentUser.displayName
                    } else {
                        nombreUser.text = it.getString("nickname").toString()
                    }
                }
            establecerBio()
            establecerListas()
            cargarRecyclerView()
        } else {
            nombreUser.visibility = View.GONE
            tvBio.visibility = View.GONE
        }

        btnEditPerfil.setOnClickListener { checkPerfil() }



        return root
    }

    //-----------------------------------------------------------------------------------------------

    private fun cargarRecyclerView() {

        val query: Query =
            db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email)
                .collection("listas").orderBy("nombre")

        val options: FirestoreRecyclerOptions<Lista> =
            FirestoreRecyclerOptions.Builder<Lista>()
                .setQuery(query, Lista::class.java)
                .build()

        FirestoreRecyclerAdapter =
            object : FirestoreRecyclerAdapter<Lista, ListaViewHolder>(options) {


                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): ListaViewHolder {
                    val inflate = LayoutInflater.from(parent.context)
                    val v =
                        inflate.inflate(R.layout.custom_listas_layout, parent, false)
                    return ListaViewHolder(v)
                }

                override fun onBindViewHolder(
                    holder: ListaViewHolder,
                    position: Int,
                    model: Lista
                ) {
                    holder.tvLista.text = model.nombre
                    holder.tvContador.text = model.total

                    holder.itemView.setOnClickListener {
                        val listaFragment = ListaFragment()

                        var bundle: Bundle = Bundle()
                        bundle.putString("nombre", holder.tvLista.text.toString())
                        listaFragment.arguments = bundle

                        activity?.getSupportFragmentManager()?.beginTransaction()
                            ?.setCustomAnimations(
                                R.anim.slide_bottom_up,
                                R.anim.slide_bottom_down
                            )
                            ?.replace(R.id.container, listaFragment)
                            ?.addToBackStack(null)
                            ?.commit();
                    }
                }

                val mIth = ItemTouchHelper(
                    object : ItemTouchHelper.SimpleCallback(
                        0,
                        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                    ) {
                        override fun onMove(
                            recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder
                        ): Boolean {
                            return false
                        }

                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                            if (direction == ItemTouchHelper.RIGHT) {
                                MaterialDialog(context as Context).show {
                                    title(null, "Borrando lista")
                                    message(null, "¿Deseas borrar la lista?")
                                    negativeButton(R.string.opcion_positivia) { dialog ->
                                        listaBorrada(viewHolder.adapterPosition)
                                    }
                                    positiveButton(R.string.opcion_negativa) { dialog ->
                                        FirestoreRecyclerAdapter.notifyItemChanged(viewHolder.adapterPosition);
                                        dialog.dismiss()
                                    }
                                }
                            }

                            if (direction == ItemTouchHelper.LEFT) {
                                MaterialDialog(context as Context).title(null, "Editando lista")
                                    .show {
                                        customView(R.layout.custom_dialog_lista)

                                        var texto: EditText =
                                            findViewById(R.id.edListaPersonalizada)
                                        texto.setText(
                                            FirestoreRecyclerAdapter.snapshots.getSnapshot(
                                                viewHolder.adapterPosition
                                            ).getString("nombre").toString()
                                        )
                                        var oldNom = texto.text.toString()

                                        positiveButton(R.string.modificar) { dialog ->
                                            listaEditada(
                                                viewHolder.adapterPosition,
                                                texto.text.toString(),
                                                oldNom
                                            )
                                        }
                                    }
                            }
                        }
                    }).attachToRecyclerView(recview)

                fun listaBorrada(position: Int) {
                    FirestoreRecyclerAdapter.snapshots.getSnapshot(position).reference.delete()
                }

                private fun listaEditada(
                    position: Int,
                    texto: String,
                    oldNom: String
                ) {
                    db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email)
                        .collection("listas").document(oldNom).update("nombre", texto)
                    FirestoreRecyclerAdapter.snapshots.getSnapshot(position).reference.delete()

                }

            }

        recview.setHasFixedSize(true)
        recview.layoutManager = LinearLayoutManager(context as Context)
        recview.adapter = FirestoreRecyclerAdapter

    }


    class ListaViewHolder(var v: View) : RecyclerView.ViewHolder(v) {
        val tvLista: TextView = v.findViewById(R.id.tvNombreLista)
        val tvContador: TextView = v.findViewById(R.id.tvTotalPelis)
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


    //    /*
//    * Función rellenadoDatos()
//    *   Rellena el RecyclerView con las listas del usuario
//    * */
//    private fun rellenadoDatos() {
//
//        db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email)
//            .collection("listas").orderBy("nombre").get().addOnSuccessListener {
//                for (doc in it) {
//                    var titulo = doc.getString("nombre").toString()
//                    db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email)
//                        .collection("listas").document(titulo).collection("peliculas").get()
//                        .addOnSuccessListener {
//                            var contador = it.size().toString()
//                            var lista = Lista(
//                                titulo,
//                                contador
//                            )
//                            añadidoLista(lista)
//                        }
//                }
//            }
//
//    }
//    /*
//    * Función añadidoLista(lista: Lista)
//    *   Recibe el objeto de tipo Lista, y lo agrega a la lista, llamando luego a la función
//    *   crearAdapter()
//    * */
//    private fun añadidoLista(lista: Lista) {
//        listaListas.add(lista)
//        crearAdapter()
//    }
//
//
//    //-----------------------------------------------------------------------------------------------
//
//    /*
//    * Función crearAdapter()
//    *   Crear el adapter para el RecyclerView y establece el evento onClick sobre los elementos
//    * */
//    private fun crearAdapter() {
//        recview.setHasFixedSize(true)
//        miAdapter = ListaAdapter(listaListas, context as Context)
//        recview.layoutManager = LinearLayoutManager(context as Context)
//        recview.adapter = miAdapter
//
//        miAdapter.setOnClickListener(View.OnClickListener {
//            val listaFragment = ListaFragment()
//
//            var bundle: Bundle = Bundle()
//            bundle.putString("nombre", listaListas.get(recview.getChildAdapterPosition(it)).nombre)
//            listaFragment.arguments = bundle
//
//            activity?.getSupportFragmentManager()?.beginTransaction()
//                ?.setCustomAnimations(
//                    R.anim.slide_bottom_up,
//                    R.anim.slide_bottom_down
//                )
//                ?.replace(R.id.container,listaFragment)
//                ?.addToBackStack(null)
//                ?.commit();
//        })
//    }
//
//    /*
//    * Función establecerListas()
//    *   Esta función devuelve el total de listas del usuario
//    * */
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