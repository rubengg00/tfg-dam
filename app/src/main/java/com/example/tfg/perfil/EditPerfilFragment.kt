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
import com.afollestad.materialdialogs.MaterialDialog
import com.example.tfg.R
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_edit_perfil.*
import www.sanju.motiontoast.MotionToast


class EditPerfilFragment : Fragment(), View.OnClickListener {

    private val db = FirebaseFirestore.getInstance()
    lateinit var btnLogOut: Button
    lateinit var btnDelAcc: Button
    lateinit var btnActPerfil: Button
    lateinit var etNombre: EditText
    lateinit var etBio: EditText
    private var nombreUsuario: String = ""
    private var biografiaUsuario: String = ""
    lateinit var gso: GoogleSignInOptions
    lateinit var mGoogleApiClient: GoogleApiClient

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_edit_perfil, container, false)

        /* Instancia de varibles */
        btnLogOut = root.findViewById(R.id.btnLogOut)
        btnDelAcc = root.findViewById(R.id.btnDelAcc)
        btnActPerfil = root.findViewById(R.id.btnActualizar)
        etNombre = root.findViewById(R.id.etNombreAct)
        etBio = root.findViewById(R.id.etBioAct)

        val fotoPerfil: ImageView = root.findViewById(R.id.userFotoPlat)
        Picasso.get().load(FirebaseAuth.getInstance().currentUser.photoUrl).into(fotoPerfil)

        /*Establecemos los valores del usuario en los campos*/
        db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email).get().addOnSuccessListener {
            if (it.exists()){
                etNombre.setText(it.get("nickname") as String?)
                etBio.setText(it.get("biografia") as String?)
            }
        }

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        mGoogleApiClient = GoogleApiClient.Builder(context!!).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build()
        mGoogleApiClient.connect()


        btnLogOut.setOnClickListener(this)
        btnActPerfil.setOnClickListener(this)
        btnDelAcc.setOnClickListener(this)

        return root
    }

    override fun onClick(v: View?) {
        when(v){
            btnLogOut->{
                MaterialDialog(context as Context).show {
                    title(null, "Cerrar sesión")
                    message(null, "¿Deseas cerrar la sesión?")
                    negativeButton(R.string.opcion_positivia) {
                        logOut()
                    }
                    positiveButton(R.string.opcion_negativa){
                        dismiss()
                    }
                }
            }
            btnActPerfil->{
                actualizar()
            }
            btnDelAcc->{
                MaterialDialog(context as Context).show {
                    title(null, "Eliminar cuenta")
                    message(null, "¿Estás seguro? Se eliminarán tanto tus listas de reproducción, como tus reseñas")
                    negativeButton(R.string.opcion_positivia){
                        deleteAccount()
                    }
                    positiveButton(R.string.opcion_negativa){
                        dismiss()
                    }
                }
            }
        }
    }

    private fun deleteAccount() {
        var email = FirebaseAuth.getInstance().currentUser.email
        db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email).delete()

        MotionToast.darkToast(activity as Activity,
            "Cuenta eliminada 👍",
            "Cuenta eliminada correctamente!",
            MotionToast.TOAST_SUCCESS,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(context as Context,R.font.helvetica_regular))

        var query: Query = FirebaseDatabase.getInstance().getReference("recomendaciones").orderByChild("email").equalTo(email)
        query.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (resultado in snapshot.children){
                    resultado.ref.removeValue()
                }
            }

        })

        logOut()

    }

    private fun actualizar() {
        if (!comprobar()) return

        val nicknamesUsuarios = arrayListOf<String>()

        db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email).get().addOnSuccessListener {
            var nicknameActual = it.getString("nickname").toString()

            db.collection("usuarios").get().addOnSuccessListener {
                for (doc in it) {
                    if (doc.getString("nickname").toString() != nicknameActual){
                        nicknamesUsuarios.add(doc.getString("nickname").toString())
                    }
                }
                if (nicknamesUsuarios.contains(nombreUsuario)) {
                    MotionToast.darkToast(activity as Activity,
                        "Error",
                        "Usuario ya existente",
                        MotionToast.TOAST_ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(context as Context,R.font.helvetica_regular))

                }else if (nombreUsuario == nicknameActual){
                    db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email)
                        .set(
                            hashMapOf("nickname" to nombreUsuario, "biografia" to biografiaUsuario)
                        )

                    MotionToast.darkToast(activity as Activity,
                        "Perfil actualizado 👍",
                        "Datos agregados correctamente!",
                        MotionToast.TOAST_SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(context as Context,R.font.helvetica_regular))

                    val perfilFragment = PerfilFragment()
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.container,perfilFragment)
                        ?.addToBackStack(null)
                        ?.commit();
                }else{
                    db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email)
                        .set(
                            hashMapOf("nickname" to nombreUsuario, "biografia" to biografiaUsuario)
                        )

                    MotionToast.darkToast(activity as Activity,
                        "Perfil actualizado 👍",
                        "Datos agregados correctamente!",
                        MotionToast.TOAST_SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(context as Context,R.font.helvetica_regular))

                    val perfilFragment = PerfilFragment()
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.container,perfilFragment)
                        ?.addToBackStack(null)
                        ?.commit();
                }
            }
        }


    }

    private fun comprobar(): Boolean {
        nombreUsuario = etNombreAct.text.toString().trim()
        biografiaUsuario = etBioAct.text.toString().trim()

        if (nombreUsuario.isEmpty() || biografiaUsuario.isEmpty()){
            MotionToast.darkToast(activity as Activity,
                "Error",
                "Ambos campos son obligatorios",
                MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(context as Context,R.font.helvetica_regular))

            return false
        }
        return true

    }


    private fun logOut() {
        FirebaseAuth.getInstance().signOut()
        Auth.GoogleSignInApi.signOut(mGoogleApiClient)

        MotionToast.darkToast(activity as Activity,
            "Sesión cerrada 👍",
            "Sesión cerrada correctamente!",
            MotionToast.TOAST_SUCCESS,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(context as Context,R.font.helvetica_regular))

        val perfilFragment = PerfilFragment()
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.container,perfilFragment)
            ?.addToBackStack(null)
            ?.commit();
    }
}