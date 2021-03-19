package com.example.tfg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

object cadenas{
    var EMAIL = "email"
    var PASS = "pass"
}

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var botonLogin: TextView
    private lateinit var botonReg: TextView
    private lateinit var textUsuario: TextView
    private lateinit var textPass: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setUp()
    }

    private fun setUp() {
        botonLogin = findViewById(R.id.btnLogin)
        botonReg = findViewById(R.id.btnReg)
        textUsuario = findViewById(R.id.usuario_input)
        textPass = findViewById(R.id.password)

        botonLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            botonLogin->{
                goMain()
            }
        }
    }

    private fun goMain() {
        if (! (comprobarVacio())) return

        var i: Intent = Intent(this, MainActivity::class.java)
        i.putExtra(cadenas.EMAIL, textUsuario.text.toString())
        i.putExtra(cadenas.PASS, textPass.text.toString())
        startActivity(i)
    }

    private fun comprobarVacio(): Boolean {
        if (textUsuario.text.trim().isEmpty()){
            textUsuario.error="Email obligatorio"
            return false
        }

        if (textPass.text.trim().isEmpty()){
            textPass.error = "Contraseña obligatoria"
            return false
        }

        return true
    }
}