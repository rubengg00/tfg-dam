package com.example.tfg.pelicula.recomendaciones

data class Recomendacion(
    var nomUsuario: String,
    var fotoUsuario: String,
    var caratula: String,
    var titulo: String,
    var categoria: String,
    var fecha: String,
    var reseña: String,
    var emoticono: String
)