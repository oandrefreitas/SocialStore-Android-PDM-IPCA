package com.example.lojasocial.models

data class Visita(
    val notas: String = "",
    val data: Long = System.currentTimeMillis(),
    val beneficiarioId: String = ""
)