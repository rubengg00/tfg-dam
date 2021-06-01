package com.example.tfg.pelicula.recomendaciones

data class Recomendacion(
    var nomUsuario: String? = null,
    var fotoUsuario: String? = null,
    var email: String? = null,
    var caratula: String? = null,
    var titulo: String? = null,
    var categoria: String? = null,
    var fecha: String? = null,
    var reseña: String? = null,
    var emoticono: String? = null,
    var fechaSubida: String? = null
)