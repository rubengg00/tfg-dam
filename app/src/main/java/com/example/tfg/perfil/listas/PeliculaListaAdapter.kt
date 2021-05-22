package com.example.tfg.perfil.listas

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.example.tfg.R
import com.example.tfg.pelicula.Pelicula
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class PeliculaListaAdapter(
    private val miLista: ArrayList<Pelicula>,
    val c: Context,
    val listaNom: String
) :
    RecyclerView.Adapter<PeliculaListaAdapter.MiViewHolder>(), View.OnClickListener {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    //Listener
    lateinit var listener: View.OnClickListener

    class MiViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvTitulo: TextView = v.findViewById(R.id.tvTitMov)
        val tvFecha: TextView = v.findViewById(R.id.tvFechaMov)
        val ivCaratula: ImageView = v.findViewById(R.id.ivMov)
        val tvOpciones: TextView = v.findViewById(R.id.tvOpciones)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val v = inflate.inflate(R.layout.custom_grid_movie_list_layout, parent, false)
        v.setOnClickListener(this)
        return MiViewHolder(v)
    }

    override fun getItemCount(): Int {
        return miLista.size
    }

    override fun onBindViewHolder(holder: MiViewHolder, position: Int) {
        val item = miLista[position]
        holder.tvTitulo.text = item.titulo
        holder.tvFecha.text = item.fecha
        Picasso.get().load(item.caratula).into(holder.ivCaratula)
        holder.tvOpciones.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                //Creamos el menu Popup
                var popupMenu: PopupMenu = PopupMenu(c, holder.tvOpciones)
                //Inflamos el menu con el recurso XML
                popupMenu.inflate(R.menu.options_menu)
                //Añadimos un evento Click
                popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem?): Boolean {
                        when (item?.itemId) {
                            R.id.menu1 -> {
                                MaterialDialog(c).show {
                                    title(null, "Eliminar de la lista")
                                    message(null, "¿Deseas eliminar la película de la lista?")
                                    negativeButton(R.string.opcion_positivia) { dialog ->
                                        db.collection("usuarios")
                                            .document(FirebaseAuth.getInstance().currentUser.email)
                                            .collection("listas").document(listaNom)
                                            .collection("peliculas")
                                            .document(holder.tvTitulo.text.toString()).delete()

                                        if (v != null) {
                                            Snackbar.make(
                                                v,
                                                "Borrado de ${listaNom}",
                                                Snackbar.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    positiveButton(R.string.opcion_negativa){dialog->{
                                        dialog.dismiss()
                                    }}
                                }

                            }
                        }
                        return false
                    }

                })
                popupMenu.show()
            }
        })


    }

    fun setOnClickListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    override fun onClick(v: View?) {
        if (listener != null) {
            listener.onClick(v)
        }
    }

}