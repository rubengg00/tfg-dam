package com.example.tfg.perfil

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.tfg.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_perfil.*

class PerfilFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()


    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_perfil, container, false)

        /* Declaración de varibles */
        val btnEditPerfil: Button = root.findViewById(R.id.editPerfil)
        val fotoPerfil: ImageView = root.findViewById(R.id.userFoto)
        val nombreUser: TextView = root.findViewById(R.id.tvUserName)
        val tvBio: TextView = root.findViewById(R.id.tvBio)

        if (FirebaseAuth.getInstance().currentUser!=null){
            Picasso.get().load(FirebaseAuth.getInstance().currentUser.photoUrl).into(fotoPerfil)
            nombreUser.text = FirebaseAuth.getInstance().currentUser.displayName
            establecerBio()
        }else{
            nombreUser.visibility = View.GONE
            tvBio.visibility = View.GONE
        }

        btnEditPerfil.setOnClickListener { checkPerfil() }


        return root
    }

    /*
    * Función establecerBio()
    *   Esta función busca la biografía del propio del usuario y la coloca en la etiqueta del layout
    * */
    private fun establecerBio() {
        db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email).get().addOnSuccessListener {
            if (it.exists()){
                tvBio.setText(it.get("biografia") as String?)
            }else{
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
        if (user == null){
            val i: Intent = Intent(context as Context, com.example.tfg.login.LoginActivity::class.java)
            startActivity(i)
        }else{
            val editPerfilFragment = EditPerfilFragment()
            activity?.getSupportFragmentManager()?.beginTransaction()
                ?.replace(R.id.container,editPerfilFragment)
                ?.addToBackStack(null)
                ?.commit();

        }
    }



}