package com.example.tfg.perfil

import android.content.Context
import android.preference.PreferenceManager

class MisPreferencias(context: Context?) {

    companion object {
        private const val MODO_OSCURO = "modo_oscuro"
    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    var darkMode = preferences.getInt(MODO_OSCURO, 0)
        set(value) = preferences.edit().putInt(MODO_OSCURO, value).apply()
}

