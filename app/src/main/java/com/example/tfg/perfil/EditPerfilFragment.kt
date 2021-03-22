package com.example.tfg.perfil

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.tfg.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_edit_perfil.*


class EditPerfilFragment : Fragment(), View.OnClickListener {
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_edit_perfil, container, false)

        val btnLogOut : Button = root.findViewById(R.id.btnLogOut)
        btnLogOut.setOnClickListener(this)

        return root
    }

    override fun onClick(v: View?) {
        when(v){
            btnLogOut->{
                logOut()
            }
        }
    }

    private fun logOut() {
        FirebaseAuth.getInstance().signOut()
        val perfilFragment = PerfilFragment()
        activity?.getSupportFragmentManager()?.beginTransaction()
            ?.replace(R.id.container,perfilFragment)
            ?.addToBackStack(null)
            ?.commit();
    }
}