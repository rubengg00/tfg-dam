package com.example.tfg.perfil

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.tfg.R

class PerfilFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_perfil, container, false)

        val cambiarTema: Button = root.findViewById(R.id.cambiarTema)


        cambiarTema.setOnClickListener { dialogoCambioTema() }

        return root
    }

    private fun dialogoCambioTema() {
        val context = context as Context
        val builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.choose_theme_text))
        val styles = arrayOf("Claro","Oscuro","Por defecto")
        val checkedItem = MisPreferencias(context).darkMode
        val activity = activity as AppCompatActivity

        builder.setSingleChoiceItems(styles, checkedItem) { dialog, which ->
            when (which) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    MisPreferencias(context).darkMode = 0
                    activity.delegate.applyDayNight()
                    dialog.dismiss()
                }
                1 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    MisPreferencias(context).darkMode = 1
                    activity.delegate.applyDayNight()
                    dialog.dismiss()
                }
                2 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    MisPreferencias(context).darkMode = 2
                    activity.delegate.applyDayNight()
                    dialog.dismiss()
                }

            }

        }

        val dialog = builder.create()
        dialog.show()
    }

    public fun comprobarTema(activity: Activity,context: Context ){
        val context = context as Context
        val activity = activity as AppCompatActivity

        when (MisPreferencias(context).darkMode) {
            0 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                activity.delegate.applyDayNight()
            }
            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                activity.delegate.applyDayNight()
            }
            2 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                activity.delegate.applyDayNight()
            }
        }
    }

}