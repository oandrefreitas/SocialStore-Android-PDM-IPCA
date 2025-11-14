package com.example.lojasocial.repository

import android.util.Log
import com.example.lojasocial.models.Beneficiario
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// Repositório para operações relacionadas aos beneficiários
object BeneficiarioRepository {

    private val db = Firebase.firestore // Instância do Firestore

    // Referência à coleção "Beneficiarios" no Firestore
    private val beneficiariosRef = db.collection("Beneficiarios")

    // Função para adicionar um beneficiário
    fun addBeneficiario(
        beneficiario: Beneficiario, // Objeto do beneficiário a ser adicionado
        onSuccess: () -> Unit, // Callback em caso de sucesso
        onFailure: (String) -> Unit // Callback em caso de falha
    ) {
        // Gera automaticamente o ID do documento
        val documentRef = beneficiariosRef.document()
        val id = documentRef.id

        // Cria uma cópia do objeto beneficiário com o ID gerado
        val beneficiarioComId = beneficiario.copy(id = id)

        // Salva o beneficiário no Firestore
        documentRef.set(beneficiarioComId)
            .addOnSuccessListener {
                // Log de sucesso
                Log.d("BeneficiarioRepository", "Beneficiário adicionado com sucesso: $beneficiarioComId")
                onSuccess() // Executa o callback de sucesso
            }
            .addOnFailureListener { e ->
                // Log de erro
                Log.e("BeneficiarioRepository", "Erro ao adicionar beneficiário: ${e.message}", e)
                onFailure(e.message ?: "Erro ao adicionar beneficiário.") // Executa o callback de erro
            }
    }

    // Função para recuperar todos os beneficiários
    fun getAllBeneficiarios(
        onSuccess: (List<Beneficiario>) -> Unit, // Callback com a lista de beneficiários
        onFailure: (String) -> Unit // Callback em caso de falha
    ) {
        beneficiariosRef.get() // Obtém todos os documentos da coleção "Beneficiarios"
            .addOnSuccessListener { result ->
                // Mapeia os documentos para objetos Beneficiario
                val beneficiarios = result.documents.mapNotNull { it.toObject(Beneficiario::class.java) }
                Log.d("BeneficiarioRepository", "Beneficiários recuperados com sucesso.")
                onSuccess(beneficiarios) // Executa o callback de sucesso
            }
            .addOnFailureListener { e ->
                // Log de erro
                Log.e("BeneficiarioRepository", "Erro ao obter beneficiários: ${e.message}", e)
                onFailure(e.message ?: "Erro ao obter beneficiários.") // Executa o callback de erro
            }
    }
}