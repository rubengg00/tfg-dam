package com.example.tfg.descubrir.recomendaciones

class Recomendacion {

    var nomUsuario: String? = null
    var fotoUsuario: String? = null
    var caratula: String? = null
    var email: String? = null
    var titulo: String? = null
    var categoria: String? = null
    var fecha: String? = null
    var reseña: String? = null
    var emoticono: String? = null
    var fechaSubida: String? = null

    constructor() {}

    constructor(
        nomUsuario: String?,
        fotoUsuario: String?,
        caratula: String?,
        email: String?,
        titulo: String?,
        categoria: String?,
        fecha: String?,
        reseña: String?,
        emoticono: String?,
        fechaSubida: String?
    ) {
        this.nomUsuario = nomUsuario
        this.fotoUsuario = fotoUsuario
        this.caratula = caratula
        this.email = email
        this.titulo = titulo
        this.categoria = categoria
        this.fecha = fecha
        this.reseña = reseña
        this.emoticono = emoticono
        this.fechaSubida = fechaSubida
    }


}