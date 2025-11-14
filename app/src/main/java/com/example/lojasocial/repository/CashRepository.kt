package com.example.lojasocial.repository

import android.util.Log
import com.example.lojasocial.models.CashTransaction
import com.example.lojasocial.models.TransactionType
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar

// Repositório responsável pelas operações relacionadas ao fluxo de caixa (Cash)
object CashRepository {

    private val db = Firebase.firestore // Instância do Firestore

    // Referência à coleção "Cash" no Firestore
    private val cashRef = db.collection("Cash")

    // Função para adicionar uma nova transação
    fun addTransaction(
        transaction: CashTransaction, // Dados da transação
        onSuccess: () -> Unit, // Callback em caso de sucesso
        onFailure: (String) -> Unit // Callback em caso de falha
    ) {
        cashRef.add(transaction) // Adiciona a transação na coleção "Cash"
            .addOnSuccessListener {
                Log.d("CashRepository", "Transação adicionada com sucesso: $transaction")
                updateBalance(transaction, onSuccess, onFailure) // Atualiza o saldo após adicionar a transação
            }
            .addOnFailureListener { e ->
                Log.e("CashRepository", "Erro ao adicionar transação: ${e.message}", e)
                onFailure(e.message ?: "Erro ao adicionar transação.")
            }
    }

    // Função privada para atualizar o saldo com base na transação
    private fun updateBalance(
        transaction: CashTransaction, // Dados da transação
        onSuccess: () -> Unit, // Callback em caso de sucesso
        onFailure: (String) -> Unit // Callback em caso de falha
    ) {
        val balanceUpdate = when (transaction.type) {
            TransactionType.ENTRY, TransactionType.ADJUSTMENT -> transaction.amount // Incrementa o saldo
            TransactionType.EXIT -> -transaction.amount // Reduz o saldo
        }

        val balanceDoc = cashRef.document("balance") // Documento de saldo no Firestore

        balanceDoc.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Atualiza o saldo existente
                    balanceDoc.update("currentBalance", FieldValue.increment(balanceUpdate))
                        .addOnSuccessListener {
                            Log.d("CashRepository", "Saldo atualizado com sucesso.")
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            Log.e("CashRepository", "Erro ao atualizar saldo: ${e.message}", e)
                            onFailure(e.message ?: "Erro ao atualizar saldo.")
                        }
                } else {
                    // Cria o documento com o saldo inicial
                    balanceDoc.set(mapOf("currentBalance" to balanceUpdate))
                        .addOnSuccessListener {
                            Log.d("CashRepository", "Saldo inicial criado com sucesso.")
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            Log.e("CashRepository", "Erro ao criar saldo inicial: ${e.message}", e)
                            onFailure(e.message ?: "Erro ao criar saldo inicial.")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("CashRepository", "Erro ao verificar documento balance: ${e.message}", e)
                onFailure(e.message ?: "Erro ao verificar documento balance.")
            }
    }

    // Recupera o saldo atual
    fun getCurrentBalance(
        onSuccess: (Double) -> Unit, // Callback com o saldo atual
        onFailure: (String) -> Unit // Callback em caso de falha
    ) {
        cashRef.document("balance")
            .get()
            .addOnSuccessListener { document ->
                val balance = document.getDouble("currentBalance") ?: 0.0
                Log.d("CashRepository", "Saldo atual recuperado com sucesso: $balance")
                onSuccess(balance)
            }
            .addOnFailureListener { e ->
                Log.e("CashRepository", "Erro ao obter saldo: ${e.message}", e)
                onFailure(e.message ?: "Erro ao obter saldo.")
            }
    }

    // Recupera transações dentro de um intervalo de datas
    fun getTransactionsByDateRange(
        startDate: Long, // Data inicial
        endDate: Long, // Data final
        onSuccess: (List<CashTransaction>) -> Unit, // Callback com as transações
        onFailure: (String) -> Unit // Callback em caso de falha
    ) {
        val startOfStartDate = getStartOfDay(startDate)
        val startOfEndDate = getStartOfDay(endDate)

        // Se o intervalo de datas for para o mesmo dia
        if (startOfStartDate == startOfEndDate) {
            getTodayTransactions(startDate, onSuccess, onFailure)
            return
        }

        cashRef
            .whereGreaterThanOrEqualTo("timestamp", startDate)
            .whereLessThanOrEqualTo("timestamp", endDate)
            .get()
            .addOnSuccessListener { result ->
                val transactions = result.documents.mapNotNull {
                    it.toObject(CashTransaction::class.java)
                }
                onSuccess(transactions)
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Erro ao buscar transações.")
            }
    }

    // Recupera transações do dia atual
    fun getTodayTransactions(
        dayTimestamp: Long, // Timestamp do dia
        onSuccess: (List<CashTransaction>) -> Unit, // Callback com as transações
        onFailure: (String) -> Unit // Callback em caso de falha
    ) {
        val startOfDay = getStartOfDay(dayTimestamp)
        val endOfDay = getEndOfDay(dayTimestamp)

        cashRef
            .whereGreaterThanOrEqualTo("timestamp", startOfDay)
            .whereLessThanOrEqualTo("timestamp", endOfDay)
            .get()
            .addOnSuccessListener { result ->
                val transactions = result.documents.mapNotNull {
                    it.toObject(CashTransaction::class.java)
                }
                onSuccess(transactions)
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Erro ao buscar transações.")
            }
    }

    // Função para calcular o início do dia
    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    // Função para calcular o fim do dia
    private fun getEndOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
}