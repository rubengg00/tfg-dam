package com.example.tfg.pelicula

import kotlin.math.sin

class Pelicula {

    var titulo: String? = null
    var fecha: String? = null
    var sinopsis: String? = null
    var duracion: String? = null
    var categoria: String? = null
    var caratula: String? = null
    var platNombre: String? = null
    var enlace: String? = null
    var trailer: String? = null

    constructor() {}

    constructor(
        titulo: String?,
        fecha: String?,
        sinopsis: String?,
        duracion: String?,
        categoria: String?,
        caratula: String?,
        platNombre: String,
        enlace: String?,
        trailer: String?
    ) {
        this.titulo = titulo
        this.fecha = fecha
        this.sinopsis = sinopsis
        this.duracion = duracion
        this.categoria = categoria
        this.caratula = caratula
        this.platNombre = platNombre
        this.enlace = enlace
        this.trailer = trailer
    }
}