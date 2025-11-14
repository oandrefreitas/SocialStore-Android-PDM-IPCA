package com.example.lojasocial.models

data class CashTransaction(
    var id: String = "",
    val amount: Double = 0.0,
    val description: String = "",
    val type: TransactionType = TransactionType.ENTRY,
    val timestamp: Long = System.currentTimeMillis(),
    val creatorUid: String = "",
)

enum class TransactionType {
    ENTRY, EXIT, ADJUSTMENT
}