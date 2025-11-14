package com.example.lojasocial.models

data class Beneficiario(
    val id: String = "",
    val primeiroNome: String = "",
    val sobrenome: String = "",
    val telemovel: String = "",
    val referencia: String = "",
    val familia: Int?=null,
    val criancas: Int?=null,
    val nacionalidade: String = "",
    val isActive: Boolean = true,
)