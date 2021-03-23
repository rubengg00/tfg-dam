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
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import com.example.tfg.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_edit_perfil.*
import www.sanju.motiontoast.MotionToast


class EditPerfilFragment : Fragment(), View.OnClickListener {

    private val db = FirebaseFirestore.getInstance()
    lateinit var btnCambiarTema: Button
    lateinit var btnLogOut: Button
    lateinit var btnActPerfil: Button
    lateinit var etNombre: EditText
    lateinit var etBio: EditText
    private var nombreUsuario: String = ""
    private var biografiaUsuario: String = ""

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_edit_perfil, container, false)

        /* Instancia de varibles */
        btnLogOut = root.findViewById(R.id.btnLogOut)
        btnCambiarTema = root.findViewById(R.id.cambiarTema)
        btnActPerfil = root.findViewById(R.id.btnActualizar)
        etNombre = root.findViewById(R.id.etNombreAct)
        etBio = root.findViewById(R.id.etBioAct)

        val fotoPerfil: ImageView = root.findViewById(R.id.userFotoEdit)
        Picasso.get().load(FirebaseAuth.getInstance().currentUser.photoUrl).into(fotoPerfil)

        /*Establecemos los valores del usuario en los campos*/
        db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email).get().addOnSuccessListener {
            if (it.exists()){
                etNombre.setText(it.get("nickname") as String?)
                etBio.setText(it.get("biografia") as String?)
            }
        }


        btnCambiarTema.setOnClickListener(this)
        btnLogOut.setOnClickListener(this)
        btnActPerfil.setOnClickListener(this)

        return root
    }

    override fun onClick(v: View?) {
        when(v){
            btnLogOut->{
                logOut()
            }
            btnCambiarTema->{
                dialogoCambioTema()
            }
            btnActPerfil->{
                actualizar()
            }
        }
    }

    private fun actualizar() {
        if (!comprobar()) return

        db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email).set(
            hashMapOf("nickname" to nombreUsuario, "biografia" to biografiaUsuario)
        )

        MotionToast.createToast(activity as Activity,
            "Perfil actualizado 👍",
            "Datos actualizados correctamente!",
            MotionToast.TOAST_SUCCESS,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(context as Context,R.font.helvetica_regular))

        val perfilFragment = PerfilFragment()
        activity?.getSupportFragmentManager()?.beginTransaction()
            ?.replace(R.id.container,perfilFragment)
            ?.addToBackStack(null)
            ?.commit();

    }

    private fun comprobar(): Boolean {
        nombreUsuario = etNombreAct.text.toString().trim()
        biografiaUsuario = etBioAct.text.toString().trim()

        if (nombreUsuario.length == 0){
            etNombreAct.setError("Este campo es obligatorio!")
            return false
        }
        if (biografiaUsuario.length == 0){
            etBioAct.setError("Este campo es obligatorio!")
            return false
        }
        return true

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

    /*
    * Función comprobarTema()
    *   Esta función se llama cada vez que se inicia el HomeFragment, para comprobar que preferencia
    *   se había guardado en la última conexión a la aplicación
    * */
    public fun comprobarTema(activity: Activity, context: Context){
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


    private fun logOut() {
        FirebaseAuth.getInstance().signOut()
        val perfilFragment = PerfilFragment()
        activity?.getSupportFragmentManager()?.beginTransaction()
            ?.replace(R.id.container,perfilFragment)
            ?.addToBackStack(null)
            ?.commit();
    }
}