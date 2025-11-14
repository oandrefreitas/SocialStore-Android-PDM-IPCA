package com.example.lojasocial.repository

import android.util.Log
import com.example.lojasocial.models.Visita
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// Objeto que atua como repositório para manipular os dados da coleção "Visitas" no Firestore
object VisitaRepository {

    private val db = Firebase.firestore // Instância da base de dados Firestore

    // Referência à coleção "Visitas" na base de dados
    private val visitasRef = db.collection("Visitas")

    // Função para adicionar uma visita à coleção "Visitas"
    fun addVisita(
        visita: Visita, // Objeto de visita a ser adicionado
        onSuccess: () -> Unit, // Callback de sucesso
        onFailure: (String) -> Unit // Callback de erro
    ) {
        visitasRef.add(visita) // Adiciona o objeto visita ao Firestore
            .addOnSuccessListener {
                // Log em caso de sucesso
                Log.d("VisitaRepository", "Visita adicionada com sucesso: $visita")
                onSuccess() // Chama o callback de sucesso
            }
            .addOnFailureListener { e ->
                // Log em caso de falha
                Log.e("VisitaRepository", "Erro ao adicionar visita: ${e.message}", e)
                onFailure(e.message ?: "Erro ao adicionar visita.") // Chama o callback de erro
            }
    }

    // Função para recuperar todas as visitas da coleção
    fun getAllVisitas(
        onSuccess: (List<Visita>) -> Unit, // Callback com a lista de visitas recuperadas
        onFailure: (String) -> Unit // Callback em caso de erro
    ) {
        visitasRef.get() // Faz uma consulta à coleção "Visitas"
            .addOnSuccessListener { result ->
                // Mapeia os documentos para objetos do tipo `Visita`
                val visitas = result.documents.mapNotNull { it.toObject(Visita::class.java) }
                Log.d("VisitaRepository", "Visitas recuperadas com sucesso.")
                onSuccess(visitas) // Chama o callback de sucesso com a lista de visitas
            }
            .addOnFailureListener { e ->
                // Log em caso de erro
                Log.e("VisitaRepository", "Erro ao buscar visitas: ${e.message}", e)
                onFailure(e.message ?: "Erro ao buscar visitas.") // Chama o callback de erro
            }
    }

    // Função para recuperar visitas associadas a um beneficiário específico
    fun getVisitasByBeneficiarioId(
        beneficiarioId: String, // ID do beneficiário
        onSuccess: (List<Visita>) -> Unit, // Callback com a lista de visitas
        onFailure: (String) -> Unit // Callback em caso de erro
    ) {
        visitasRef.whereEqualTo("beneficiarioId", beneficiarioId) // Filtra pela propriedade "beneficiarioId"
            .get()
            .addOnSuccessListener { result ->
                // Mapeia os documentos para objetos do tipo `Visita`
                val visitas = result.documents.mapNotNull { it.toObject(Visita::class.java) }
                Log.d("VisitaRepository", "Visitas para o beneficiário $beneficiarioId recuperadas com sucesso.")
                onSuccess(visitas) // Chama o callback de sucesso
            }
            .addOnFailureListener { e ->
                // Log em caso de erro
                Log.e("VisitaRepository", "Erro ao buscar visitas por beneficiário: ${e.message}", e)
                onFailure(e.message ?: "Erro ao buscar visitas por beneficiário.") // Chama o callback de erro
            }
    }
}