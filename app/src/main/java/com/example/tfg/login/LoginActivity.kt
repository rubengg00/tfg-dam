package com.example.tfg.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.example.tfg.MainActivity
import com.example.tfg.R
import com.example.tfg.perfil.PerfilFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import www.sanju.motiontoast.MotionToast

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private val REQ_GOG = 124 // Validacion con Google
    private val REQ_INT_GOG = 125 // Intent a Activity2 con Google

    companion object datos {
        val MAIL = "Correo"
    }

    private lateinit var mAuth: FirebaseAuth
    var firebaseUser: FirebaseUser? = null

    //Para la validación de Google
    private lateinit var clienteGoogle: GoogleSignInClient

    private val db = FirebaseFirestore.getInstance()


    //----------------------------------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        clienteGoogle = GoogleSignIn.getClient(this, gso)
        btnSignGoogle.setOnClickListener(this)
    }
    //----------------------------------------------------------------------------------------------
    override fun onClick(v: View?) {
        when(v){
            btnSignGoogle->{
                signIn()
            }
        }
    }
    //----------------------------------------------------------------------------------------------
    private fun signIn() {
        val i: Intent = clienteGoogle.signInIntent
        startActivityForResult(i, REQ_GOG)
    }
    //----------------------------------------------------------------------------------------------

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    if (user != null){
                        user.email?.let { irPerfil(it) }
                    }
                } else {
                    Toast.makeText(this, "Error validación Google: " + task.exception, Toast.LENGTH_LONG).show()
                }
            }
    }
    //----------------------------------------------------------------------------------------------
    private fun irPerfil(mail: String){
        val i = Intent(this, MainActivity::class.java)
        i.putExtra(datos.MAIL, mail)
        startActivityForResult(i, REQ_INT_GOG)
        MotionToast.createToast(this,
            "Inicio completo 😍",
            "Has iniciado sesión correctamente!",
            MotionToast.TOAST_SUCCESS,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(this,R.font.helvetica_regular))

        db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email).collection("listas").document("💜 Películas favoritas").set(
            hashMapOf("nombre" to "💜 Películas favoritas")
        )
        db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email).collection("listas").document("⏰ Películas pendientes").set(
            hashMapOf("nombre" to "⏰ Películas pendientes")
        )
        db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email).collection("listas").document("👁 Películas vistas").set(
            hashMapOf("nombre" to "👁 Películas vistas")
        )
    }
    //----------------------------------------------------------------------------------------------
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_GOG) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Error al iniciar con Google", Toast.LENGTH_LONG).show()
            }
        }
        if (requestCode == REQ_INT_GOG && resultCode == RESULT_OK){
            clienteGoogle.signOut()
        }
    }

}