package com.example.tfg.perfil.admin.recomendaciones

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.example.tfg.R
import com.example.tfg.perfil.admin.categorias.AllCategoriasFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*

class EditRecFragment : Fragment() {

    lateinit var tvNom: TextView
    lateinit var edUserComent: EditText
    lateinit var edComenUser: EditText
    lateinit var edFechaSubida: EditText
    lateinit var btnBorrar: Button
    private val nodoRaiz = FirebaseDatabase.getInstance()
    var titulo = ""
    var fecha = ""
    var categoria = ""
    var resena = ""
    var nomusu = ""
    var subida = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_edit_rec, container, false)

        var datos = this.arguments

        titulo = datos?.getString("titulo").toString()
        fecha = datos?.getString("fecha").toString()
        categoria = datos?.getString("categoria").toString()
        resena = datos?.getString("reseña").toString()
        nomusu = datos?.getString("nomusu").toString()
        subida = datos?.getString("subida").toString()

        tvNom = root.findViewById(R.id.tvNomPeliRese)
        edUserComent = root.findViewById(R.id.edUserComent)
        edComenUser = root.findViewById(R.id.edComenUser)
        edFechaSubida = root.findViewById(R.id.edFechaSubida)
        btnBorrar = root.findViewById(R.id.btnDeleteRec)

        edUserComent.setText(nomusu)
        edComenUser.setText(resena)
        edFechaSubida.setText(subida)
        tvNom.text = titulo

        btnBorrar.setOnClickListener {
            borrarReseña()
        }

        return root
    }

    private fun borrarReseña() {
        MaterialDialog(context as Context).show {
            title(null, "Eliminar reseña")
            message(
                null,
                "¿Deseas eliminar esta reseña de la plataforma?"
            )
            negativeButton(R.string.opcion_positivia) {
                var clave = ""
                var query: Query =
                    FirebaseDatabase.getInstance().getReference("recomendaciones")
                        .orderByChild("fechaSubida").equalTo(edFechaSubida.text.toString())
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

                getView()?.let { it1 ->
                    Snackbar.make(
                        it1,
                        "¡Reseña borrada!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                val allReseñas = AllRecFragment()

                var bundle: Bundle = Bundle()
                allReseñas.arguments = bundle

                activity?.getSupportFragmentManager()?.beginTransaction()
                    ?.setCustomAnimations(
                        R.anim.slide_bottom_up,
                        R.anim.slide_bottom_down
                    )
                    ?.replace(R.id.container, allReseñas)
                    ?.addToBackStack(null)
                    ?.commit();
            }
            positiveButton(R.string.opcion_negativa) { dialog ->
                {
                    dialog.dismiss()
                }
            }
        }
    }

}