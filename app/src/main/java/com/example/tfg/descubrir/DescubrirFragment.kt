package com.example.tfg.descubrir

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tfg.R
import com.example.tfg.casa.HomeFragment


class DescubrirFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_descubrir, container, false)

    companion object {
        fun newInstance(): DescubrirFragment = DescubrirFragment()
    }
}