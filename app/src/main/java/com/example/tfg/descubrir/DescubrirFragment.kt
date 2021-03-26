package com.example.tfg.descubrir

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.tfg.R
import com.example.tfg.casa.HomeFragment
import com.example.tfg.descubrir.busqueda.BusquedaFragment
import com.example.tfg.perfil.EditPerfilFragment


class DescubrirFragment : Fragment() {

    lateinit var btnBuscar : Button

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_descubrir, container, false)

        btnBuscar = root.findViewById(R.id.btnBus)

        btnBuscar.setOnClickListener {
            val busquedaFragment = BusquedaFragment()
            activity?.getSupportFragmentManager()?.beginTransaction()
                ?.setCustomAnimations(
                    R.anim.slide_bottom_up,
                    R.anim.slide_bottom_down
                )
                ?.replace(R.id.container,busquedaFragment)
                ?.addToBackStack(null)
                ?.commit();
        }

        return root
    }
}