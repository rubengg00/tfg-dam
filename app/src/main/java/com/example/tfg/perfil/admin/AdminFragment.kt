package com.example.tfg.perfil.admin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.tfg.R
import com.example.tfg.perfil.admin.categorias.AllCategoriasFragment
import com.example.tfg.perfil.admin.peliculas.AllPeliculasFragment
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class AdminFragment : Fragment() {

    lateinit var ivFotoAdmin: ImageView
    lateinit var btnRepositorio: Button
    lateinit var btnPeliculas: Button
    lateinit var btnCategorias: Button
    lateinit var btnResenas: Button


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_admin, container, false)

        ivFotoAdmin = root.findViewById(R.id.ivFotoAdmin)
        btnRepositorio = root.findViewById(R.id.btnRepositorio)
        btnPeliculas = root.findViewById(R.id.btnPeliculas)
        btnCategorias = root.findViewById(R.id.btnCategorias)
        btnResenas = root.findViewById(R.id.btnResenas)

        Picasso.get().load(FirebaseAuth.getInstance().currentUser.photoUrl).into(ivFotoAdmin)

        btnRepositorio.setOnClickListener { llevarRepositorio() }
        btnPeliculas.setOnClickListener { todasPeliculas() }
        btnCategorias.setOnClickListener { todasCategorias() }
        btnResenas.setOnClickListener { todasResenas() }

        return root
    }

    private fun todasPeliculas() {
        val allPeliculas = AllPeliculasFragment()

        var bundle: Bundle = Bundle()
        allPeliculas.arguments = bundle

        activity?.getSupportFragmentManager()?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_bottom_up,
                R.anim.slide_bottom_down
            )
            ?.replace(R.id.container,allPeliculas)
            ?.addToBackStack(null)
            ?.commit();
    }

    private fun todasCategorias() {
        val allCategorias = AllCategoriasFragment()

        var bundle: Bundle = Bundle()
        allCategorias.arguments = bundle

        activity?.getSupportFragmentManager()?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_bottom_up,
                R.anim.slide_bottom_down
            )
            ?.replace(R.id.container,allCategorias)
            ?.addToBackStack(null)
            ?.commit();
    }

    private fun todasResenas() {
        
    }

    private fun llevarRepositorio() {
        val i: Intent = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse("https://github.com/RubenGarciaGonzalez/tfg-dam")
        startActivity(i)
    }

}