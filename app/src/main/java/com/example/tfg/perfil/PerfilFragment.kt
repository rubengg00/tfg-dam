package com.example.tfg.perfil

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.example.tfg.R
import com.example.tfg.login.AgregadoInfoActivity
import com.example.tfg.pelicula.PelisPorCatFragment
import com.example.tfg.perfil.admin.AdminFragment
import com.example.tfg.perfil.listas.Lista
import com.example.tfg.perfil.listas.ListaFragment
import com.example.tfg.perfil.reseñas.MisRecomenFragment
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_perfil.*
import org.w3c.dom.Text
import www.sanju.motiontoast.MotionToast

class PerfilFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    lateinit var numListas: TextView
    lateinit var recview: RecyclerView
    lateinit var edit: TextView
    lateinit var FirestoreRecyclerAdapter: FirestoreRecyclerAdapter<Lista, ListaViewHolder>
    lateinit var tvAdmin: TextView
    lateinit var btnMisResenas: Button
    lateinit var nombreUser: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_perfil, container, false)

        /* Declaración de varibles */
        btnMisResenas = root.findViewById(R.id.misReseñas)
        var fotoPerfil: ImageView = root.findViewById(R.id.userFoto)
        nombreUser = root.findViewById(R.id.tvUserName)
        var tvBio: TextView = root.findViewById(R.id.tvBio)
        tvAdmin = root.findViewById(R.id.tvAdmin)
        tvAdmin.visibility = View.GONE
        edit = root.findViewById(R.id.tvEditPerfil)
        numListas = root.findViewById(R.id.tvNumListas)
        recview = root.findViewById(R.id.recListas)

        if (FirebaseAuth.getInstance().currentUser != null) {
            Picasso.get().load(FirebaseAuth.getInstance().currentUser.photoUrl).into(fotoPerfil)
            db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email).get()
                .addOnSuccessListener {
                    if (it.getString("nickname").toString() == "" || it.getString("nickname")
                            .isNullOrEmpty()
                    ) {
                        nombreUser.text = FirebaseAuth.getInstance().currentUser.displayName
                    } else {
                        nombreUser.text = it.getString("nickname").toString()
                    }
                }
            establecerBio()
            numeroListas()
            cargarRecyclerView()
            agregarInfoUser()

            if (FirebaseAuth.getInstance().currentUser.email.equals("xykaxkillercc@gmail.com")) {
                tvAdmin.visibility = View.VISIBLE
            }

        } else {
            nombreUser.visibility = View.GONE
            tvBio.visibility = View.GONE
            edit.visibility = View.GONE
            btnMisResenas.text = "Iniciar Sesión"
        }

        edit.setOnClickListener { checkPerfil() }

        btnMisResenas.setOnClickListener { checkReseñas() }

        if (tvAdmin.visibility == View.VISIBLE) {
            tvAdmin.setOnClickListener {
                goAdmin()
            }
        }

        return root
    }

    private fun goAdmin() {
        val adminFragment = AdminFragment()

        var bundle: Bundle = Bundle()
        bundle.putString("nickname", nombreUser.text.toString())
        adminFragment.arguments = bundle

        activity?.getSupportFragmentManager()?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_bottom_up,
                R.anim.slide_bottom_down
            )
            ?.replace(R.id.container, adminFragment)
            ?.addToBackStack(null)
            ?.commit();
    }

    private fun checkReseñas() {

        if (btnMisResenas.text == "Iniciar Sesión") {
            val i: Intent =
                Intent(context as Context, com.example.tfg.login.LoginActivity::class.java)
            startActivity(i)
        } else {
            var nickname = ""

            db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email)
                .get().addOnSuccessListener {
                    nickname = it.getString("nickname").toString()
                    val misRecomenFragment = MisRecomenFragment()
                    var bundle: Bundle = Bundle()
                    bundle.putString("nickname", nickname)
                    misRecomenFragment.arguments = bundle

                    activity?.supportFragmentManager?.beginTransaction()
                        ?.setCustomAnimations(
                            R.anim.slide_bottom_up,
                            R.anim.slide_bottom_down
                        )
                        ?.replace(R.id.container, misRecomenFragment)
                        ?.addToBackStack(null)
                        ?.commit();
                }
        }

    }

    //-----------------------------------------------------------------------------------------------
    private fun agregarInfoUser() {
        db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email).get()
            .addOnSuccessListener {
                if (it.getString("nickname").isNullOrEmpty() || it.getString("biografia")
                        .isNullOrEmpty()
                ) {
                    val i: Intent =
                        Intent(context as Context, AgregadoInfoActivity::class.java)
                    startActivity(i)
                }
            }
    }

    //-----------------------------------------------------------------------------------------------

    private fun cargarRecyclerView() {

        val query: Query =
            db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email)
                .collection("listas")

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

                    if (holder.tvLista.text == "⏰ Películas pendientes" || holder.tvLista.text == "👁 Películas vistas" || holder.tvLista.text == "💜 Películas favoritas"){
                        holder.tvOpciones.visibility = View.GONE
                    }

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

                    holder.tvOpciones.setOnClickListener {
                        eliminacionLista(holder)
                    }
                }
            }

        recview.setHasFixedSize(true)
        recview.layoutManager = LinearLayoutManager(context as Context)
        recview.adapter = FirestoreRecyclerAdapter

    }

    private fun eliminacionLista(holder: ListaViewHolder) {
        MaterialDialog(context as Context)
            .show {
                var titulo = ""
                title(null, "Borrando lista")
                message(null, "¿Deseas borrar la lista ${holder.tvLista.text}?")
                negativeButton(R.string.opcion_positivia) { dialog ->
                    db.collection("usuarios")
                        .document(FirebaseAuth.getInstance().currentUser.email)
                        .collection("listas").document(holder.tvLista.text.toString()).delete()

                    MotionToast.darkToast(
                        activity as Activity,
                        "Lista eliminada 👍",
                        "Lista eliminada correctamente!",
                        MotionToast.TOAST_SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(context as Context, R.font.helvetica_regular)
                    )
                    numListas.text = ""
                    numeroListas()


                }
                positiveButton(R.string.opcion_negativa) { dialog ->
                    {
                        dialog.dismiss()
                    }
                }

            }
    }


    class ListaViewHolder(var v: View) : RecyclerView.ViewHolder(v) {
        val tvLista: TextView = v.findViewById(R.id.tvNombreLista)
        val tvOpciones: TextView = v.findViewById(R.id.tvOpciones3)
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
    //-----------------------------------------------------------------------------------------------


    /*
    * Función numeroListas()
    *   Esta función devuelve el total de listas del usuario
    * */
    private fun numeroListas() {
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
                    tvBio.text = it.get("biografia").toString()
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