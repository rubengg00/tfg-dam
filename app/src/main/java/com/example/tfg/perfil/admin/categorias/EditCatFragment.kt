package com.example.tfg.perfil.admin.categorias

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
import com.example.tfg.perfil.admin.peliculas.AllPeliculasFragment
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import www.sanju.motiontoast.MotionToast

class EditCatFragment : Fragment() {

    lateinit var tvNom: TextView
    lateinit var tvEmoji: TextView
    lateinit var edNom: EditText
    lateinit var edEmoji: EditText
    lateinit var btnSave: Button
    lateinit var btnDel: Button
    private val db = FirebaseFirestore.getInstance()
    private val nodoRaiz = FirebaseDatabase.getInstance()
    var nombre = ""
    var emoticono = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit_cat, container, false)

        var datos = this.arguments

        tvNom = root.findViewById(R.id.tvNomCat)
        tvEmoji = root.findViewById(R.id.tvEmoCat)
        edNom = root.findViewById(R.id.edNomCat)
        edNom.setOnKeyListener(null)
        edEmoji = root.findViewById(R.id.edEmoCat)
        btnSave = root.findViewById(R.id.btnSaveCat)
        btnDel = root.findViewById(R.id.btnDelCat)

        tvNom.text = datos?.getString("titulo")
        tvEmoji.text = datos?.getString("emoji")
        edNom.setText(datos?.getString("titulo"))
        edEmoji.setText(datos?.getString("emoji"))

        btnSave.setOnClickListener { modificarCat() }
        btnDel.setOnClickListener {
            MaterialDialog(context as Context).show {
                title(null, "Eliminar categoría")
                message(
                    null,
                    "¿Deseas eliminar la categoría ${tvNom.text} de la plataforma? Borrará todas las películas relacionadas a esta también."
                )
                negativeButton(R.string.opcion_positivia) {
                    borrarCat()
                }
                positiveButton(R.string.opcion_negativa) {
                    dismiss()
                }
            }
        }

        return root
    }

    private fun borrarCat() {
        var correo = ""
        var lista = ""
        var titulo = ""


        db.collection("categorias").document(tvNom.text.toString()).delete()

        var query: Query =
            FirebaseDatabase.getInstance().getReference("recomendaciones")
                .orderByChild("categoria").equalTo(tvNom.text.toString())

        query.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (resultado in snapshot.getChildren()) {
                    resultado.ref.removeValue()
                }
            }
        })

        MotionToast.darkToast(
            activity as Activity,
            "Categoría borrada 👍",
            "Categoría borrada correctamente",
            MotionToast.TOAST_SUCCESS,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(context as Context, R.font.helvetica_regular)
        )

        val allCategorias = AllCategoriasFragment()

        var bundle: Bundle = Bundle()
        allCategorias.arguments = bundle

        activity?.getSupportFragmentManager()?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_bottom_up,
                R.anim.slide_bottom_down
            )
            ?.replace(R.id.container, allCategorias)
            ?.addToBackStack(null)
            ?.commit();
    }

    private fun modificarCat() {
        if (!comprobar()) return

        db.collection("categorias").document(tvNom.text.toString())
            .update(
                mapOf(
                    "logo" to edEmoji.text.toString().trim()
                )
            )

        MotionToast.darkToast(
            activity as Activity,
            "Categoría actualizada 👍",
            "Categoría actualizada correctamente",
            MotionToast.TOAST_SUCCESS,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(context as Context, R.font.helvetica_regular)
        )

        val allCategorias = AllCategoriasFragment()

        var bundle: Bundle = Bundle()
        allCategorias.arguments = bundle

        activity?.getSupportFragmentManager()?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_bottom_up,
                R.anim.slide_bottom_down
            )
            ?.replace(R.id.container, allCategorias)
            ?.addToBackStack(null)
            ?.commit();

    }

    private fun comprobar(): Boolean {
        nombre = edNom.text.toString().trim()
        emoticono = edEmoji.text.toString().trim()

        if (nombre.isEmpty() || emoticono.isEmpty()) {
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