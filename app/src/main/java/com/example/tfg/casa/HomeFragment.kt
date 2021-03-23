package com.example.tfg.casa

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.tfg.R
import com.example.tfg.perfil.EditPerfilFragment
import com.example.tfg.perfil.PerfilFragment
import kotlinx.android.synthetic.main.fragment_home.*
import www.sanju.motiontoast.MotionToast


class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        setUp()

        EditPerfilFragment().comprobarTema(activity as Activity, context as Context)
        return root
    }

    private fun setUp() {

    }
}