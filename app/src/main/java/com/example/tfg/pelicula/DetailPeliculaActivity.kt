package com.example.tfg.pelicula

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.tfg.R
import com.example.tfg.login.AgregadoInfoActivity
import com.example.tfg.login.LoginActivity
import com.example.tfg.pelicula.recomendaciones.Recomendacion
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.mrapp.android.bottomsheet.BottomSheet
import kotlinx.android.synthetic.main.activity_detail_pelicula.*


class DetailPeliculaActivity : AppCompatActivity() {

    lateinit var tvTitulo: TextView
    lateinit var tvFecha: TextView
    lateinit var tvDuracion: TextView
    lateinit var tvCategoria: TextView
    lateinit var tvSinopsis: TextView
    lateinit var tvPlataforma: ImageView
    lateinit var tvTrailer: ImageView
    lateinit var caratula: ImageView
    lateinit var btnAdd: Button
    lateinit var btnRecom: Button
    var platNombre: String = ""
    var enlace: String = ""

    private val db = FirebaseFirestore.getInstance()
    private val nodoRaiz = FirebaseDatabase.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_pelicula)

        /*
        * Si la versión de Android es superior a la versión KitKat, se mostrará la actividad
        * en pantalla completa
        * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(
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

        btnRecom.setOnClickListener {
            if (FirebaseAuth.getInstance().currentUser == null) {
                val i: Intent = Intent(this, LoginActivity::class.java)
                startActivity(i)
            } else {
                db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email)
                    .get().addOnSuccessListener {
                        if (it.getString("nickname").isNullOrEmpty()) {
                            val i: Intent = Intent(this, AgregadoInfoActivity::class.java)
                            startActivity(i)
                        } else {
                            añadirReseña()
                        }
                    }
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

        val contextView = findViewById<View>(R.id.btnAddLista)

        var nomUsuario = ""

        MaterialDialog(this).show {
            customView(R.layout.custom_dialog_recomendation)
            positiveButton(R.string.recomendacion) { dialog ->

                var emojiSeleccionado: RadioButton? = null

                var radioGrupo: RadioGroup = this.findViewById(R.id.rGroup)
                var seleccionado = radioGrupo.checkedRadioButtonId
                if (seleccionado != null){
                    emojiSeleccionado = findViewById(seleccionado)
                }

                fotoUsuario = FirebaseAuth.getInstance().currentUser.photoUrl.toString()
                caratul = datos?.getString("caratula").toString()
                titul = datos?.getString("titulo").toString()
                categori = datos?.getString("categoria").toString()
                fech = datos?.getString("fecha").toString()
                if (seleccionado != null){
                    emoji = emojiSeleccionado?.text.toString()
                }
                reseña = findViewById<TextView>(R.id.edComentario).text.toString()
                email = FirebaseAuth.getInstance().currentUser.email

                if (reseña.trim().isNullOrEmpty() || emojiSeleccionado == null) {
                    Toast.makeText(context, "Rellene los campos", Toast.LENGTH_LONG).show()
                    return@positiveButton
                } else {
                    db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email)
                        .get().addOnSuccessListener {
                            nomUsuario = it.getString("nickname").toString()
                            var recomendacion: Recomendacion = Recomendacion(
                                nomUsuario,
                                fotoUsuario,
                                email,
                                caratul,
                                titul,
                                categori,
                                fech,
                                reseña,
                                emoji
                            )
                            nodoRaiz.reference.child("recomendaciones")
                                .child(id_recomendacion.toString()).setValue(recomendacion)
                            Log.d("nombre", nomUsuario)
                        }
                }

                Snackbar.make(
                    contextView,
                    "¡Película recomendada!",
                    Snackbar.LENGTH_SHORT
                ).show()
            }

        }

    }


    private fun setUpDetallePelicula() {

        //Declaración de variables
        tvTitulo = findViewById(R.id.tvTituloDet)
        tvFecha = findViewById(R.id.tvFechaDet)
        tvDuracion = findViewById(R.id.tvDuracionDet)
        tvCategoria = findViewById(R.id.tvCatDet)
        tvSinopsis = findViewById(R.id.tvSinDet)
        tvPlataforma = findViewById(R.id.ivFotoPlat)
        tvTrailer = findViewById(R.id.ivTrailer)

        //Recogemos los datos del Intent
        val datos = intent.extras
        val titulo = datos?.getString("titulo")
        val fecha = datos?.getString("fecha")
        val duracion = datos?.getString("duracion")
        val categoria = datos?.getString("categoria")
        val sinopsis = datos?.getString("sinopsis")
        val caratula = datos?.getString("caratula")
        val trailer = datos?.getString("trailer")

        tvTrailer.setOnClickListener {
            val i: Intent = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(trailer)
            startActivity(i)
        }

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

        Picasso.get().load(caratula).into(ivCaratulaDet)


        tvTitulo.text = titulo
        tvFecha.text = fecha
        tvDuracion.text = duracion
        tvCategoria.text = categoria
        tvSinopsis.text = sinopsis
    }

    private fun agregarALista() {

        val data = hashMapOf(
            "titulo" to tvTitulo.text.toString(),
            "categoria" to tvCategoria.text.toString(),
            "fecha" to tvFechaDet.text.toString()
        )
        val contextView = findViewById<View>(R.id.btnAddLista)

        var builder: BottomSheet.Builder = BottomSheet.Builder(this)
        builder.setTitle("Seleccione la lista")
        var id = 1
        var lista: MutableMap<Int, String> = HashMap()

        db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser.email)
            .collection("listas").get().addOnSuccessListener {
                for (doc in it) {
                    builder.addItem(1, doc.getString("nombre").toString())
                    lista[id] = doc.getString("nombre").toString()
                    id++
                    var bottomSheet: BottomSheet = builder.create()
                    bottomSheet.show()
                }
                builder.addDivider()
                builder.addItem(4, "➕ Crear nueva lista")
            }

        builder.setOnItemClickListener { parent, view, position, id ->

            if (id.toString() == "4") {
                MaterialDialog(this).title(null, "Agregue el nombre de la lista")
                    .show {
                        customView(R.layout.custom_dialog_lista)
                        positiveButton(R.string.crear) { dialog ->
                            var nombreLista = ""
                            var texto: EditText = findViewById(R.id.edListaPersonalizada)
                            nombreLista = texto.text.toString()
                            Log.d("nombre de la lista", nombreLista)

                            val dataListaPersonalizada = hashMapOf(
                                "nombre" to nombreLista
                            )

                            db.collection("usuarios")
                                .document(FirebaseAuth.getInstance().currentUser.email)
                                .collection("listas").document(nombreLista)
                                .set(dataListaPersonalizada)

                            db.collection("usuarios")
                                .document(FirebaseAuth.getInstance().currentUser.email)
                                .collection("listas").document(nombreLista)
                                .collection("peliculas")
                                .document(tvTitulo.text.toString())
                                .set(data)

                            Snackbar.make(
                                contextView,
                                "Agregado a ${nombreLista}",
                                Snackbar.LENGTH_SHORT
                            ).show()

                        }
                    }
            } else {
                var posicion = position + 1
                Log.d("nombre por ID", id.toString())

                Log.d("nombre", id.toString())
                db.collection("usuarios")
                    .document(FirebaseAuth.getInstance().currentUser.email)
                    .collection("listas").document((lista.get(posicion)).toString())
                    .collection("peliculas")
                    .document(tvTitulo.text.toString())
                    .set(data)

                Snackbar.make(
                    contextView,
                    "Agregado a ${(lista.get(posicion)).toString()}",
                    Snackbar.LENGTH_SHORT
                ).show()
            }


        }
    }
}
