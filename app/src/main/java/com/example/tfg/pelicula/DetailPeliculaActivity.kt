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
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.res.ResourcesCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.example.tfg.R
import com.example.tfg.login.LoginActivity
import com.example.tfg.pelicula.recomendaciones.Recomendacion
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_pelicula.*
import kotlinx.android.synthetic.main.custom_dialog_recomendation.*
import kotlinx.android.synthetic.main.fragment_perfil.*
import www.sanju.motiontoast.MotionToast
import java.sql.Timestamp
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class DetailPeliculaActivity : AppCompatActivity() {

    lateinit var tvTitulo: TextView
    lateinit var tvFecha: TextView
    lateinit var tvDuracion: TextView
    lateinit var tvCategoria: TextView
    lateinit var tvSinopsis: TextView
    lateinit var tvPlataforma: ImageView
    lateinit var caratula: ImageView
    lateinit var btnAdd: Button
    lateinit var btnRecom: Button
    var platNombre: String = ""
    var enlace: String = ""

    private val db = FirebaseFirestore.getInstance()
    private val nodoRaiz = FirebaseDatabase.getInstance()
    lateinit var reference: DatabaseReference
    lateinit var btnAddRecom: Button

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
        btnRecom = findViewById(R.id.btnRecom)

        btnAdd.setOnClickListener {
            if (FirebaseAuth.getInstance().currentUser == null) {
                val i: Intent = Intent(this, LoginActivity::class.java)
                startActivity(i)
            } else {
                agregarALista()
            }
        }

        btnRecom.setOnClickListener{
            if (FirebaseAuth.getInstance().currentUser == null){
                val i: Intent = Intent(this, LoginActivity::class.java)
                startActivity(i)
            }else{
                añadirReseña()
            }
        }

    }

    private fun añadirReseña() {
        var datos = intent.extras

        var fotoUsuario = ""
        var caratul = ""
        var titul = ""
        var categori = ""
        var fech = ""
        var reseña = ""
        var emoji = ""
        var email = ""
        var id_recomendacion = System.currentTimeMillis()


        MaterialDialog(this).show {
            customView(R.layout.custom_dialog_recomendation)
            positiveButton(R.string.recomendacion){dialog->
                var nomUsuario = ""
                var radioGrupo: RadioGroup = findViewById(R.id.rGroup)
                var texto: TextView = findViewById(R.id.tvTextOpinion)
                var seleccionado = rGroup.checkedRadioButtonId
                var emojiSeleccionado: RadioButton = findViewById(seleccionado)

                fotoUsuario = FirebaseAuth.getInstance().currentUser.photoUrl.toString()
                caratul = datos?.getString("caratula").toString()
                titul = datos?.getString("titulo").toString()
                categori = datos?.getString("categoria").toString()
                fech = datos?.getString("fecha").toString()
                emoji = emojiSeleccionado.text.toString()
                reseña = findViewById<TextView>(R.id.edComentario).text.toString()
                email = FirebaseAuth.getInstance().currentUser.email

                db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email).get().addOnSuccessListener {
                    if (it.getString("nickname").toString() == ""){
                        nomUsuario = FirebaseAuth.getInstance().currentUser.displayName
                        Log.d("email", email)
                        var recomendacion: Recomendacion = Recomendacion(nomUsuario, fotoUsuario,email, caratul, titul, categori, fech, reseña, emoji)
                        nodoRaiz.reference.child("recomendaciones").child(id_recomendacion.toString()).setValue(recomendacion)
                        Log.d("nombre", nomUsuario)
                    }else{
                        nomUsuario = it.getString("nickname").toString()
                        var recomendacion: Recomendacion = Recomendacion(nomUsuario, fotoUsuario,email, caratul, titul, categori, fech, reseña, emoji)
                        nodoRaiz.reference.child("recomendaciones").child(id_recomendacion.toString()).setValue(recomendacion)
                        Log.d("nombre", nomUsuario)
                    }
                }

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


}