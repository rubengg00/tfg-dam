package com.example.tfg.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import com.example.tfg.MainActivity
import com.example.tfg.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import www.sanju.motiontoast.MotionToast

class AgregadoInfoActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var etNombre: EditText
    lateinit var etBio: EditText
    lateinit var btnSave: Button
    private var nombre: String = ""
    private var biografia: String = ""

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregado_info)

        etNombre = findViewById(R.id.etNickname)
        etBio = findViewById(R.id.etBio)
        btnSave = findViewById(R.id.btnSaveData)

        btnSave.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        guardarCampos()
    }

    private fun guardarCampos() {
        if (!comprobar()) return

        val nomAnterior = arrayListOf<String>()

        db.collection("usuarios").get().addOnSuccessListener {
            for (doc in it) {
                nomAnterior.add(doc.getString("nickname").toString())
            }
            if (nomAnterior.contains(nombre)) {
                MotionToast.darkToast(this,
                    "Error",
                    "Usuario ya existente",
                    MotionToast.TOAST_ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this,R.font.helvetica_regular))
            } else {
                db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email)
                    .set(
                        hashMapOf("nickname" to nombre, "biografia" to biografia)
                    )

                MotionToast.darkToast(this,
                    "Perfil actualizado 👍",
                    "Datos agregados correctamente!",
                    MotionToast.TOAST_SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this,R.font.helvetica_regular))

                val i: Intent = Intent(this, MainActivity::class.java)
                startActivity(i)
            }
        }
    }

    private fun comprobar(): Boolean {
        nombre = etNombre.text.toString().trim()
        biografia = etBio.text.toString().trim()

        if (nombre.isEmpty() || biografia.isEmpty()){
            MotionToast.darkToast(this,
                "Error",
                "Ambos campos son obligatorios",
                MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this,R.font.helvetica_regular))

            return false
        }
        return true

    }
}