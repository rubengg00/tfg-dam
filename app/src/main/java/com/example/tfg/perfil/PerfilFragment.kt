package com.example.tfg.perfil

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import com.example.tfg.LoginActivity
import com.example.tfg.R
import com.example.tfg.casa.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import www.sanju.motiontoast.MotionToast

class PerfilFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_perfil, container, false)

        /* Declaración de varibles */
        val btnCambiarTema: Button = root.findViewById(R.id.cambiarTema)
        val btnEditPerfil: Button = root.findViewById(R.id.editPerfil)

        btnCambiarTema.setOnClickListener { dialogoCambioTema() }
        btnEditPerfil.setOnClickListener { checkPerfil() }

        val fotoPerfil: ImageView = root.findViewById(R.id.userFoto)
        val nombreUser: TextView = root.findViewById(R.id.tvUserName)
        if (FirebaseAuth.getInstance().currentUser!=null){
            Picasso.get().load(FirebaseAuth.getInstance().currentUser.photoUrl).into(fotoPerfil)
            nombreUser.text = FirebaseAuth.getInstance().currentUser.displayName
        }

        return root
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

    /*
    * Función dialogoCambioTema()
    *   Función que abre un diálogo al usuario para poder cambiar entre modo claro y modo oscuro en
    *   la aplicación
    * */
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