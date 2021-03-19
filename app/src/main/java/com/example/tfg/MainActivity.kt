package com.example.tfg

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tfg.casa.HomeFragment
import com.example.tfg.descubrir.DescubrirFragment
import com.example.tfg.perfil.PerfilFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomBar.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navigation_home->{
                    val homeFragment = HomeFragment()
                    openFragment(homeFragment)
                    it.setChecked(true)
                    true
                }
                R.id.navigation_descubrir->{
                    val descubrirFragment = DescubrirFragment()
                    openFragment(descubrirFragment)
                    it.setChecked(true)
                    true
                }
                R.id.navigation_perfil->{
                    val perfilFragment = PerfilFragment()
                    openFragment(perfilFragment)
                    it.setChecked(true)
                    true
                }
            }
            false
        }

        if (savedInstanceState == null) {
            bottomBar.setSelectedItemId(R.id.navigation_home); // change to whichever id should be default
        }
    }

    private fun openFragment(fragment: Fragment){
        val transaction =  supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}