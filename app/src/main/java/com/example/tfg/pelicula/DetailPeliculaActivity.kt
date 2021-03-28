package com.example.tfg.pelicula

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.res.ResourcesCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.example.tfg.R
import com.example.tfg.login.LoginActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_pelicula.*
import kotlinx.android.synthetic.main.fragment_perfil.*
import www.sanju.motiontoast.MotionToast

class DetailPeliculaActivity : AppCompatActivity() {

    lateinit var tvTitulo: TextView
    lateinit var tvFecha: TextView
    lateinit var tvDuracion: TextView
    lateinit var tvCategoria: TextView
    lateinit var tvSinopsis: TextView
    lateinit var tvPlataforma: ImageView
    lateinit var caratula: ImageView
    lateinit var btnAdd: Button
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
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }

        /*
        * Función setUpDetallePelicula()
        *   Se encarga de establecer los datos de la película en el layout
        * */
        setUpDetallePelicula()

        btnAdd = findViewById(R.id.btnAddLista)

        btnAdd.setOnClickListener {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                val i: Intent = Intent(this, LoginActivity::class.java)
                startActivity(i)
            } else {
                agregarALista()
            }
        }


    }

    private fun setUpDetallePelicula() {

        //Recogemos los datos del Intent
        var datos = intent.extras
        var titulo = datos?.getString("titulo")
        var fecha = datos?.getString("fecha")
        var duracion = datos?.getString("duracion")
        var categoria = datos?.getString("categoria")
        var sinopsis = datos?.getString("sinopsis")
        var caratula = datos?.getString("caratula")


        if (categoria != null) {
            if (titulo != null) {
                db.collection("categorias").document(categoria)
                    .collection("peliculas").document(titulo)
                    .collection("plataformas").get().addOnSuccessListener {
                        for (doc in it) {
                            platNombre = doc.getString("logo").toString()
                            Picasso.get().load(platNombre).into(tvPlataforma)
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
        tvPlataforma = findViewById(R.id.ivFotoPlat)
        Picasso.get().load(caratula).into(ivCaratulaDet)

        tvTitulo.text = titulo
        tvFecha.text = fecha
        tvDuracion.text = duracion
        tvCategoria.text = categoria
        tvSinopsis.text = sinopsis
    }

    private fun agregarALista() {

        val elementosLista = listOf(
            "💜 Películas favoritas",
            "⏰ Películas pendientes",
            "👁 Películas vistas"
        )

        val dataFavs = hashMapOf(
            "nombre" to "💜 Películas favoritas"
        )

        val dataPendientes = hashMapOf(
            "nombre" to "⏰ Películas pendientes"
        )

        val dataVistas = hashMapOf(
            "nombre" to "👁 Películas vistas"
        )

        val data = hashMapOf(
            "titulo" to tvTitulo.text.toString(),
            "categoria" to tvCategoria.text.toString(),
            "fecha" to tvFechaDet.text.toString()
        )

        val contextView = findViewById<View>(R.id.btnAddLista)

        val activity: Activity = Activity()


        MaterialDialog(this).title(null, "Seleccione la lista").show {
            listItems(items = elementosLista) { dialog, index, text ->
                when (index) {
                    0 -> {
                        db.collection("usuarios")
                            .document(FirebaseAuth.getInstance().currentUser.email)
                            .collection("listas").document(text.toString()).set(dataFavs)

                        db.collection("usuarios")
                            .document(FirebaseAuth.getInstance().currentUser.email)
                            .collection("listas").document(text.toString()).collection("peliculas")
                            .document(tvTitulo.text.toString())
                            .set(data)

                        Snackbar.make(
                            contextView,
                            "Agregado a ${text.toString()}",
                            Snackbar.LENGTH_SHORT
                        ).show()

                        db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email)
                        .collection("listas").get().addOnSuccessListener {
                            val size = it.size()
                            Log.d("Listas", size.toString())
                        }
                    }
                    1 -> {
                        db.collection("usuarios")
                            .document(FirebaseAuth.getInstance().currentUser.email)
                            .collection("listas").document(text.toString()).set(dataPendientes)

                        db.collection("usuarios")
                            .document(FirebaseAuth.getInstance().currentUser.email)
                            .collection("listas").document(text.toString()).collection("peliculas")
                            .document(tvTitulo.text.toString())
                            .set(data)

                        Snackbar.make(
                            contextView,
                            "Agregado a ${text.toString()}",
                            Snackbar.LENGTH_SHORT
                        ).show()

                    }
                    2 -> {
                        db.collection("usuarios")
                            .document(FirebaseAuth.getInstance().currentUser.email)
                            .collection("listas").document(text.toString()).set(dataVistas)

                        db.collection("usuarios")
                            .document(FirebaseAuth.getInstance().currentUser.email)
                            .collection("listas").document(text.toString()).collection("peliculas")
                            .document(tvTitulo.text.toString())
                            .set(data)

                        Snackbar.make(
                            contextView,
                            "Agregado a ${text.toString()}",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }


            }
        }
    }


//        db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email)
//            .collection("listas").document(tvTitulo.text.toString()).set(data)
//
//        db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email)
//            .collection("listas").get().addOnSuccessListener {
//                val size = it.size()
//                Log.d("Peliculas en lista", size.toString())
//            }


}