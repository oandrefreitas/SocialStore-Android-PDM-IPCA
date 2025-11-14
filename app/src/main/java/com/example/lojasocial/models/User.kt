package com.example.lojasocial.models

import com.google.firebase.firestore.PropertyName

data class User(
    var id: String = "",
    val name: String = "",
    val email: String? = null,
    val phone: String? = null,
    var accessLevel: Int = 2,
    @get:PropertyName("isActive") @set:PropertyName("isActive") //Sem este campo a criação no Firestore era como "Active
    var isActive: Boolean = false
)