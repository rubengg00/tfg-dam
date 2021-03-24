package com.example.tfg.pelicula

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.tfg.R
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_pelicula.*

class DetailPeliculaActivity : AppCompatActivity() {

    lateinit var tvTitulo: TextView
    lateinit var tvFecha: TextView
    lateinit var tvDuracion: TextView
    lateinit var tvCategoria: TextView
    lateinit var tvSinopsis: TextView
    lateinit var tvPlataforma: TextView
    lateinit var caratula: ImageView
    var platNombre: String = ""
    var enlace: String = ""

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_pelicula)

        /*
        * Si la versión de Android es superior a la versión KitKat, se mostrará la actividad
        * en pantalla completa
        * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        //Recogemos los datos del Intent
        var datos = intent.extras
        var titulo = datos?.getString("titulo")
        var fecha = datos?.getString("fecha")
        var duracion = datos?.getString("duracion")
        var categoria = datos?.getString("categoria")
        var sinopsis = datos?.getString("sinopsis")
        var caratula = datos?.getString("caratula")

        Log.d("caratula", caratula.toString())

        if (categoria != null) {
            if (titulo != null) {
                db.collection("categorias").document(categoria)
                    .collection("peliculas").document(titulo)
                    .collection("plataformas").get().addOnSuccessListener {
                        for (doc in it) {
                            platNombre = doc.getString("nombre").toString()
                            tvPlataforma.text = platNombre
                            Log.d("plataforma", platNombre)
                            enlace = doc.getString("enlace").toString()
                            tvPlataforma.setOnClickListener {
                                val i: Intent = Intent(Intent.ACTION_VIEW)
                                i.data = Uri.parse(enlace)
                                startActivity(i)
                            }
                        }
                    }

            }
        }
        
        //Declaración de variables
        tvTitulo = findViewById(R.id.tvTituloDet)
        tvFecha = findViewById(R.id.tvFechaDet)
        tvDuracion = findViewById(R.id.tvDuracionDet)
        tvCategoria = findViewById(R.id.tvCatDet)
        tvSinopsis = findViewById(R.id.tvSinDet)
        tvPlataforma = findViewById(R.id.tvPlatDet)
        Picasso.get().load(caratula).into(ivCaratulaDet)

        tvTitulo.text = titulo
        tvFecha.text = fecha
        tvDuracion.text = duracion
        tvCategoria.text = categoria
        tvSinopsis.text = sinopsis

    }


}