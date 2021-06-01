package com.example.tfg.perfil.admin.peliculas

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.example.tfg.R
import com.google.firebase.firestore.FirebaseFirestore
import www.sanju.motiontoast.MotionToast

class EditPeliculaFragment : Fragment() {

    lateinit var tvMov: TextView
    lateinit var edFecha: EditText
    lateinit var edSinopsis: EditText
    lateinit var edTrailer: EditText
    lateinit var edDuracion: EditText
    lateinit var btnEdit: Button
    lateinit var btnEliminar: Button
    var titulo = ""
    var fecha = ""
    var duracion = ""
    var sinopsis = ""
    var trailer = ""
    var cat = ""
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit_pelicula, container, false)
        //Recogemos los datos del Bundle
        var datos: Bundle? = this.arguments
        //Declaración de variables
        tvMov = root.findViewById(R.id.tvMovEdit)
        edFecha = root.findViewById(R.id.edFecha)
        edSinopsis = root.findViewById(R.id.edSinopsis)
        edTrailer = root.findViewById(R.id.edTrailer)
        edDuracion = root.findViewById(R.id.edDuracion)
        btnEdit = root.findViewById(R.id.btnEditPeli)
        btnEliminar = root.findViewById(R.id.btnEliminarPeli)
        //Asignación de datos
        tvMov.text = datos?.getString("titulo").toString()
        edTrailer.setText(datos?.getString("trailer").toString())
        edFecha.setText(datos?.getString("fecha").toString())
        edSinopsis.setText(datos?.getString("sinopsis").toString())
        edDuracion.setText(datos?.getString("duracion").toString())
        cat = datos?.getString("categoria").toString()
        //Eventos onClick sobre los botones
        btnEdit.setOnClickListener { editar(cat) }
        btnEliminar.setOnClickListener {
            MaterialDialog(context as Context).show {
                title(null, "Eliminar película")
                message(null, "¿Deseas eliminar ${tvMov.text.toString()} de la plataforma?")
                negativeButton(R.string.opcion_positivia) {
                    borrado(cat, tvMov.text.toString())
                }
                positiveButton(R.string.opcion_negativa) {
                    dismiss()
                }
            }
        }

        return root
    }

    private fun borrado(cat: String, titulo: String) {
        //db.collection("categorias").document(cat).collection("peliculas").document(titulo).delete()

        var correo = ""
        var lista = ""

        db.collection("usuarios").get().addOnSuccessListener {
            for (doc in it) {
                correo = doc.id
                Log.d("correo", correo)
                db.collection("usuarios").document(correo).collection("listas").get()
                    .addOnSuccessListener {
                        for (doc in it) {
                            lista = doc.id
                            Log.d("lista", lista)
                            db.collection("usuarios").document(correo).collection("listas")
                                .document(lista).collection("peliculas").get()
                                .addOnSuccessListener {
                                    for (doc in it){
                                        if (doc.getString("titulo").toString() == titulo){
                                            db.collection("usuarios").document(correo).collection("listas")
                                                .document(lista).collection("peliculas").document(titulo).delete()
                                        }
                                    }
                                }
                        }
                    }
            }
        }

        MotionToast.darkToast(
            activity as Activity,
            "Película borrada 👍",
            "Película borrada correctamente",
            MotionToast.TOAST_SUCCESS,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(context as Context, R.font.helvetica_regular)
        )

        val allPeliculas = AllPeliculasFragment()

        var bundle: Bundle = Bundle()
        allPeliculas.arguments = bundle

        activity?.getSupportFragmentManager()?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_bottom_up,
                R.anim.slide_bottom_down
            )
            ?.replace(R.id.container, allPeliculas)
            ?.addToBackStack(null)
            ?.commit();
    }

    private fun editar(categoria: String) {
        if (!comprobar()) return

        db.collection("categorias").document(categoria).collection("peliculas")
            .document(tvMov.text.toString())
            .update(
                mapOf(
                    "fecha" to edFecha.text.toString().trim(),
                    "sinopsis" to edSinopsis.text.toString().trim(),
                    "duracion" to edDuracion.text.toString().trim(),
                    "trailer" to edTrailer.text.toString().trim()
                )
            )

        MotionToast.darkToast(
            activity as Activity,
            "Película actualizada 👍",
            "Película actualizada correctamente",
            MotionToast.TOAST_SUCCESS,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(context as Context, R.font.helvetica_regular)
        )

        val allPeliculas = AllPeliculasFragment()

        var bundle: Bundle = Bundle()
        allPeliculas.arguments = bundle

        activity?.getSupportFragmentManager()?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_bottom_up,
                R.anim.slide_bottom_down
            )
            ?.replace(R.id.container, allPeliculas)
            ?.addToBackStack(null)
            ?.commit();

    }

    private fun comprobar(): Boolean {
        fecha = edFecha.text.toString().trim()
        duracion = edDuracion.text.toString().trim()
        sinopsis = edSinopsis.text.toString().trim()
        trailer = edTrailer.text.toString().trim()

        if (fecha.isEmpty() || duracion.isEmpty() || sinopsis.isEmpty() || trailer.isEmpty()) {
            MotionToast.darkToast(
                activity as Activity,
                "Error",
                "Todos los campos son obligatorios",
                MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(context as Context, R.font.helvetica_regular)
            )

            return false
        }
        return true

    }
}